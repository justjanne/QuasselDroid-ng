package de.kuschku.quasseldroid

import de.kuschku.bitflags.of
import de.kuschku.libquassel.protocol.connection.*
import de.kuschku.libquassel.protocol.features.FeatureSet
import de.kuschku.libquassel.protocol.io.ChainedByteBuffer
import de.kuschku.libquassel.protocol.io.contentToString
import de.kuschku.libquassel.protocol.messages.handshake.ClientInit
import de.kuschku.libquassel.protocol.serializers.HandshakeSerializers
import de.kuschku.libquassel.protocol.serializers.handshake.ClientInitAckSerializer
import de.kuschku.libquassel.protocol.serializers.handshake.ClientInitRejectSerializer
import de.kuschku.libquassel.protocol.serializers.handshake.ClientInitSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.HandshakeMapSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.IntSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.UIntSerializer
import de.kuschku.libquassel.protocol.variant.into
import de.kuschku.quasseldroid.protocol.io.CoroutineChannel
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class ExampleUnitTest {

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
      val connectionFeatureSet = FeatureSet.all()
      val sizeBuffer = ByteBuffer.allocateDirect(4)
      val sendBuffer = ChainedByteBuffer(direct = true)
      val channel = CoroutineChannel()
      channel.connect(InetSocketAddress("kuschku.de", 4242))

      suspend fun readAmount(amount: Int? = null): Int {
        if (amount != null) return amount

        sizeBuffer.clear()
        channel.read(sizeBuffer)
        sizeBuffer.flip()
        val size = IntSerializer.deserialize(sizeBuffer, connectionFeatureSet)
        sizeBuffer.clear()
        return size
      }

      suspend fun write(sizePrefix: Boolean = true, f: suspend (ChainedByteBuffer) -> Unit) {
        f(sendBuffer)
        if (sizePrefix) {
          sizeBuffer.clear()
          sizeBuffer.putInt(sendBuffer.size)
          sizeBuffer.flip()
          channel.write(sizeBuffer)
          sizeBuffer.clear()
        }
        channel.write(sendBuffer)
        channel.flush()
        sendBuffer.clear()
      }

      suspend fun <T> read(amount: Int? = null, f: suspend (ByteBuffer) -> T): T {
        val amount1 = readAmount(amount)
        val messageBuffer = ByteBuffer.allocateDirect(minOf(amount1, 65 * 1024 * 1024))
        channel.read(messageBuffer)
        messageBuffer.flip()
        return f(messageBuffer)
      }

      println("Writing protocol")
      write(sizePrefix = false) {
        ConnectionHeaderSerializer.serialize(
          it,
          ConnectionHeader(
            features = ProtocolFeature.of(
              ProtocolFeature.Compression,
              ProtocolFeature.TLS
            ),
            versions = listOf(
              ProtocolMeta(
                0x0000u,
                ProtocolVersion.Datastream,
              ),
            )
          ),
          connectionFeatureSet
        )
      }

      println("Reading protocol")
      read(4) {
        println(ProtocolInfoSerializer.deserialize(it, connectionFeatureSet))
        println(channel.tlsInfo.value)
        channel.enableTLS(context)
        println(channel.tlsInfo.value)
        channel.enableCompression()
      }
      println("Writing clientInit")
      write {
        HandshakeMapSerializer.serialize(
          it,
          ClientInitSerializer.serialize(ClientInit(
            clientVersion = "Quasseldroid test",
            buildDate = "Never",
            clientFeatures = connectionFeatureSet.legacyFeatures(),
            featureList = connectionFeatureSet.featureList()
          )),
          connectionFeatureSet
        )
      }
      read {
        val data = HandshakeMapSerializer.deserialize(it, connectionFeatureSet)
        println(data)
        val msgType: String = data["MsgType"].into("")
        val message: Any? = HandshakeSerializers[msgType]?.deserialize(data)
        println(message)
      }
    }
  }
}

