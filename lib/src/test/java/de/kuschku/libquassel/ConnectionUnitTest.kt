package de.kuschku.libquassel

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.ProtocolFeature
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.session.BacklogStorage
import de.kuschku.libquassel.session.ConnectionState
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

  @Test
  fun testRemote() {
    runTest(
      host = System.getenv("TEST_HOST"),
      port = System.getenv("TEST_PORT").toInt(),
      user = System.getenv("TEST_USER"),
      pass = System.getenv("TEST_PASS")
    )
  }

  private fun runTest(host: String, port: Int, user: String, pass: String) {
    val start = System.currentTimeMillis()
    val session = Session(
      ClientData(
        identifier = "libquassel test",
        buildDate = Instant.EPOCH,
        clientFeatures = QuasselFeatures.all(),
        protocolFeatures = Protocol_Feature.of(ProtocolFeature.TLS, ProtocolFeature.Compression),
        supportedProtocols = listOf(Protocol.Datastream)
      ), object : X509TrustManager {
      override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
      }

      override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
      }

      override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }, SocketAddress(host, port), JavaHandlerService(), object : BacklogStorage {
      override fun updateIgnoreRules(session: Session) = Unit
      override fun storeMessages(session: Session, vararg messages: Message,
                                 initialLoad: Boolean) = Unit

      override fun storeMessages(session: Session, messages: Iterable<Message>,
                                 initialLoad: Boolean) = Unit

      override fun clearMessages(bufferId: BufferId, idRange: IntRange) = Unit
      override fun clearMessages(bufferId: BufferId) = Unit
      override fun clearMessages() = Unit
    }, user to pass, {}, {})
    session.state.subscribe {
      if (it == ConnectionState.CONNECTED) {
        val end = System.currentTimeMillis()
        println("Connection took ${0.001 * (end - start)} seconds")
        session.close()
      }
    }
    session.join()
  }
}
