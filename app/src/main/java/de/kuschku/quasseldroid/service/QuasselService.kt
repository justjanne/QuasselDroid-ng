package de.kuschku.quasseldroid.service

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.*
import android.net.ConnectivityManager
import de.kuschku.libquassel.protocol.ClientData
import de.kuschku.libquassel.protocol.Protocol
import de.kuschku.libquassel.protocol.Protocol_Feature
import de.kuschku.libquassel.protocol.Protocol_Features
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.session.*
import de.kuschku.malheur.CrashHandler
import de.kuschku.quasseldroid.BuildConfig
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.AccountDatabase
import de.kuschku.quasseldroid.persistence.QuasselBacklogStorage
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.ConnectionSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.util.QuasseldroidNotificationManager
import de.kuschku.quasseldroid.util.backport.DaggerLifecycleService
import de.kuschku.quasseldroid.util.compatibility.AndroidHandlerService
import de.kuschku.quasseldroid.util.helper.editApply
import de.kuschku.quasseldroid.util.helper.editCommit
import de.kuschku.quasseldroid.util.helper.sharedPreferences
import de.kuschku.quasseldroid.util.helper.toLiveData
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.Instant
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.X509TrustManager

class QuasselService : DaggerLifecycleService(),
                       SharedPreferences.OnSharedPreferenceChangeListener {
  @Inject
  lateinit var connectionSettings: ConnectionSettings

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    update()
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

      updateConnection(accountId, reconnect)
    }
  }

  private var accountId: Long = -1
  private var reconnect: Boolean = false

  private lateinit var notificationManager: QuasseldroidNotificationManager
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
    handleIntent(intent)
    return result
  }

  private fun handleIntent(intent: Intent?) {
    if (intent?.getBooleanExtra("disconnect", false) == true) {
      sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
        editApply {
          putBoolean(Keys.Status.reconnect, false)
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

  private fun updateConnection(accountId: Long, reconnect: Boolean) {
    handlerService.backend {
      val account = if (accountId != -1L && reconnect) {
        accountDatabase.accounts().findById(accountId)
      } else {
        null
      }

      if (account == null) {
        sessionManager.state.toLiveData()
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

  private val trustManager = object : X509TrustManager {
    @SuppressLint("TrustAllX509TrustManager")
    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) = Unit

    @SuppressLint("TrustAllX509TrustManager")
    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) = Unit

    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
  }

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
        clientData, trustManager, address, user to pass, reconnect
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

  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (context != null && intent != null) {
        connectivity.onNext(Unit)
      }
    }
  }
  private val connectivity = BehaviorSubject.createDefault(Unit)

  private fun disconnectFromCore() {
    getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE).editCommit {
      putBoolean(Keys.Status.reconnect, false)
    }
  }

  override fun onCreate() {
    super.onCreate()
    sessionManager = SessionManager(
      ISession.NULL,
      QuasselBacklogStorage(database),
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

    Observable.combineLatest(
      sessionManager.state.filter { it == ConnectionState.DISCONNECTED || it == ConnectionState.CLOSED },
      connectivity,
      BiFunction { a: ConnectionState, _: Unit -> a })
      .distinctUntilChanged()
      .delay(200, TimeUnit.MILLISECONDS)
      .throttleFirst(1, TimeUnit.SECONDS)
      .toLiveData()
      .observe(
        this, Observer {
        if (it == ConnectionState.DISCONNECTED || it == ConnectionState.CLOSED)
          sessionManager.reconnect(true)
      })

    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      registerOnSharedPreferenceChangeListener(this@QuasselService)
    }
    sharedPreferences {
      registerOnSharedPreferenceChangeListener(this@QuasselService)
    }

    registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    notificationManager = QuasseldroidNotificationManager(this)
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

    unregisterReceiver(receiver)

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
      disconnect: Boolean? = null
    ) = context.startService(intent(context, disconnect))

    fun intent(
      context: Context,
      disconnect: Boolean? = null
    ) = Intent(context, QuasselService::class.java).apply {
      if (disconnect != null) {
        putExtra("disconnect", disconnect)
      }
    }
  }
}
