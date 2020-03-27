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

package de.kuschku.libquassel.util

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.AliasManager
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork

fun TestSession.with(f: TestSession.() -> Unit) = f.invoke(this)

fun withTestSession(f: TestSession.() -> Unit) = f.invoke(setupTestSession())

fun setupTestSession() = TestSession().provideTestData {
  bufferViewConfigs = listOf(
    buildBufferViewConfig(0) {
      setBufferViewName("All Chats")
      addBuffer(BufferId(1), 0)
      addBuffer(BufferId(2), 1)
      addBuffer(BufferId(3), 2)
    }
  )

  identities = listOf(
    buildIdentity(IdentityId(1)) {
      setIdentityName("Default Identity")
      setRealName("Janne Mareike Koschinski <janne@kuschku.de>")
      setNicks(listOf(
        "justJanne",
        "ninjanne",
        "janne",
        "kuschku"
      ))
      setIdent("justJanne")
    }
  )

  networks = listOf(
    buildNetwork(NetworkId(1)) {
      setNetworkName("FreeNode")
      setCurrentServer("tepper.freenode.net")
      setConnected(true)
      setConnectionState(INetwork.ConnectionState.Initialized)
      setMyNick("justJanne")
      setLatency(48)
      setIdentity(IdentityId(1))
      setActualServerList(listOf(
        INetwork.Server(
          host = "irc.freenode.net",
          port = INetwork.PortDefaults.PORT_SSL.port,
          useSsl = true
        ),
        INetwork.Server(
          host = "chat.freenode.net",
          port = INetwork.PortDefaults.PORT_SSL.port,
          useSsl = true
        )
      ))
      addSupport("CHANTYPES", "#")
      addSupport("EXCEPTS")
      addSupport("INVEX")
      addSupport("CHANMODES", "eIbq,k,flj,CFLMPQScgimnprstz")
      addSupport("CHANLIMIT", "#:120")
      addSupport("PREFIX", "(ov)@+")
      addSupport("MAXLIST", "bqeI:100")
      addSupport("MODES", "4")
      addSupport("NETWORK", "freenode")
      addSupport("STATUSMSG", "@+")
      addSupport("CALLERID", "g")
      addSupport("CASEMAPPING", "rfc1459")
      addSupport("CHARSET", "ascii")
      addSupport("NICKLEN", "16")
      addSupport("CHANNELLEN", "50")
      addSupport("TOPICLEN", "390")
      addSupport("DEAF", "D")
      addSupport("FNC")
      addSupport("TARGMAX", "NAMES:1,LIST:1,KICK:1,WHOIS:1,PRIVMSG:4,NOTICE:4,ACCEPT:,MONITOR:")
      addSupport("EXTBAN", "$,ajrxz")
      addSupport("CLIENTVER", "3.0")
      addSupport("ETRACE")
      addSupport("KNOCK")
      addSupport("WHOX")
      addSupport("CPRIVMSG")
      addSupport("CNOTICE")
      addSupport("SAFELIST")
      addSupport("ELIST", "CTU")

      addCap("account-notify")
      addCap("sasl")
      addCap("identify-msg")
      addCap("multi-prefix")
      addCap("extended-join")

      acknowledgeCap("sasl")
      acknowledgeCap("account-notify")
      acknowledgeCap("extended-join")
      acknowledgeCap("multi-prefix")

      val justJanne = buildIrcUser("justJanne") {
        setUser("kuschku")
        setHost("kuschku.de")
        setRealName("Janne Mareike Koschinski <janne@kuschku.de>")
        setAccount("justJanne")
        setServer("tepper.freenode.net")
      }
      val digitalcircuit = buildIrcUser("digitalcircuit") {
        setUser("~quassel")
        setHost("2605:6000:1518:830d:ec4:7aff:fe6b:c6b0")
        setRealName("Shane <avatar@mg.zorro.casa>")
        setAccount("digitalcircuit")
        setServer("wolfe.freenode.net")
      }
      val Sput = buildIrcUser("Sput") {
        setUser("~sputnick")
        setHost("quassel/developer/sput")
        setRealName("Sputnick -- http://quassel-irc.org")
        setAccount("Sput")
        setServer("niven.freenode.net")
      }

      buildIrcChannel("#quassel-test") {
        setTopic("Quassel testing channel")
        addChannelMode('n')
        addChannelMode('t')
        addChannelMode('c')

        joinIrcUser(justJanne)
        joinIrcUser(digitalcircuit)
        joinIrcUser(Sput)
      }
    }
  )

  buffers = listOf(
    TestSession.BufferTestData(
      bufferInfo = BufferInfo(
        bufferId = BufferId(1),
        networkId = NetworkId(1),
        bufferName = "FreeNode",
        type = Buffer_Type.of(Buffer_Type.StatusBuffer)
      )
    ),
    TestSession.BufferTestData(
      bufferInfo = BufferInfo(
        bufferId = BufferId(2),
        networkId = NetworkId(1),
        bufferName = "#quassel-test",
        type = Buffer_Type.of(Buffer_Type.ChannelBuffer)
      )
    ),
    TestSession.BufferTestData(
      bufferInfo = BufferInfo(
        bufferId = BufferId(3),
        networkId = NetworkId(1),
        bufferName = "digitalcircuit",
        type = Buffer_Type.of(Buffer_Type.QueryBuffer)
      )
    ),
    TestSession.BufferTestData(
      bufferInfo = BufferInfo(
        bufferId = BufferId(4),
        networkId = NetworkId(1),
        bufferName = "ChanServ",
        type = Buffer_Type.of(Buffer_Type.QueryBuffer)
      )
    )
  )

  aliases = AliasManager.defaults()
}
