package de.kuschku.quasseldroid_ng.service

import android.arch.lifecycle.LifecycleService
import android.content.Intent
import android.os.Binder
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.session.SocketAddress
import de.kuschku.quasseldroid_ng.BuildConfig
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.util.AndroidHandlerService
import org.threeten.bp.Instant
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class QuasselService : LifecycleService() {
  private lateinit var session: Session

  private val backend = object : Backend {
    override fun session() = session

    override fun connect(address: SocketAddress, user: String, pass: String) {
      disconnect()
      val handlerService = AndroidHandlerService()
      session.connect(address, handlerService)
      session.userData = user to pass
    }

    override fun disconnect() {
      session.cleanUp()
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
