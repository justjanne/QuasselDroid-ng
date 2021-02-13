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

package de.justjanne.libquassel.client

import de.justjanne.bitflags.of
import de.justjanne.libquassel.client.io.CoroutineChannel
import de.justjanne.libquassel.client.testutil.QuasselCoreContainer
import de.justjanne.libquassel.client.testutil.TestX509TrustManager
import de.justjanne.libquassel.protocol.connection.*
import de.justjanne.libquassel.protocol.features.FeatureSet
import de.justjanne.libquassel.protocol.io.ChainedByteBuffer
import de.justjanne.libquassel.protocol.messages.handshake.*
import de.justjanne.libquassel.protocol.serializers.HandshakeSerializers
import de.justjanne.libquassel.protocol.serializers.handshake.ClientInitSerializer
import de.justjanne.libquassel.protocol.serializers.handshake.ClientLoginSerializer
import de.justjanne.libquassel.protocol.serializers.handshake.CoreSetupDataSerializer
import de.justjanne.libquassel.protocol.serializers.primitive.HandshakeMapSerializer
import de.justjanne.libquassel.protocol.serializers.primitive.IntSerializer
import de.justjanne.libquassel.protocol.variant.into
import de.justjanne.testcontainersci.api.providedContainer
import de.justjanne.testcontainersci.extension.CiContainers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import javax.net.ssl.SSLContext

@ExperimentalCoroutinesApi
@CiContainers
class EndToEndTest {
  private val quassel = providedContainer("QUASSEL_CONTAINER") {
    QuasselCoreContainer()
  }

  private val sslContext = SSLContext.getInstance("TLSv1.3").apply {
    init(null, arrayOf(TestX509TrustManager), null)
  }

  private val connectionFeatureSet = FeatureSet.all()
  private val sizeBuffer = ByteBuffer.allocateDirect(4)
  private val sendBuffer = ChainedByteBuffer(direct = true)
  private val channel = CoroutineChannel()

  private val username = "AzureDiamond"
  private val password = "hunter2"

  @Test
  fun testConnect(): Unit = runBlocking {
    channel.connect(
      InetSocketAddress(
        quassel.address,
        quassel.getMappedPort(4242)
      )
    )

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
    println("Reading clientInit response")
    read {
      val data = HandshakeMapSerializer.deserialize(it, connectionFeatureSet)
      val msgType: String = data["MsgType"].into("")
      HandshakeSerializers[msgType]?.deserialize(data)
        as? ClientInitAck
        ?: fail("Could not deserialize message $data")
    }
    println("Writing invalid core init")
    write {
      HandshakeMapSerializer.serialize(
        it,
        CoreSetupDataSerializer.serialize(CoreSetupData(
          adminUser = username,
          adminPassword = password,
          backend = "MongoDB",
          setupData = emptyMap(),
          authenticator = "OAuth2",
          authSetupData = emptyMap(),
        )),
        connectionFeatureSet
      )
    }
    println("Reading invalid clientInit response")
    read {
      val data = HandshakeMapSerializer.deserialize(it, connectionFeatureSet)
      val msgType: String = data["MsgType"].into("")
      assertEquals(
        CoreSetupReject("Could not setup storage!"),
        HandshakeSerializers[msgType]?.deserialize(data)
      )
    }
    println("Writing valid core init")
    write {
      HandshakeMapSerializer.serialize(
        it,
        CoreSetupDataSerializer.serialize(CoreSetupData(
          adminUser = username,
          adminPassword = password,
          backend = "SQLite",
          setupData = emptyMap(),
          authenticator = "Database",
          authSetupData = emptyMap(),
        )),
        connectionFeatureSet
      )
    }
    println("Reading valid clientInit response")
    read {
      val data = HandshakeMapSerializer.deserialize(it, connectionFeatureSet)
      val msgType: String = data["MsgType"].into("")
      HandshakeSerializers[msgType]?.deserialize(data)
        as? CoreSetupAck
        ?: fail("Could not deserialize message $data")
    }
    println("Writing invalid clientLogin")
    write {
      HandshakeMapSerializer.serialize(
        it,
        ClientLoginSerializer.serialize(ClientLogin(
          user = "acidburn",
          password = "ineverweardresses"
        )),
        connectionFeatureSet
      )
    }
    println("Reading invalid clientLogin response")
    read {
      val data = HandshakeMapSerializer.deserialize(it, connectionFeatureSet)
      val msgType: String = data["MsgType"].into("")
      assertEquals(
        ClientLoginReject("<b>Invalid username or password!</b><br>The username/password combination you supplied could not be found in the database."),
        HandshakeSerializers[msgType]?.deserialize(data)
      )
    }
    println("Writing valid clientLogin")
    write {
      HandshakeMapSerializer.serialize(
        it,
        ClientLoginSerializer.serialize(ClientLogin(
          user = username,
          password = password
        )),
        connectionFeatureSet
      )
    }
    println("Reading valid clientLogin response")
    read {
      val data = HandshakeMapSerializer.deserialize(it, connectionFeatureSet)
      val msgType: String = data["MsgType"].into("")
      HandshakeSerializers[msgType]?.deserialize(data)
        as? ClientLoginAck
        ?: fail("Could not deserialize message $data")
    }
    println("Reading valid session init")
    read {
      val data = HandshakeMapSerializer.deserialize(it, connectionFeatureSet)
      val msgType: String = data["MsgType"].into("")
      HandshakeSerializers[msgType]?.deserialize(data)
        as? SessionInit
        ?: fail("Could not deserialize message $data")
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
