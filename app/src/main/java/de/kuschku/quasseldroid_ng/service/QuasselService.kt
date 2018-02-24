package de.kuschku.quasseldroid_ng.service

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.*
import android.net.ConnectivityManager
import android.os.Binder
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.session.*
import de.kuschku.quasseldroid_ng.BuildConfig
import de.kuschku.quasseldroid_ng.Keys
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase
import de.kuschku.quasseldroid_ng.persistence.QuasselBacklogStorage
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.ui.settings.data.ConnectionSettings
import de.kuschku.quasseldroid_ng.ui.settings.data.Settings
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.NotificationManager
import de.kuschku.quasseldroid_ng.util.compatibility.AndroidHandlerService
import de.kuschku.quasseldroid_ng.util.helper.sharedPreferences
import de.kuschku.quasseldroid_ng.util.helper.toLiveData
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.Instant
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager

class QuasselService : LifecycleService(),
                       SharedPreferences.OnSharedPreferenceChangeListener {
  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    update()
  }

  private fun update() {
    val connectionSettings = Settings.connection(this)
    if (this.connectionSettings?.showNotification != connectionSettings.showNotification) {
      this.connectionSettings = connectionSettings

      updateNotificationStatus()
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

  private lateinit var notificationManager: NotificationManager
  private var notificationHandle: NotificationManager.Handle? = null
  private var progress = Triple(ConnectionState.DISCONNECTED, 0, 0)

  private fun updateNotificationStatus() {
    if (connectionSettings?.showNotification == true) {
      val notificationHandle = notificationManager.notificationBackground()
      this.notificationHandle = notificationHandle
      updateNotification(notificationHandle)
      startForeground(notificationHandle.id, notificationHandle.builder.build())
    } else {
      stopForeground(true)
    }
  }

  private fun updateNotification(handle: NotificationManager.Handle) {
    val (state, progress, max) = this.progress
    when (state) {
      ConnectionState.DISCONNECTED -> {
        handle.builder.setContentTitle(getString(R.string.label_status_disconnected))
        handle.builder.setProgress(0, 0, false)
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
        handle.builder.setProgress(max, progress, false)
      }
      ConnectionState.CONNECTED    -> {
        handle.builder.setContentTitle(getString(R.string.label_status_connected))
        handle.builder.setProgress(0, 0, false)
      }
    }
  }

  private fun updateConnection(accountId: Long, reconnect: Boolean) {
    handler.post {
      val account = if (accountId == -1L || !reconnect) {
        null
      } else {
        AccountDatabase.Creator.init(this).accounts().findById(accountId)
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

  private var connectionSettings: ConnectionSettings? = null

  private val trustManager = object : X509TrustManager {
    @SuppressLint("TrustAllX509TrustManager")
    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) = Unit

    @SuppressLint("TrustAllX509TrustManager")
    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) = Unit

    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
  }

  private val backendImplementation = object : Backend {
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
        clientData, trustManager, address, ::AndroidHandlerService,
        user to pass, reconnect
      )
    }

    override fun reconnect() {
      sessionManager.reconnect()
    }

    override fun disconnect(forever: Boolean) {
      sessionManager.disconnect(forever)
    }
  }

  private val handler = AndroidHandlerThread("Backend")

  private val asyncBackend = object : Backend {
    override fun connectUnlessConnected(address: SocketAddress, user: String, pass: String,
                                        reconnect: Boolean) {
      handler.post {
        backendImplementation.connectUnlessConnected(address, user, pass, reconnect)
      }
    }

    override fun connect(address: SocketAddress, user: String, pass: String, reconnect: Boolean) {
      handler.post {
        backendImplementation.connect(address, user, pass, reconnect)
      }
    }

    override fun reconnect() {
      handler.post {
        backendImplementation.reconnect()
      }
    }

    override fun disconnect(forever: Boolean) {
      handler.post {
        backendImplementation.disconnect(forever)
        if (forever)
          stopSelf()
      }
    }

    override fun sessionManager() = backendImplementation.sessionManager()
  }

  override fun onDestroy() {
    handler.onDestroy()
    super.onDestroy()
  }

  private lateinit var database: QuasselDatabase

  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (context != null && intent != null) {
        connectivity.onNext(Unit)
      }
    }
  }
  private val connectivity = BehaviorSubject.createDefault(Unit)

  override fun onCreate() {
    handler.onCreate()
    super.onCreate()
    database = QuasselDatabase.Creator.init(application)
    sessionManager = SessionManager(ISession.NULL, QuasselBacklogStorage(database))
    clientData = ClientData(
      identifier = "${resources.getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}",
      buildDate = Instant.ofEpochSecond(BuildConfig.GIT_COMMIT_DATE),
      clientFeatures = Quassel_Features.of(*Quassel_Feature.validValues),
      protocolFeatures = Protocol_Features.of(
        Protocol_Feature.Compression,
        Protocol_Feature.TLS
      ),
      supportedProtocols = listOf(Protocol.Datastream)
    )

    sessionManager.connectionProgress.toLiveData().observe(this, Observer {
      this.progress = it ?: Triple(ConnectionState.DISCONNECTED, 0, 0)
      val handle = this.notificationHandle
      if (handle != null) {
        updateNotification(handle)
        notificationManager.notify(handle)
      }
    })

    Observable.combineLatest(
      sessionManager.state.filter { it == ConnectionState.DISCONNECTED },
      connectivity,
      BiFunction { a: ConnectionState, b: Unit -> a to b })
      .delay(200, TimeUnit.MILLISECONDS)
      .throttleFirst(1, TimeUnit.SECONDS)
      .toLiveData()
      .observe(
        this, Observer {
        sessionManager.reconnect(true)
      }
      )

    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      registerOnSharedPreferenceChangeListener(this@QuasselService)
    }
    sharedPreferences {
      registerOnSharedPreferenceChangeListener(this@QuasselService)
    }

    registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    notificationManager = NotificationManager(this)
    notificationManager.init()

    update()
  }

  override fun onBind(intent: Intent?): QuasselBinder {
    super.onBind(intent)
    return QuasselBinder(asyncBackend)
  }

  class QuasselBinder(val backend: Backend) : Binder()
}
