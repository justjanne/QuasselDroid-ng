/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.*
import org.junit.Test

class NetworkTest {
  @Test
  fun testSerialization() {
    val original = Network(NetworkId(randomInt()), SignalProxy.NULL)
    original.setNetworkName(randomString())
    original.setIdentity(IdentityId(randomInt()))
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
    original.setCodecForServer(randomCharset().name())
    original.setCodecForEncoding(randomCharset().name())
    original.setCodecForDecoding(randomCharset().name())
    original.addSupport("AWAYLEN", "200")
    original.addSupport("CALLERID", "g")
    original.addSupport("CASEMAPPING", "rfc1459")
    original.addSupport("CHANMODES", "IXZbegw,k,FHJLUdfjlx,ABCDKMNOPQRSTcimnprstuz")
    original.addSupport("CHANNELLEN", "64")
    original.addSupport("CHANTYPES", "#")
    original.addSupport("CHARSET", "ascii")
    original.addSupport("ELIST", "MU")
    original.addSupport("ESILENCE", "")
    original.addSupport("EXCEPTS", "e")
    original.addSupport("EXTBAN", ",ABCNOQRSTUcjmprsz")
    original.addSupport("FNC", "")
    original.addSupport("INVEX", "I")
    for (i in 0 until 8) {
      original.newIrcUser(randomString() + "!" + randomString() + "@" + randomString()).apply {
        setUser(randomString())
        setHost(randomString())
        setNick(randomString())
        setRealName(randomString())
        setAccount(randomString())
        setAway(randomBoolean())
        setAwayMessage(randomString())
        setIdleTime(randomInstant())
        setLoginTime(randomInstant())
        setServer(randomString())
        setIrcOperator(randomString())
        setLastAwayMessageTime(randomInstant())
        setWhoisServiceReply(randomString())
        setSuserHost(randomString())
        setEncrypted(randomBoolean())
        setUserModes(listOf(
          randomOf(*USERMODES),
          randomOf(*USERMODES),
          randomOf(*USERMODES)
        ).toString())
      }
    }
    for (i in 0 until 8) {
      original.newIrcChannel(randomString(), mapOf()).apply {
        setTopic(randomString())
        setPassword(randomString())
        setEncrypted(randomBoolean())
        addChannelMode(randomOf(*CHANMODES_A), randomString())
        addChannelMode(randomOf(*CHANMODES_A), randomString())
        addChannelMode(randomOf(*CHANMODES_A), randomString())
        addChannelMode(randomOf(*CHANMODES_B), randomString())
        addChannelMode(randomOf(*CHANMODES_C), randomString())
        addChannelMode(randomOf(*CHANMODES_C), randomString())
        addChannelMode(randomOf(*CHANMODES_D), null)
        addChannelMode(randomOf(*CHANMODES_D), null)
        addChannelMode(randomOf(*CHANMODES_D), null)
        randomOf(original.ircUsers()).let {
          joinIrcUser(it)
          setUserModes(it, randomOf(*CHANMODES_PREFIX).toString())
        }
        randomOf(original.ircUsers()).let {
          joinIrcUser(it)
          setUserModes(it, randomOf(*CHANMODES_PREFIX).toString())
        }
        randomOf(original.ircUsers()).let {
          joinIrcUser(it)
          setUserModes(it, randomOf(*CHANMODES_PREFIX).toString())
        }
        randomOf(original.ircUsers()).let {
          joinIrcUser(it)
          setUserModes(it, randomOf(*CHANMODES_PREFIX).toString())
        }
        randomOf(original.ircUsers()).let {
          joinIrcUser(it)
          setUserModes(it, randomOf(*CHANMODES_PREFIX).toString())
        }
      }
    }

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    assert(original.isEqual(copy)) {
      System.err.println("Original:\n$original")
      System.err.println("Copy:\n$copy")
    }
  }

