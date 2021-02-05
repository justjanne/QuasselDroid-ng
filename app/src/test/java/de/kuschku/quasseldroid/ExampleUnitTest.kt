package de.kuschku.quasseldroid

import de.kuschku.quasseldroid.protocol.io.ChainedByteBuffer
import de.kuschku.quasseldroid.protocol.serializers.primitive.IntSerializer
import de.kuschku.quasseldroid.protocol.serializers.primitive.ProtocolInfoSerializer
import de.kuschku.quasseldroid.protocol.serializers.primitive.UIntSerializer
import de.kuschku.quasseldroid.protocol.io.CoroutineChannel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testNetworking() {
    val context = SSLContext.getInstance("TLSv1.3")
    context.init(null, arrayOf(object : X509TrustManager {
      override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        // FIXME: accept everything
      }

      override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        // FIXME: accept everything
      }

      override fun getAcceptedIssuers(): Array<X509Certificate> {
        // FIXME: accept nothing
        return emptyArray()
      }
    }), null)

    runBlocking {
      val sizeBuffer = ByteBuffer.allocateDirect(4)
      val sendBuffer = ChainedByteBuffer(direct = true)
      val channel = CoroutineChannel()
      channel.connect(InetSocketAddress("kuschku.de", 4242))
      val readBuffer = ByteBuffer.allocateDirect(4)
      UIntSerializer.serialize(sendBuffer, 0x42b3_3f00u or 0x03u)
      IntSerializer.serialize(sendBuffer, 2)
      UIntSerializer.serialize(sendBuffer, 0x8000_0000u)
      channel.write(sendBuffer)
      channel.read(readBuffer)
      readBuffer.flip()
      println(ProtocolInfoSerializer.deserialize(readBuffer))
      println(channel.tlsInfo.value)
      channel.enableTLS(context)
      println(channel.tlsInfo.value)
      channel.enableCompression()
    }
  }
}

