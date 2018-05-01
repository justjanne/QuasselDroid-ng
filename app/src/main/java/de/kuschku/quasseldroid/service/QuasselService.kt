/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.service

import android.arch.lifecycle.Observer
import android.content.*
import android.net.ConnectivityManager
import android.support.v4.app.RemoteInput
import android.text.SpannableString
import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.HostnameVerifier
import de.kuschku.libquassel.connection.SocketAddress
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.malheur.CrashHandler
import de.kuschku.quasseldroid.BuildConfig
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.AccountDatabase
import de.kuschku.quasseldroid.persistence.QuasselBacklogStorage
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.ConnectionSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ssl.QuasselHostnameVerifier
import de.kuschku.quasseldroid.ssl.QuasselTrustManager
import de.kuschku.quasseldroid.ssl.custom.QuasselCertificateManager
import de.kuschku.quasseldroid.ssl.custom.QuasselHostnameManager
import de.kuschku.quasseldroid.util.backport.DaggerLifecycleService
import de.kuschku.quasseldroid.util.compatibility.AndroidHandlerService
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.irc.format.IrcFormatSerializer
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.X509TrustManager

class QuasselService : DaggerLifecycleService(),
                       SharedPreferences.OnSharedPreferenceChangeListener {
  @Inject
  lateinit var connectionSettings: ConnectionSettings

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    update()
    notificationBackend.updateSettings()
  }

  private fun update() {
    val connectionSettings = Settings.connection(this)
    if (this.connectionSettings.showNotification != connectionSettings.showNotification) {
      this.connectionSettings = connectionSettings

      updateNotificationStatus(this.progress)
    }

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
          backendImplementation.connectUnlessConnected(
            SocketAddress(account.host, account.port),
            account.user,
            account.pass,
            true
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

    val bufferId = intent.getIntExtra("buffer", -1)

    val inputResults = RemoteInput.getResultsFromIntent(intent)?.getCharSequence("reply_content")
    if (inputResults != null && bufferId != -1) {
      if (inputResults.isNotBlank()) {
        val lines = inputResults.lineSequence().map {
          it.toString() to ircFormatSerializer.toEscapeCodes(SpannableString(it))
        }

        sessionManager.session.value?.let { session ->
          session.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
            val output = mutableListOf<IAliasManager.Command>()
            for ((_, formatted) in lines) {
              session.aliasManager?.processInput(bufferInfo, formatted, output)
            }
            for (command in output) {
              session.rpcHandler?.sendInput(command.buffer, command.message)
            }
          }
        }
      }
    }

    val clearMessageId = intent.getLongExtra("mark_read_message", -1)
    if (bufferId != -1 && clearMessageId != -1L) {
      sessionManager.session.value?.bufferSyncer?.requestSetLastSeenMsg(bufferId, clearMessageId)
      sessionManager.session.value?.bufferSyncer?.requestMarkBufferAsRead(bufferId)
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

  private fun updateConnection(accountId: Long, reconnect: Boolean) {
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
        backendImplementation.connectUnlessConnected(
          SocketAddress(account.host, account.port),
          account.user,
          account.pass,
          true
        )
      }
    }
  }

  private lateinit var sessionManager: SessionManager

  private lateinit var clientData: ClientData

  private lateinit var trustManager: X509TrustManager

  private lateinit var hostnameVerifier: HostnameVerifier

  private lateinit var certificateManager: QuasselCertificateManager

  private val backendImplementation = object : Backend {
    override fun updateUserDataAndLogin(user: String, pass: String) {
      accountDatabase.accounts().findById(accountId)?.let { old ->
        accountDatabase.accounts().save(old.copy(user = user, pass = pass))
        sessionManager.login(user, pass)
      }
    }

    override fun sessionManager() = sessionManager

    override fun connectUnlessConnected(address: SocketAddress, user: String, pass: String,
                                        reconnect: Boolean) {
      sessionManager.ifDisconnected {
        this.connect(address, user, pass, reconnect)
      }
    }

    override fun connect(address: SocketAddress, user: String, pass: String, reconnect: Boolean) {
      disconnect()
      sessionManager.connect(
        clientData, trustManager, hostnameVerifier, address, user to pass, reconnect
      )
    }

    override fun reconnect() {
      sessionManager.reconnect()
    }

    override fun disconnect(forever: Boolean) {
      sessionManager.disconnect(forever)
    }
  }

  private val handlerService = AndroidHandlerService()

  private val asyncBackend = AsyncBackend(handlerService, backendImplementation, ::stopSelf)

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var accountDatabase: AccountDatabase

  @Inject
  lateinit var contentFormatter: ContentFormatter

  @Inject
  lateinit var messageSettings: MessageSettings

  private val connectivityReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (context != null && intent != null) {
        connectivity.onNext(Unit)
      }
    }
  }
  private val connectivity = PublishSubject.create<Unit>()

  private fun disconnectFromCore() {
    getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE).editCommit {
      putBoolean(Keys.Status.reconnect, false)
    }
  }

  override fun onCreate() {
    super.onCreate()

    certificateManager = QuasselCertificateManager(database.validityWhitelist())
    hostnameVerifier = QuasselHostnameVerifier(QuasselHostnameManager(database.hostnameWhitelist()))
    trustManager = QuasselTrustManager(certificateManager)

    sessionManager = SessionManager(
      ISession.NULL,
      QuasselBacklogStorage(database),
      notificationBackend,
      handlerService,
      ::disconnectFromCore,
      CrashHandler::handle
    )

    clientData = ClientData(
      identifier = "${resources.getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}",
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

    connectivity
      .delay(200, TimeUnit.MILLISECONDS)
      .throttleFirst(1, TimeUnit.SECONDS)
      .toLiveData()
      .observe(this, Observer {
        handlerService.backend {
          sessionManager.autoReconnect(true)
        }
      })

    sessionManager.state
      .distinctUntilChanged()
      .delay(200, TimeUnit.MILLISECONDS)
      .throttleFirst(1, TimeUnit.SECONDS)
      .toLiveData()
      .observe(
        this, Observer {
        handlerService.backend {
          sessionManager.autoReconnect()
        }
      })

    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      registerOnSharedPreferenceChangeListener(this@QuasselService)
    }
    sharedPreferences {
      registerOnSharedPreferenceChangeListener(this@QuasselService)
    }

    registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    notificationManager.init()

    update()
    updateNotificationStatus(this.progress)
  }

  override fun onDestroy() {
    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      unregisterOnSharedPreferenceChangeListener(this@QuasselService)
    }
    sharedPreferences {
      unregisterOnSharedPreferenceChangeListener(this@QuasselService)
    }

    unregisterReceiver(connectivityReceiver)

    notificationHandle?.let { notificationManager.remove(it) }
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): QuasselBinder {
    super.onBind(intent)
    return QuasselBinder(asyncBackend)
  }

  companion object {
    fun launch(
      context: Context,
      disconnect: Boolean? = null,
      markRead: BufferId? = null,
      markReadMessage: MsgId? = null
    ): ComponentName = context.startService(intent(context, disconnect, markRead, markReadMessage))

    fun intent(
      context: Context,
      disconnect: Boolean? = null,
      bufferId: BufferId? = null,
      markReadMessage: MsgId? = null
    ) = Intent(context, QuasselService::class.java).apply {
      if (disconnect != null) {
        putExtra("disconnect", disconnect)
      }
      if (bufferId != null) {
        putExtra("buffer", bufferId)
      }
      if (markReadMessage != null) {
        putExtra("mark_read_message", markReadMessage)
      }
    }
  }
}
