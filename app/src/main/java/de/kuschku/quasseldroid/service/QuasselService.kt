/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.SpannableString
import androidx.core.app.RemoteInput
import androidx.lifecycle.Observer
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.HostnameVerifier
import de.kuschku.libquassel.connection.SocketAddress
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.quassel.syncables.BufferSyncer
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.session.manager.ConnectionInfo
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.INFO
import de.kuschku.libquassel.util.flag.Flags
import de.kuschku.libquassel.util.flag.minus
import de.kuschku.libquassel.util.helper.clampOf
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.getOr
import de.kuschku.libquassel.util.helper.value
import de.kuschku.malheur.CrashHandler
import de.kuschku.quasseldroid.Backend
import de.kuschku.quasseldroid.BuildConfig
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.defaults.Defaults
import de.kuschku.quasseldroid.persistence.dao.buffers
import de.kuschku.quasseldroid.persistence.dao.clear
import de.kuschku.quasseldroid.persistence.dao.markHidden
import de.kuschku.quasseldroid.persistence.dao.markReadNormal
import de.kuschku.quasseldroid.persistence.db.AccountDatabase
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.persistence.util.QuasselBacklogStorage
import de.kuschku.quasseldroid.settings.ConnectionSettings
import de.kuschku.quasseldroid.settings.NotificationSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ssl.QuasselHostnameVerifier
import de.kuschku.quasseldroid.ssl.QuasselTrustManager
import de.kuschku.quasseldroid.ssl.custom.QuasselCertificateManager
import de.kuschku.quasseldroid.ssl.custom.QuasselHostnameManager
import de.kuschku.quasseldroid.util.backport.DaggerLifecycleService
import de.kuschku.quasseldroid.util.compatibility.AndroidHandlerService
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.IrcFormatSerializer
import de.kuschku.quasseldroid.util.ui.LocaleHelper
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.Instant
import javax.inject.Inject
import javax.net.ssl.X509TrustManager

