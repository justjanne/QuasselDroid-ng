/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.*
import org.junit.Test

class NetworkTest {
  @Test
  fun testSerialization() {
    val original = Network(randomUInt(), SignalProxy.NULL)
    original.setNetworkName(randomString())
    original.setIdentity(randomUInt())
    original.setActualServerList(listOf(
      INetwork.Server(
        host = randomString(),
        port = randomUInt(),
        password = randomString(),
        useSsl = randomBoolean(),
        sslVerify = randomBoolean(),
        sslVersion = randomInt(),
        useProxy = randomBoolean(),
        proxyType = randomOf(*INetwork.ProxyType.values()).value,
        proxyHost = randomString(),
        proxyPort = randomUInt(),
        proxyUser = randomString(),
        proxyPass = randomString()
      )
    ))
    original.setUseRandomServer(randomBoolean())
    original.setPerform(listOf(
      randomString(),
      randomString(),
      randomString()
    ))
    original.setUseAutoIdentify(randomBoolean())
    original.setAutoIdentifyService(randomString())
    original.setAutoIdentifyPassword(randomString())
    original.setUseSasl(randomBoolean())
    original.setSaslAccount(randomString())
    original.setSaslPassword(randomString())
    original.setUseAutoReconnect(randomBoolean())
    original.setAutoReconnectInterval(randomUInt())
    original.setAutoReconnectRetries(randomUShort())
    original.setUnlimitedReconnectRetries(randomBoolean())
    original.setRejoinChannels(randomBoolean())
    original.setUseCustomMessageRate(randomBoolean())
    original.setMessageRateBurstSize(randomUInt())
    original.setMessageRateDelay(randomUInt())
    original.setUnlimitedMessageRate(randomBoolean())
    original.setCodecForServer(randomCharset())
    original.setCodecForEncoding(randomCharset())
    original.setCodecForDecoding(randomCharset())

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    assert(original.isEqual(copy))
  }

  @Test
  fun testCopy() {
    val original = Network(randomUInt(), SignalProxy.NULL)
    original.setNetworkName(randomString())
    original.setIdentity(randomUInt())
    original.setActualServerList(listOf(
      INetwork.Server(
        host = randomString(),
        port = randomUInt(),
        password = randomString(),
        useSsl = randomBoolean(),
        sslVerify = randomBoolean(),
        sslVersion = randomInt(),
        useProxy = randomBoolean(),
        proxyType = randomOf(*INetwork.ProxyType.values()).value,
        proxyHost = randomString(),
        proxyPort = randomUInt(),
        proxyUser = randomString(),
        proxyPass = randomString()
      )
    ))
    original.setUseRandomServer(randomBoolean())
    original.setPerform(listOf(
      randomString(),
      randomString(),
      randomString()
    ))
    original.setUseAutoIdentify(randomBoolean())
    original.setAutoIdentifyService(randomString())
    original.setAutoIdentifyPassword(randomString())
    original.setUseSasl(randomBoolean())
    original.setSaslAccount(randomString())
    original.setSaslPassword(randomString())
    original.setUseAutoReconnect(randomBoolean())
    original.setAutoReconnectInterval(randomUInt())
    original.setAutoReconnectRetries(randomUShort())
    original.setUnlimitedReconnectRetries(randomBoolean())
    original.setRejoinChannels(randomBoolean())
    original.setUseCustomMessageRate(randomBoolean())
    original.setMessageRateBurstSize(randomUInt())
    original.setMessageRateDelay(randomUInt())
    original.setUnlimitedMessageRate(randomBoolean())
    original.setCodecForServer(randomCharset())
    original.setCodecForEncoding(randomCharset())
    original.setCodecForDecoding(randomCharset())
    original.setAutoAwayActive(randomBoolean())

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    assert(original.isEqual(copy))
  }
}