  @Test
  fun testCopy() {
    val original = Network(NetworkId(randomInt()), SignalProxy.NULL)
    original.setNetworkName(randomString())
    original.setIdentity(IdentityId(randomInt()))
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
    original.setCodecForServer(randomCharset().name())
    original.setCodecForEncoding(randomCharset().name())
    original.setCodecForDecoding(randomCharset().name())
    original.addSupport("AWAYLEN", "200")
    original.addSupport("CALLERID", "g")
    original.addSupport("CASEMAPPING", "rfc1459")
    original.addSupport("CHANMODES", "IXZbegw,k,FHJLUdfjlx,ABCDKMNOPQRSTcimnprstuz")
    original.addSupport("CHANNELLEN", "64")
    original.addSupport("CHANTYPES", "#")
    original.addSupport("CHARSET", "ascii")
    original.addSupport("ELIST", "MU")
    original.addSupport("ESILENCE", "")
    original.addSupport("EXCEPTS", "e")
    original.addSupport("EXTBAN", ",ABCNOQRSTUcjmprsz")
    original.addSupport("FNC", "")
    original.addSupport("INVEX", "I")
    for (i in 0 until 8) {
      original.newIrcUser(randomString() + "!" + randomString() + "@" + randomString()).apply {
        setUser(randomString())
        setHost(randomString())
        setNick(randomString())
        setRealName(randomString())
        setAccount(randomString())
        setAway(randomBoolean())
        setAwayMessage(randomString())
        setIdleTime(randomInstant())
        setLoginTime(randomInstant())
        setServer(randomString())
        setIrcOperator(randomString())
        setLastAwayMessageTime(randomInstant())
        setWhoisServiceReply(randomString())
        setSuserHost(randomString())
        setEncrypted(randomBoolean())
        setUserModes(listOf(
          randomOf(*USERMODES),
          randomOf(*USERMODES),
          randomOf(*USERMODES)
        ).toString())
      }
    }
    for (i in 0 until 8) {
      original.newIrcChannel(randomString(), mapOf()).apply {
        setTopic(randomString())
        setPassword(randomString())
        setEncrypted(randomBoolean())
        addChannelMode(randomOf(*CHANMODES_A), randomString())
        addChannelMode(randomOf(*CHANMODES_A), randomString())
        addChannelMode(randomOf(*CHANMODES_A), randomString())
        addChannelMode(randomOf(*CHANMODES_B), randomString())
        addChannelMode(randomOf(*CHANMODES_C), randomString())
        addChannelMode(randomOf(*CHANMODES_C), randomString())
        addChannelMode(randomOf(*CHANMODES_D), null)
        addChannelMode(randomOf(*CHANMODES_D), null)
        addChannelMode(randomOf(*CHANMODES_D), null)
        randomOf(original.ircUsers()).let {
          joinIrcUser(it)
          setUserModes(it, randomOf(*CHANMODES_PREFIX).toString())
        }
        randomOf(original.ircUsers()).let {
          joinIrcUser(it)
          setUserModes(it, randomOf(*CHANMODES_PREFIX).toString())
        }
        randomOf(original.ircUsers()).let {
          joinIrcUser(it)
          setUserModes(it, randomOf(*CHANMODES_PREFIX).toString())
        }
        randomOf(original.ircUsers()).let {
          joinIrcUser(it)
          setUserModes(it, randomOf(*CHANMODES_PREFIX).toString())
        }
        randomOf(original.ircUsers()).let {
          joinIrcUser(it)
          setUserModes(it, randomOf(*CHANMODES_PREFIX).toString())
        }
      }
    }

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    assert(original.isEqual(copy)) {
      System.err.println("Original:\n$original")
      System.err.println("Copy:\n$copy")
    }
  }

  companion object {
    private val USERMODES = arrayOf(
      'B', 'H', 'I', 'L', 'O', 'R', 'S', 'T', 'W', 'c', 'd', 'g', 'h', 'i', 'k', 'o', 'r', 's', 'w',
      'x'
    )
    private val CHANMODES_PREFIX = arrayOf(
      'Y', 'o', 'h', 'v'
    )
    private val CHANMODES_A = arrayOf(
      'I', 'X', 'Z', 'b', 'e', 'g', 'w'
    )
    private val CHANMODES_B = arrayOf(
      'k'
    )
    private val CHANMODES_C = arrayOf(
      'F', 'H', 'J', 'L', 'U', 'd', 'f', 'j', 'l', 'x'
    )
    private val CHANMODES_D = arrayOf(
      'A', 'B', 'C', 'D', 'K', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'c', 'i', 'm', 'n', 'p', 'r',
      's', 't', 'u', 'z'
    )
  }
}
