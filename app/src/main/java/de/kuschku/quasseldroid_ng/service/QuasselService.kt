package de.kuschku.quasseldroid_ng.service

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Binder
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.session.*
import de.kuschku.quasseldroid_ng.BuildConfig
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselBacklogStorage
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.compatibility.AndroidHandlerService
import de.kuschku.quasseldroid_ng.util.helper.toLiveData
import org.threeten.bp.Instant
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager

class QuasselService : LifecycleService() {
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

  override fun onCreate() {
    handler.onCreate()
    super.onCreate()
    database = QuasselDatabase.Creator.init(application)
    sessionManager = SessionManager(ISession.NULL, QuasselBacklogStorage(database))
    clientData = ClientData(
      identifier = "${resources.getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}",
      buildDate = Instant.ofEpochSecond(BuildConfig.GIT_COMMIT_DATE),
      clientFeatures = Quassel_Features.of(*Quassel_Feature.values()),
      protocolFeatures = Protocol_Features.of(
        Protocol_Feature.Compression,
        Protocol_Feature.TLS
      ),
      supportedProtocols = listOf(Protocol.Datastream)
    )
    sessionManager.state
      .filter { it == ConnectionState.DISCONNECTED }
      .delay(200, TimeUnit.MILLISECONDS)
      .throttleFirst(1, TimeUnit.SECONDS)
      .toLiveData()
      .observe(
        this, Observer {
        sessionManager.reconnect()
      }
      )
  }

  override fun onBind(intent: Intent?): QuasselBinder {
    super.onBind(intent)
    return QuasselBinder(asyncBackend)
  }

  class QuasselBinder(val backend: Backend) : Binder()
}
