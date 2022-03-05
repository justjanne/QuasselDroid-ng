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

package de.kuschku.libquassel.quassel.syncables.interfaces

import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.annotations.SyncedCall
import de.justjanne.libquassel.annotations.SyncedObject
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QStringList
import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.QuasselType
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.protocol.qVariant
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.protocol.valueOrThrow
import de.kuschku.libquassel.util.flag.Flag
import de.kuschku.libquassel.util.flag.Flags
import de.kuschku.libquassel.util.helper.serializeString
import java.io.Serializable
import java.nio.ByteBuffer

@SyncedObject("Network")
interface INetwork : ISyncableObject {
  fun initCapsEnabled(): QVariantList
  fun initServerList(): QVariantList
  fun initCaps(): QVariantMap
  fun initIrcUsersAndChannels(): QVariantMap
  fun initSupports(): QVariantMap
  fun initSetCaps(caps: QVariantMap)
  fun initSetCapsEnabled(capsEnabled: QVariantList)
  fun initSetIrcUsersAndChannels(usersAndChannels: QVariantMap)
  fun initSetServerList(serverList: QVariantList)
  fun initSetSupports(supports: QVariantMap)

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setNetworkName(networkName: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setNetworkName",
      qVariant(networkName, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setCurrentServer(currentServer: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setCurrentServer",
      qVariant(currentServer, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setMyNick(myNick: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setMyNick",
      qVariant(myNick, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setLatency(latency: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setLatency",
      qVariant(latency, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setCodecForServer(codecForServer: ByteBuffer) {
    sync(
      target = ProtocolSide.CLIENT,
      "setCodecForServer",
      qVariant(codecForServer, QtType.QByteArray),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setCodecForEncoding(codecForEncoding: ByteBuffer) {
    sync(
      target = ProtocolSide.CLIENT,
      "setCodecForEncoding",
      qVariant(codecForEncoding, QtType.QByteArray),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setCodecForDecoding(codecForDecoding: ByteBuffer) {
    sync(
      target = ProtocolSide.CLIENT,
      "setCodecForDecoding",
      qVariant(codecForDecoding, QtType.QByteArray),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setIdentity(identityId: IdentityId) {
    sync(
      target = ProtocolSide.CLIENT,
      "setIdentity",
      qVariant(identityId, QuasselType.IdentityId),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setConnected(isConnected: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setConnected",
      qVariant(isConnected, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setConnectionState(connectionState: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setConnectionState",
      qVariant(connectionState, QtType.Int),
    )
    setConnectionState(ConnectionState.of(connectionState))
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUseRandomServer(useRandomServer: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUseRandomServer",
      qVariant(useRandomServer, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setPerform(perform: QStringList) {
    sync(
      target = ProtocolSide.CLIENT,
      "setPerform",
      qVariant(perform, QtType.QStringList),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setSkipCaps(skipCaps: QStringList) {
    sync(
      target = ProtocolSide.CLIENT,
      "setSkipCaps",
      qVariant(skipCaps, QtType.QStringList),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUseAutoIdentify(useAutoIdentify: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUseAutoIdentify",
      qVariant(useAutoIdentify, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoIdentifyService(autoIdentifyService: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoIdentifyService",
      qVariant(autoIdentifyService, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoIdentifyPassword(autoIdentifyPassword: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoIdentifyPassword",
      qVariant(autoIdentifyPassword, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUseSasl(useSasl: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUseSasl",
      qVariant(useSasl, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setSaslAccount(saslAccount: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setSaslAccount",
      qVariant(saslAccount, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setSaslPassword(saslPassword: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setSaslPassword",
      qVariant(saslPassword, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUseAutoReconnect(useAutoReconnect: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUseAutoReconnect",
      qVariant(useAutoReconnect, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoReconnectInterval(autoReconnectInterval: UInt) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoReconnectInterval",
      qVariant(autoReconnectInterval, QtType.UInt),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoReconnectRetries(autoReconnectRetries: UShort) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoReconnectRetries",
      qVariant(autoReconnectRetries, QtType.UShort),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUnlimitedReconnectRetries(unlimitedReconnectRetries: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUnlimitedReconnectRetries",
      qVariant(unlimitedReconnectRetries, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setRejoinChannels(rejoinChannels: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setRejoinChannels",
      qVariant(rejoinChannels, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUseCustomMessageRate(useCustomMessageRate: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUseCustomMessageRate",
      qVariant(useCustomMessageRate, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setMessageRateBurstSize(messageRateBurstSize: UInt) {
    sync(
      target = ProtocolSide.CLIENT,
      "setMessageRateBurstSize",
      qVariant(messageRateBurstSize, QtType.UInt),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setMessageRateDelay(messageRateDelay: UInt) {
    sync(
      target = ProtocolSide.CLIENT,
      "setMessageRateDelay",
      qVariant(messageRateDelay, QtType.UInt),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUnlimitedMessageRate(unlimitedMessageRate: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUnlimitedMessageRate",
      qVariant(unlimitedMessageRate, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setServerList(serverList: QVariantList) {
    sync(
      target = ProtocolSide.CLIENT,
      "setServerList",
      qVariant(serverList, QtType.QVariantList),
    )
    setActualServerList(serverList.map {
      it.valueOrThrow<QVariantMap>()
    }.map(Server.Companion::fromVariantMap))
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addSupport(param: String, value: String = "") {
    sync(
      target = ProtocolSide.CLIENT,
      "addSupport",
      qVariant(param, QtType.QString),
      qVariant(value, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun removeSupport(param: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "removeSupport",
      qVariant(param, QtType.QString)
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addCap(capability: String, value: String = "") {
    sync(
      target = ProtocolSide.CLIENT,
      "addCap",
      qVariant(capability, QtType.QString),
      qVariant(value, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun acknowledgeCap(capability: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "acknowledgeCap",
      qVariant(capability, QtType.QString)
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun removeCap(capability: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "removeCap",
      qVariant(capability, QtType.QString)
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun clearCaps() {
    sync(
      target = ProtocolSide.CLIENT,
      "clearCaps"
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addIrcUser(hostmask: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "addIrcUser",
      qVariant(hostmask, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addIrcChannel(channel: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "addIrcChannel",
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestConnect() {
    sync(
      target = ProtocolSide.CORE,
      "requestConnect",
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestDisconnect() {
    sync(
      target = ProtocolSide.CORE,
      "requestDisconnect",
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetNetworkInfo(info: NetworkInfo) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetNetworkInfo",
      qVariant(info, QuasselType.NetworkInfo),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setNetworkInfo(info: NetworkInfo) {
    sync(
      target = ProtocolSide.CLIENT,
      "setNetworkInfo",
      qVariant(info, QuasselType.NetworkInfo),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)

  fun setConnectionState(state: ConnectionState)
  fun setActualServerList(serverList: List<Server>)

  enum class ConnectionState(val value: Int) {
    Disconnected(0),
    Connecting(1),
    Initializing(2),
    Initialized(3),
    Reconnecting(4),
    Disconnecting(5);

    companion object {
      private val byId = enumValues<ConnectionState>().associateBy(
        ConnectionState::value
      )

      fun of(value: Int) = byId[value] ?: Disconnected
    }
  }

  /**
   * {@see http://www.irc.org/tech_docs/005.html}
   * {@see http://www.irc.org/tech_docs/draft-brocklesby-irc-isupport-03.txt}
   */
  enum class ChannelModeType(override val bit: UInt) :
    Flag<ChannelModeType> {
    NOT_A_CHANMODE(0x00u),
    A_CHANMODE(0x01u),
    B_CHANMODE(0x02u),
    C_CHANMODE(0x04u),
    D_CHANMODE(0x08u);

    companion object : Flags.Factory<ChannelModeType> {
      override val NONE = of()
      val validValues = values().filter { it.bit != 0u }.toTypedArray()
      override fun of(bit: Int) = Flags.of<ChannelModeType>(bit)
      override fun of(bit: UInt) = Flags.of<ChannelModeType>(bit)
      override fun of(vararg flags: ChannelModeType) = Flags.of(*flags)
      override fun of(flags: Iterable<ChannelModeType>) = Flags.of(flags)
    }
  }

  /**
   * Default port assignments according to what many IRC networks have settled on.
   * Technically not a standard, but it's fairly widespread.
   * {@see https://freenode.net/news/port-6697-irc-via-tlsssl}
   */
  enum class PortDefaults(val port: UInt) {
    /** Default port for unencrypted connections */
    PORT_PLAINTEXT(6667u),
    /** Default port for encrypted connections */
    PORT_SSL(6697u)
  }

  data class Server(
    val host: String? = "",
    val port: UInt = PortDefaults.PORT_PLAINTEXT.port,
    val password: String? = "",
    val useSsl: Boolean = false,
    val sslVerify: Boolean = false,
    val sslVersion: Int = 0,
    val useProxy: Boolean = false,
    val proxyType: Int = ProxyType.Socks5Proxy.value,
    val proxyHost: String? = "localhost",
    val proxyPort: UInt = 8080u,
    val proxyUser: String? = "",
    val proxyPass: String? = ""
  ) : Serializable {
    fun toVariantMap(): QVariantMap = mapOf(
      "Host" to QVariant.of(host, QtType.QString),
      "Port" to QVariant.of(port, QtType.UInt),
      "Password" to QVariant.of(password, QtType.QString),
      "UseSSL" to QVariant.of(useSsl, QtType.Bool),
      "sslVerify" to QVariant.of(sslVerify, QtType.Bool),
      "sslVersion" to QVariant.of(sslVersion, QtType.Int),
      "UseProxy" to QVariant.of(useProxy, QtType.Bool),
      "ProxyType" to QVariant.of(proxyType, QtType.Int),
      "ProxyHost" to QVariant.of(proxyHost, QtType.QString),
      "ProxyPort" to QVariant.of(proxyPort, QtType.UInt),
      "ProxyUser" to QVariant.of(proxyUser, QtType.QString),
      "ProxyPass" to QVariant.of(proxyPass, QtType.QString)
    )

    companion object {
      fun fromVariantMap(map: QVariantMap) = Server(
        host = map["Host"].value(""),
        port = map["Port"].value(PortDefaults.PORT_PLAINTEXT.port),
        password = map["Password"].value(""),
        useSsl = map["UseSSL"].value(false),
        sslVerify = map["sslVerify"].value(false),
        sslVersion = map["sslVersion"].value(0),
        useProxy = map["UseProxy"].value(false),
        proxyType = map["ProxyType"].value(ProxyType.Socks5Proxy.value),
        proxyHost = map["ProxyHost"].value("localhost"),
        proxyPort = map["ProxyPort"].value(8080u),
        proxyUser = map["ProxyUser"].value(""),
        proxyPass = map["ProxyPass"].value("")
      )
    }
  }

  enum class ProxyType(val value: Int) {
    DefaultProxy(0),
    Socks5Proxy(1),
    NoProxy(2),
    HttpProxy(3),
    HttpCachingProxy(4),
    FtpCachingProxy(5);

    companion object {
      private val byId = enumValues<ProxyType>().associateBy(ProxyType::value)
      fun of(value: Int) = byId[value] ?: DefaultProxy
    }
  }

  data class NetworkInfo(
    var networkId: NetworkId = NetworkId(-1),
    var networkName: String = "",
    var identity: IdentityId = IdentityId(-1),
    // unused
    var useCustomEncodings: Boolean = false,
    var codecForServer: String = "UTF_8",
    var codecForEncoding: String = "UTF_8",
    var codecForDecoding: String = "UTF_8",
    var serverList: List<Server> = emptyList(),
    var useRandomServer: Boolean = false,
    var perform: List<String> = emptyList(),
    var useAutoIdentify: Boolean = false,
    var autoIdentifyService: String = "",
    var autoIdentifyPassword: String = "",
    var useSasl: Boolean = false,
    var saslAccount: String = "",
    var saslPassword: String = "",
    var useAutoReconnect: Boolean = true,
    var autoReconnectInterval: UInt = 0u,
    var autoReconnectRetries: UShort = 0u,
    var unlimitedReconnectRetries: Boolean = true,
    var rejoinChannels: Boolean = true,
    var useCustomMessageRate: Boolean = false,
    var messageRateBurstSize: UInt = 0u,
    var messageRateDelay: UInt = 0u,
    var unlimitedMessageRate: Boolean = false
  ) {
    fun toVariantMap() = mapOf(
      "NetworkId" to QVariant.of(networkId, QuasselType.NetworkId),
      "NetworkName" to QVariant.of(networkName, QtType.QString),
      "Identity" to QVariant.of(identity, QuasselType.IdentityId),
      "UseCustomEncodings" to QVariant.of(useCustomEncodings, QtType.Bool),
      "CodecForServer" to QVariant.of(
        codecForServer.serializeString(StringSerializer.UTF8), QtType.QByteArray
      ),
      "CodecForEncoding" to QVariant.of(
        codecForEncoding.serializeString(StringSerializer.UTF8), QtType.QByteArray
      ),
      "CodecForDecoding" to QVariant.of(
        codecForDecoding.serializeString(StringSerializer.UTF8), QtType.QByteArray
      ),
      "ServerList" to QVariant.of(serverList.map {
        QVariant.of(it.toVariantMap(), QuasselType.Network_Server)
      }, QtType.QVariantList),
      "UseRandomServer" to QVariant.of(useRandomServer, QtType.Bool),
      "Perform" to QVariant.of(perform, QtType.QStringList),
      "UseAutoIdentify" to QVariant.of(useAutoIdentify, QtType.Bool),
      "AutoIdentifyService" to QVariant.of(autoIdentifyService, QtType.QString),
      "AutoIdentifyPassword" to QVariant.of(autoIdentifyPassword, QtType.QString),
      "UseSasl" to QVariant.of(useSasl, QtType.Bool),
      "SaslAccount" to QVariant.of(saslAccount, QtType.QString),
      "SaslPassword" to QVariant.of(saslPassword, QtType.QString),
      "UseAutoReconnect" to QVariant.of(useAutoReconnect, QtType.Bool),
      "AutoReconnectInterval" to QVariant.of(autoReconnectInterval, QtType.UInt),
      "AutoReconnectRetries" to QVariant.of(autoReconnectRetries, QtType.UShort),
      "UnlimitedReconnectRetries" to QVariant.of(unlimitedReconnectRetries, QtType.Bool),
      "RejoinChannels" to QVariant.of(rejoinChannels, QtType.Bool),
      "UseCustomMessageRate" to QVariant.of(useCustomMessageRate, QtType.Bool),
      "MessageRateBurstSize" to QVariant.of(messageRateBurstSize, QtType.UInt),
      "MessageRateDelay" to QVariant.of(messageRateDelay, QtType.UInt),
      "UnlimitedMessageRate" to QVariant.of(unlimitedMessageRate, QtType.Bool)
    )

    fun fromVariantMap(map: QVariantMap) {
      networkId = map["NetworkId"].value(networkId)
      networkName = map["NetworkName"].value(networkName)
      identity = map["Identity"].value(identity)
      useCustomEncodings = map["UseCustomEncodings"].value(useCustomEncodings)
      codecForServer = map["CodecForServer"].value(codecForServer)
      codecForEncoding = map["CodecForEncoding"].value(codecForEncoding)
      codecForDecoding = map["CodecForDecoding"].value(codecForDecoding)
      serverList = map["ServerList"].value(emptyList<QVariant_>()).map {
        Server.fromVariantMap(it.value(emptyMap()))
      }
      useRandomServer = map["UseRandomServer"].value(useRandomServer)
      perform = map["Perform"].value(perform)
      useAutoIdentify = map["UseAutoIdentify"].value(useAutoIdentify)
      autoIdentifyService = map["AutoIdentifyService"].value(autoIdentifyService)
      autoIdentifyPassword = map["AutoIdentifyPassword"].value(autoIdentifyPassword)
      useSasl = map["UseSasl"].value(useSasl)
      saslAccount = map["SaslAccount"].value(saslAccount)
      saslPassword = map["SaslPassword"].value(saslPassword)
      useAutoReconnect = map["UseAutoReconnect"].value(useAutoReconnect)
      autoReconnectInterval = map["AutoReconnectInterval"].value(autoReconnectInterval)
      autoReconnectRetries = map["AutoReconnectRetries"].value(autoReconnectRetries)
      unlimitedReconnectRetries = map["UnlimitedReconnectRetries"].value(unlimitedReconnectRetries)
      rejoinChannels = map["RejoinChannels"].value(rejoinChannels)
      useCustomMessageRate = map["UseCustomMessageRate"].value(useCustomMessageRate)
      messageRateBurstSize = map["MessageRateBurstSize"].value(messageRateBurstSize)
      messageRateDelay = map["MessageRateDelay"].value(messageRateDelay)
      unlimitedMessageRate = map["UnlimitedMessageRate"].value(unlimitedMessageRate)
    }
  }

  fun ircUserNickChanged(old: String, new: String)

  /**
   * IRCv3 capability names and values
   */
  @Suppress("MemberVisibilityCanBePrivate")
  object IrcCap {
    // NOTE: If you add or modify the constants below, update the knownCaps list.
    /**
     * Account change notification.
     *
     * http://ircv3.net/specs/extensions/account-notify-3.1.html
     */
    const val ACCOUNT_NOTIFY = "account-notify"
    /**
     * Magic number for WHOX, used to ignore user-requested WHOX replies from servers
     *
     * If a user initiates a WHOX, there's no easy way to tell what fields were requested.  It's
     * simpler to not attempt to parse data from user-requested WHOX replies.
     */
    const val ACCOUNT_NOTIFY_WHOX_NUM = 369
    /**
     * Away change notification.
     *
     * http://ircv3.net/specs/extensions/away-notify-3.1.html
     */
    const val AWAY_NOTIFY = "away-notify"
    /**
     * Capability added/removed notification.
     *
     * This is implicitly enabled via CAP LS 302, and is here for servers that only partially
     * support IRCv3.2.
     *
     * http://ircv3.net/specs/extensions/cap-notify-3.2.html
     */
    const val CAP_NOTIFY = "cap-notify"
    /**
     * Hostname/user changed notification.
     *
     * http://ircv3.net/specs/extensions/chghost-3.2.html
     */
    const val CHGHOST = "chghost"
    /**
     * Extended join information.
     *
     * http://ircv3.net/specs/extensions/extended-join-3.1.html
     */
    const val EXTENDED_JOIN = "extended-join"
    /**
     * Multiple mode prefixes in MODE and WHO replies.
     *
     * http://ircv3.net/specs/extensions/multi-prefix-3.1.html
     */
    const val MULTI_PREFIX = "multi-prefix"
    /**
     * SASL authentication.
     *
     * http://ircv3.net/specs/extensions/sasl-3.2.html
     */
    const val SASL = "sasl"
    /**
     * Userhost in names replies.
     *
     * http://ircv3.net/specs/extensions/userhost-in-names-3.2.html
     */
    const val USERHOST_IN_NAMES = "userhost-in-names"

    /**
     * Vendor-specific capabilities
     */
    object Vendor {
      /**
       * Self message support, as recognized by ZNC.
       *
       * Some servers (e.g. Bitlbee) assume self-message support; ZNC requires a capability
       * instead.  As self-message is already implemented, there's little reason to not do this.
       *
       * More information in the IRCv3 commit that removed the 'self-message' capability.
       *
       * https://github.com/ircv3/ircv3-specifications/commit/1bfba47843c2526707c902034b3395af934713c8
       */
      const val ZNC_SELF_MESSAGE = "znc.in/self-message"
    }

    /**
     * List of capabilities currently implemented and requested during capability negotiation.
     */
    val knownCaps = listOf(
      ACCOUNT_NOTIFY,
      AWAY_NOTIFY,
      CAP_NOTIFY,
      CHGHOST,
      EXTENDED_JOIN,
      MULTI_PREFIX,
      SASL,
      USERHOST_IN_NAMES,
      Vendor::ZNC_SELF_MESSAGE
    )
    // NOTE: If you modify the knownCaps list, update the constants above as needed.
    /**
     * SASL authentication mechanisms
     *
     * http://ircv3.net/specs/extensions/sasl-3.1.html
     */
    object SaslMech {
      /**
       * PLAIN authentication, e.g. hashed password
       */
      const val PLAIN = "PLAIN"
      /**
       * EXTERNAL authentication, e.g. SSL certificate and keys
       */
      const val EXTERNAL = "EXTERNAL"
    }
  }
}
