/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel

import de.kuschku.bitflags.of
import de.kuschku.ci_containers.CiContainers
import de.kuschku.ci_containers.CiContainersExtension
import de.kuschku.libquassel.protocol.connection.*
import de.kuschku.libquassel.protocol.features.FeatureSet
import de.kuschku.libquassel.protocol.io.ChainedByteBuffer
import de.kuschku.libquassel.protocol.messages.handshake.ClientInit
import de.kuschku.libquassel.protocol.serializers.HandshakeSerializers
import de.kuschku.libquassel.protocol.serializers.handshake.ClientInitSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.HandshakeMapSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.IntSerializer
import de.kuschku.libquassel.protocol.variant.into
import de.kuschku.libquassel.testutil.TestX509TrustManager
import de.kuschku.libquassel.testutil.quasselContainer
import de.kuschku.quasseldroid.protocol.io.CoroutineChannel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.ByteBuffer
import javax.net.ssl.SSLContext

@ExperimentalCoroutinesApi
@CiContainers
class EndToEndTest {
  private val quassel = quasselContainer()

  private val sslContext = SSLContext.getInstance("TLSv1.3").apply {
    init(null, arrayOf(TestX509TrustManager), null)
  }

  private val connectionFeatureSet = FeatureSet.all()
  private val sizeBuffer = ByteBuffer.allocateDirect(4)
  private val sendBuffer = ChainedByteBuffer(direct = true)
  private val channel = CoroutineChannel()

  @Test
  fun testConnect() = runBlocking {
    channel.connect(quassel.address)

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
      val protocol = ProtocolInfoSerializer.deserialize(it, connectionFeatureSet)
      assertEquals(
        ProtocolFeature.of(
          ProtocolFeature.TLS,
          ProtocolFeature.Compression
        ),
        protocol.flags
      )
      println("Negotiated protocol $protocol")
      if (protocol.flags.contains(ProtocolFeature.TLS)) {
        channel.enableTLS(sslContext)
      }
      if (protocol.flags.contains(ProtocolFeature.Compression)) {
        channel.enableCompression()
      }
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
}
