package de.kuschku.libquassel

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.ProtocolFeature
import de.kuschku.libquassel.quassel.QuasselFeature
import de.kuschku.libquassel.session.BacklogStorage
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.session.SocketAddress
import de.kuschku.libquassel.util.compatibility.reference.JavaHandlerService
import org.junit.BeforeClass
import org.junit.Test
import org.threeten.bp.Instant
import java.security.cert.X509Certificate
import java.util.logging.LogManager
import javax.net.ssl.X509TrustManager

class ConnectionUnitTest {
  companion object {
    @JvmStatic
    @BeforeClass
    fun before() {
      LogManager.getLogManager()
        .readConfiguration(this::class.java.getResourceAsStream("/logging.properties"))
    }
  }

  @Test
  fun testLocal() {
    runTest("localhost", 4242, "user", "pass")
  }

  private fun runTest(host: String, port: Int, user: String, pass: String) {
    val session = Session(ClientData(
      identifier = "libquassel test",
      buildDate = Instant.EPOCH,
      clientFeatures = Quassel_Feature.of(*QuasselFeature.validValues),
      protocolFeatures = Protocol_Feature.of(ProtocolFeature.TLS, ProtocolFeature.Compression),
      supportedProtocols = listOf(Protocol.Datastream)
    ), object : X509TrustManager {
      override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
      }

      override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
      }

      override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }, SocketAddress(host, port), JavaHandlerService(), object : BacklogStorage {
      override fun storeMessages(vararg messages: Message, initialLoad: Boolean) = Unit
      override fun storeMessages(messages: Iterable<Message>, initialLoad: Boolean) = Unit
      override fun clearMessages(bufferId: BufferId, idRange: IntRange) = Unit
      override fun clearMessages(bufferId: BufferId) = Unit
      override fun clearMessages() = Unit
    }, user to pass)
    session.join()
  }
}