class QuasselService : DaggerLifecycleService(),
                       SharedPreferences.OnSharedPreferenceChangeListener {
  @Inject
  lateinit var connectionSettings: ConnectionSettings

  @Inject
  lateinit var notificationSettings: NotificationSettings

  private lateinit var translatedLocale: Context

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    update()
    translatedLocale = LocaleHelper.setLocale(this)
    notificationBackend.updateSettings()
  }

  private fun update() {
    this.notificationSettings = Settings.notification(this)
    val connectionSettings = Settings.connection(this)

    if (this.connectionSettings.showNotification != connectionSettings.showNotification) {
      updateNotificationStatus(this.progress)
    }
    this.connectionSettings = connectionSettings

    val (accountId, reconnect) = this.sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      Pair(
        getLong(Keys.Status.selectedAccount, -1),
        getBoolean(Keys.Status.reconnect, false)
      )
    }
    if (this.accountId != accountId || this.reconnect != reconnect) {
      this.accountId = accountId
      this.reconnect = reconnect

      handlerService.backend {
        val account = if (accountId != -1L && reconnect) {
          accountDatabase.accounts().findById(accountId)
        } else {
          null
        }

        if (account == null) {
          backendImplementation.disconnect(true)
          stopSelf()
        } else {
          backendImplementation.autoConnect(
            connectionInfo = Backend.ConnectionInfo(
              SocketAddress(account.host, account.port),
              account.user,
              account.pass,
              account.requireSsl,
              true
            )
          )
        }
      }
    } else if (accountId == -1L || !reconnect) {
      handlerService.backend {
        backendImplementation.disconnect(true)
        stopSelf()
      }
    }
  }

  private var accountId: Long = -1
    set(value) {
      field = value
      liveAccountId.onNext(value)
    }
  private var liveAccountId = BehaviorSubject.createDefault(-1L)
  private var reconnect: Boolean = false

  @Inject
  lateinit var notificationManager: QuasseldroidNotificationManager

  @Inject
  lateinit var notificationBackend: QuasselNotificationBackend

  @Inject
  lateinit var ircFormatSerializer: IrcFormatSerializer

  private var notificationHandle: QuasseldroidNotificationManager.Handle? = null
  private var progress = Triple(ConnectionState.DISCONNECTED, 0, 0)

  private fun updateNotificationStatus(rawProgress: Triple<ConnectionState, Int, Int>) {
    if (connectionSettings.showNotification) {
      val notificationHandle = notificationManager.notificationBackground()
      this.notificationHandle = notificationHandle
      updateNotification(notificationHandle, rawProgress)
      startForeground(notificationHandle.id, notificationHandle.builder.build())
    } else {
      this.notificationHandle = null
      stopForeground(true)
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val result = super.onStartCommand(intent, flags, startId)
    intent?.let(this@QuasselService::handleIntent)
    return result
  }

  private fun handleIntent(intent: Intent) {
    if (intent.getBooleanExtra("disconnect", false)) {
      sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
        editApply {
          putBoolean(Keys.Status.reconnect, false)
        }
      }
    }

    val bufferId = BufferId(intent.getIntExtra("bufferId", -1))

    val inputResults = RemoteInput.getResultsFromIntent(intent)?.getCharSequence("reply_content")
    if (inputResults != null && bufferId.isValidId()) {
      if (inputResults.isNotBlank()) {
        val lines = inputResults.lineSequence().map {
          it.toString() to ircFormatSerializer.toEscapeCodes(SpannableString(it))
        }

        sessionManager.connectedSession.value?.let { session ->
          session.bufferSyncer.bufferInfo(bufferId)?.also { bufferInfo ->
            val output = mutableListOf<IAliasManager.Command>()
            for ((_, formatted) in lines) {
              session.aliasManager.processInput(bufferInfo, formatted, output)
            }
            for (command in output) {
              session.rpcHandler.sendInput(command.buffer, command.message)
            }
          }
          handlerService.backend {
            notificationBackend.showConnectedNotifications()
          }
        }
      }
    } else {
      val clearMessageId = MsgId(intent.getLongExtra("mark_read_message", -1))
      if (bufferId.isValidId() && clearMessageId.isValidId()) {
        sessionManager.connectedSession.value?.bufferSyncer?.requestSetLastSeenMsg(bufferId,
                                                                                   clearMessageId)
        sessionManager.connectedSession.value?.bufferSyncer?.requestMarkBufferAsRead(bufferId)
      }

      val hideMessageId = MsgId(intent.getLongExtra("hide_message", -1))
      if (bufferId.isValidId() && hideMessageId.isValidId()) {
        if (notificationSettings.markReadOnSwipe) {
          sessionManager.connectedSession.value?.bufferSyncer?.requestSetLastSeenMsg(bufferId,
                                                                                     hideMessageId)
          sessionManager.connectedSession.value?.bufferSyncer?.requestMarkBufferAsRead(bufferId)
        } else {
          handlerService.backend {
            database.notifications().markHidden(bufferId, hideMessageId)
          }
        }
      }
    }
  }

  private fun updateNotification(handle: QuasseldroidNotificationManager.Handle,
                                 rawProgress: Triple<ConnectionState, Int, Int>) {
    val (state, progress, max) = rawProgress
    when (state) {
      ConnectionState.DISCONNECTED -> {
        handle.builder.setContentTitle(getString(R.string.label_status_disconnected))
        handle.builder.setProgress(0, 0, true)
      }
      ConnectionState.CONNECTING   -> {
        handle.builder.setContentTitle(getString(R.string.label_status_connecting))
        handle.builder.setProgress(max, progress, true)
      }
      ConnectionState.HANDSHAKE    -> {
        handle.builder.setContentTitle(getString(R.string.label_status_handshake))
        handle.builder.setProgress(max, progress, true)
      }
      ConnectionState.INIT         -> {
        handle.builder.setContentTitle(getString(R.string.label_status_init))
        // Show indeterminate when no progress has been made yet
        handle.builder.setProgress(max, progress, progress == 0 || max == 0)
      }
      ConnectionState.CONNECTED    -> {
        handle.builder.setContentTitle(getString(R.string.label_status_connected))
        handle.builder.setProgress(0, 0, false)
      }
      ConnectionState.CLOSED       -> {
        handle.builder.setContentTitle(getString(R.string.label_status_closed))
        handle.builder.setProgress(0, 0, false)
      }
    }
  }

  private lateinit var sessionManager: SessionManager

  private lateinit var clientData: ClientData

  private lateinit var trustManager: X509TrustManager

  private lateinit var hostnameVerifier: HostnameVerifier

  private lateinit var certificateManager: QuasselCertificateManager

  class BackendImplementation : Backend {
    var service: QuasselService? = null

    override fun updateUserDataAndLogin(user: String, pass: String) {
      service?.apply {
        accountDatabase.accounts().findById(accountId)?.let { old ->
          accountDatabase.accounts().save(old.copy(user = user, pass = pass))
          sessionManager.login(user, pass)
        }
      }
    }

    override fun sessionManager() = service?.sessionManager

    override fun autoConnect(
      ignoreConnectionState: Boolean,
      ignoreSetting: Boolean,
      ignoreErrors: Boolean,
      connectionInfo: Backend.ConnectionInfo?
    ) {
      service?.apply {
        if (connectionInfo != null) {
          sessionManager.autoConnect(
            ignoreConnectionState,
            ignoreSetting,
            ignoreErrors,
            ConnectionInfo(
              clientData = clientData,
              trustManager = trustManager,
              hostnameVerifier = hostnameVerifier,
              address = connectionInfo.address,
              userData = Pair(connectionInfo.username,
                              connectionInfo.password),
              requireSsl = connectionInfo.requireSsl,
              shouldReconnect = connectionInfo.shouldReconnect
            )
          )
        } else {
          sessionManager.autoConnect(ignoreConnectionState, ignoreSetting, ignoreErrors)
        }
      }
    }

    override fun disconnect(forever: Boolean) {
      service?.apply {
        sessionManager.disconnect(forever)
      }
    }

    override fun requestConnectNewNetwork() {
      service?.apply {
        sessionManager.connectedSession.flatMap(ISession::liveNetworkAdded).firstElement().flatMap { id ->
          sessionManager.connectedSession.flatMap(ISession::liveNetworks)
            .map { it[id] }
            .flatMap { network ->
              network.liveInitialized
                .filter { it }
                .map { network }
            }.firstElement()
        }.toLiveData().observe(this, Observer {
          it?.requestConnect()
        })
      }
    }

    override fun setCurrentBuffer(id: BufferId) {
      service?.currentBuffer?.onNext(id)
    }
  }

  private val backendImplementation = BackendImplementation()

  private val handlerService = AndroidHandlerService()

  private val asyncBackend = AsyncBackend(handlerService, backendImplementation)

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var accountDatabase: AccountDatabase

  lateinit var currentBuffer: BehaviorSubject<BufferId>

  private fun disconnectFromCore() {
    getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE).editCommit {
      putBoolean(Keys.Status.reconnect, false)
    }
  }

  override fun onCreate() {
    super.onCreate()

    backendImplementation.service = this
    asyncBackend.setDisconnectCallback(::stopSelf)

    translatedLocale = LocaleHelper.setLocale(this)

    certificateManager = QuasselCertificateManager(database.validityWhitelist())
    hostnameVerifier = QuasselHostnameVerifier(QuasselHostnameManager(database.hostnameWhitelist()))
    trustManager = QuasselTrustManager(certificateManager)

    val backlogStorage = QuasselBacklogStorage(database)
    currentBuffer = backlogStorage.currentBuffer
    sessionManager = SessionManager(
      ISession.NULL,
      backlogStorage,
      notificationBackend,
      handlerService,
      ::AndroidHeartBeatRunner,
      CrashHandler::handle
    )

    sessionManager.setDisconnectFromCore(::disconnectFromCore)
    sessionManager.setInitCallback(::initCallback)

    clientData = ClientData(
      identifier = "${resources.getString(R.string.app_name)} ${BuildConfig.FANCY_VERSION_NAME}",
      buildDate = Instant.ofEpochSecond(BuildConfig.GIT_COMMIT_DATE),
      clientFeatures = QuasselFeatures.all(),
      protocolFeatures = Protocol_Features.of(
        Protocol_Feature.Compression,
        Protocol_Feature.TLS
      ),
      supportedProtocols = listOf(Protocol.Datastream)
    )

    sessionManager.connectionProgress.toLiveData().observe(this, Observer {
      if (this.progress.first != it?.first && it?.first == ConnectionState.CONNECTED) {
        handlerService.backend {
          database.message().clearMessages()
        }
      }
      val rawProgress = it ?: Triple(ConnectionState.DISCONNECTED, 0, 0)
      this.progress = rawProgress
      val handle = this.notificationHandle
      if (handle != null) {
        updateNotification(handle, rawProgress)
        notificationManager.notify(handle)
      }
    })

    var buffersWithNewActivity = emptySet<BufferId>()
    combineLatest(
      liveAccountId.switchMap { database.filtered().listenRx(it).toObservable() },
      sessionManager.connectedSession
        .map(ISession::bufferSyncer)
        .switchMap(BufferSyncer::liveActivities)
    ).map { (filteredList, bufferActivities) ->
      val filtered = filteredList.map {
        Pair(it.bufferId, it.filtered)
      }.toMap()

      bufferActivities.mapValues { (bufferId, activities) ->
        activities.minus(filtered.getOr(bufferId, 0).toUInt())
      }.filterValues(Flags<Message_Type>::isNotEmpty).keys
    }.map {
      val newlyChangedBuffers = it - buffersWithNewActivity
      buffersWithNewActivity = it
      newlyChangedBuffers
    }.map {
      val bufferSyncer = sessionManager.connectedSession.value?.bufferSyncer
      Pair(bufferSyncer, it.mapNotNull {
        bufferSyncer?.bufferInfo(it)
      })
    }.toLiveData().observe(this, Observer { (bufferSyncer, bufferInfos) ->
      val bufferViewManager = sessionManager.connectedSession.value?.bufferViewManager
      if (bufferSyncer != null && bufferViewManager != null) {
        for (bufferInfo in bufferInfos) {
          bufferViewManager.handleBuffer(bufferInfo, bufferSyncer, unhide = true)
        }
      }
    })

    ReactiveNetwork
      .observeNetworkConnectivity(applicationContext)
      .toLiveData()
      .observe(this, Observer { connectivity ->
        if (!connectionSettings.ignoreNetworkChanges) {
          log(INFO, "QuasselService", "Connectivity changed: $connectivity")
          handlerService.backend {
            log(INFO, "QuasselService", "Reconnect triggered: Network changed")
            sessionManager.autoConnect(
              ignoreConnectionState = true,
              ignoreSetting = true
            )
          }
        }
      })

    sessionManager.state
      .distinctUntilChanged()
      .toLiveData()
      .observe(
        this, Observer {
        handlerService.backend {
          if (it == ConnectionState.HANDSHAKE) {
            backoff = BACKOFF_MIN
          }

          if (it == ConnectionState.CLOSED) {
            scheduleReconnect()
            notificationBackend.showDisconnectedNotifications()
          }
        }
      })

    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      registerOnSharedPreferenceChangeListener(this@QuasselService)
    }
    sharedPreferences {
      registerOnSharedPreferenceChangeListener(this@QuasselService)
    }

    notificationManager.init()

    update()
    updateNotificationStatus(this.progress)
  }

  private var backoff = BACKOFF_MIN
  private var scheduled = false
  private fun scheduleReconnect() {
    if (!scheduled && sessionManager.canAutoReconnect(ignoreSetting = true)) {
      log(INFO, "QuasselService", "Reconnect: Scheduling backoff in ${backoff / 1_000} seconds")
      scheduled = true
      handlerService.backendDelayed(backoff) {
        log(INFO, "QuasselService", "Reconnect: Scheduled backoff happened")
        scheduled = false

        backoff = clampOf(backoff * 2, BACKOFF_MIN, BACKOFF_MAX)
        sessionManager.autoConnect(ignoreSetting = true)
      }
    }
  }

  override fun onDestroy() {
    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      unregisterOnSharedPreferenceChangeListener(this@QuasselService)
    }
    sharedPreferences {
      unregisterOnSharedPreferenceChangeListener(this@QuasselService)
    }

    sessionManager.dispose()
    asyncBackend.setDisconnectCallback(null)
    backendImplementation.service = null

    notificationHandle?.let { notificationManager.remove(it) }
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): QuasselBinder {
    super.onBind(intent)
    return QuasselBinder(asyncBackend)
  }

  private fun initCallback(session: Session) {
    if (session.bufferViewManager.bufferViewConfigs().isEmpty()) {
      session.bufferViewManager.requestCreateBufferView(
        Defaults.bufferViewConfigInitial(translatedLocale).apply {
          for (info in session.bufferSyncer.bufferInfos()) {
            handleBuffer(info, session.bufferSyncer)
          }
        }.toVariantMap()
      )
    }

    // Cleanup deleted buffers from cache

    val buffers = session.bufferSyncer.bufferInfos().map(BufferInfo::bufferId)

    val deletedBuffersMessage = database.message().buffers().toSet() - buffers
    log(INFO, "QuasselService", "Buffers deleted from message storage: $deletedBuffersMessage")
    for (deletedBuffer in deletedBuffersMessage) {
      database.message().clearMessages(deletedBuffer.id)
    }

    val deletedBuffersFiltered = database.filtered().buffers(accountId).toSet() - buffers
    log(INFO, "QuasselService", "Buffers deleted from filtered storage: $deletedBuffersFiltered")
    for (deletedBuffer in deletedBuffersFiltered) {
      database.filtered().clear(accountId, deletedBuffer)
    }

    val deletedBuffersNotifications = database.notifications().buffers().toSet() - buffers
    log(INFO,
        "QuasselService",
        "Buffers deleted from notification storage: $deletedBuffersNotifications")
    for (deletedBuffer in deletedBuffersNotifications) {
      database.notifications().markReadNormal(deletedBuffer)
    }
  }

  companion object {
    // default backoff is 5 seconds
    const val BACKOFF_MIN = 5_000L
    // max is 30 minutes
    const val BACKOFF_MAX = 1_800_000L

    fun launch(
      context: Context,
      disconnect: Boolean? = null,
      markRead: BufferId? = null,
      markReadMessage: MsgId? = null,
      hideMessage: MsgId? = null
    ): ComponentName? = context.startService(
      intent(context, disconnect, markRead, markReadMessage, hideMessage)
    )

    fun intent(
      context: Context,
      disconnect: Boolean? = null,
      bufferId: BufferId? = null,
      markReadMessage: MsgId? = null,
      hideMessage: MsgId? = null
    ) = Intent(context, QuasselService::class.java).apply {
      if (disconnect != null) {
        putExtra("disconnect", disconnect)
      }
      if (bufferId != null) {
        putExtra("bufferId", bufferId.id)
      }
      if (markReadMessage != null) {
        putExtra("mark_read_message", markReadMessage.id)
      }
      if (hideMessage != null) {
        putExtra("hide_message", hideMessage.id)
      }
    }
  }
}
