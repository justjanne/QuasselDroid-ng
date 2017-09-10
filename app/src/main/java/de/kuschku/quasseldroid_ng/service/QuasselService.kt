package de.kuschku.quasseldroid_ng.service

import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Intent
import android.os.Binder
import de.kuschku.quasseldroid_ng.BuildConfig
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.protocol.*
import de.kuschku.quasseldroid_ng.session.*
import org.threeten.bp.Instant
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class QuasselService : LifecycleService() {
  private lateinit var session: Session

  private val backend = object : Backend {
    override fun session() = session

    override fun connect(address: SocketAddress, user: String, pass: String) {
      disconnect()
      session.coreConnection = CoreConnection(session, address)
      session.coreConnection?.start()
      session.userData = user to pass
      connection.postValue(session.coreConnection)
    }

    override fun disconnect() {
      session.coreConnection?.close()
      session.coreConnection = null
      connection.postValue(null)
      ABSENT.postValue(ConnectionState.DISCONNECTED)
    }

    private val connection = MutableLiveData<CoreConnection>()

    val ABSENT = MutableLiveData<ConnectionState>()
    override val status = Transformations.switchMap(connection) { input: CoreConnection? ->
      input?.liveState ?: ABSENT
    }
  }

  private lateinit var database: QuasselDatabase

  override fun onCreate() {
    super.onCreate()
    database = QuasselDatabase.Creator.init(application)
    session = Session(
      clientData = ClientData(
        identifier = "${resources.getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}",
        buildDate = Instant.ofEpochSecond(BuildConfig.GIT_COMMIT_DATE),
        clientFeatures = Quassel_Features.of(*Quassel_Feature.values()),
        protocolFeatures = Protocol_Features.of(
          Protocol_Feature.Compression,
          Protocol_Feature.TLS
        ),
        supportedProtocols = byteArrayOf(0x02)
      ),
      trustManager = object : X509TrustManager {
        override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        }

        override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
      }
    )
  }

  override fun onBind(intent: Intent?): QuasselBinder {
    super.onBind(intent)
    return QuasselBinder(backend)
  }

  class QuasselBinder(val backend: Backend) : Binder()
}
