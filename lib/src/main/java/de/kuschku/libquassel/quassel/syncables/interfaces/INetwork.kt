package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.util.flag.Flag
import de.kuschku.libquassel.util.flag.Flags
import de.kuschku.libquassel.util.helpers.serializeString
import java.io.Serializable
import java.nio.ByteBuffer

@Syncable(name = "Network")
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

  @Slot
  fun acknowledgeCap(capability: String)

  @Slot
  fun addCap(capability: String, value: String?)

  @Slot
  fun addIrcChannel(channel: String)

  @Slot
  fun addIrcUser(hostmask: String)

  @Slot
  fun addSupport(param: String, value: String? = null)

  @Slot
  fun clearCaps()

  @Slot
  fun emitConnectionError(error: String)

  @Slot
  fun ircUserNickChanged(old: String, new: String)

  @Slot
  fun removeCap(capability: String)

  @Slot
  fun removeSupport(param: String)

  @Slot
  fun requestConnect() {
    REQUEST("requestConnect")
  }

  @Slot
  fun requestDisconnect() {
    REQUEST("requestDisconnect")
  }

  @Slot
  fun requestSetNetworkInfo(info: NetworkInfo) {
    REQUEST("requestSetNetworkInfo", ARG(info, QType.NetworkInfo))
  }

  @Slot
  fun setAutoIdentifyPassword(password: String)

  @Slot
  fun setAutoIdentifyService(service: String)

  @Slot
  fun setAutoReconnectInterval(interval: UInt)

  @Slot
  fun setAutoReconnectRetries(retries: UShort)

  @Slot
  fun setCodecForDecoding(codecName: ByteBuffer?)

  @Slot
  fun setCodecForEncoding(codecName: ByteBuffer?)

  @Slot
  fun setCodecForServer(codecName: ByteBuffer?)

  @Slot
  fun setConnected(isConnected: Boolean)

  @Slot
  fun setConnectionState(state: Int)

  @Slot
  fun setCurrentServer(currentServer: String?)

  @Slot
  fun setIdentity(identity: IdentityId)

  @Slot
  fun setLatency(latency: Int)

  @Slot
  fun setMessageRateBurstSize(burstSize: UInt)

  @Slot
  fun setMessageRateDelay(messageDelay: UInt)

  @Slot
  fun setMyNick(mynick: String?)

  @Slot
  fun setNetworkName(networkName: String)

  @Slot
  fun setPerform(perform: QStringList)

  @Slot
  fun setRejoinChannels(rejoinChannels: Boolean)

  @Slot
  fun setSaslAccount(account: String)

  @Slot
  fun setSaslPassword(password: String)

  @Slot
  fun setServerList(serverList: QVariantList)

  @Slot
  fun setUnlimitedMessageRate(unlimitedRate: Boolean)

  @Slot
  fun setUnlimitedReconnectRetries(unlimitedRetries: Boolean)

  @Slot
  fun setUseAutoIdentify(autoIdentify: Boolean)

  @Slot
  fun setUseAutoReconnect(autoReconnect: Boolean)

  @Slot
  fun setUseCustomMessageRate(useCustomRate: Boolean)

  @Slot
  fun setUseRandomServer(randomServer: Boolean)

  @Slot
  fun setUseSasl(sasl: Boolean)

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }

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
  enum class ChannelModeType(override val bit: Int) :
    Flag<ChannelModeType> {
    NOT_A_CHANMODE(0x00),
    A_CHANMODE(0x01),
    B_CHANMODE(0x02),
    C_CHANMODE(0x04),
    D_CHANMODE(0x08);

    companion object : Flags.Factory<ChannelModeType> {
      override val NONE = ChannelModeType.of()
      val validValues = values().filter { it.bit != 0 }.toTypedArray()
      override fun of(bit: Int) = Flags.of<ChannelModeType>(bit)
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
    PORT_PLAINTEXT(6667),
    /** Default port for encrypted connections */
    PORT_SSL(6697)
  }

  data class Server(
    val host: String = "",
    val port: UInt = PortDefaults.PORT_PLAINTEXT.port,
    val password: String = "",
    val useSsl: Boolean = false,
    val sslVerify: Boolean = false,
    val sslVersion: Int = 0,
    val useProxy: Boolean = false,
    val proxyType: Int = ProxyType.Socks5Proxy.value,
    val proxyHost: String = "localhost",
    val proxyPort: UInt = 8080,
    val proxyUser: String = "",
    val proxyPass: String = ""
  ) : Serializable {
    fun toVariantMap(): QVariantMap = mapOf(
      "Host" to QVariant.of(host, Type.QString),
      "Port" to QVariant.of(port, Type.UInt),
      "Password" to QVariant.of(password, Type.QString),
      "UseSSL" to QVariant.of(useSsl, Type.Bool),
      "sslVerify" to QVariant.of(sslVerify, Type.Bool),
      "sslVersion" to QVariant.of(sslVersion, Type.Int),
      "UseProxy" to QVariant.of(useProxy, Type.Bool),
      "ProxyType" to QVariant.of(proxyType, Type.Int),
      "ProxyHost" to QVariant.of(proxyHost, Type.QString),
      "ProxyPort" to QVariant.of(proxyPort, Type.UInt),
      "ProxyUser" to QVariant.of(proxyUser, Type.QString),
      "ProxyPass" to QVariant.of(proxyPass, Type.QString)
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
        proxyPort = map["ProxyPort"].value(8080),
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
    var networkId: NetworkId = -1,
    var networkName: String = "",
    var identity: IdentityId = -1,
    // unused
    var useCustomEncodings: Boolean = false,
    var codecForServer: String = "UTF_8",
    var codecForEncoding: String = "UTF_8",
    var codecForDecoding: String = "UTF_8",
    var serverList: List<INetwork.Server> = emptyList(),
    var useRandomServer: Boolean = false,
    var perform: List<String> = emptyList(),
    var useAutoIdentify: Boolean = false,
    var autoIdentifyService: String = "",
    var autoIdentifyPassword: String = "",
    var useSasl: Boolean = false,
    var saslAccount: String = "",
    var saslPassword: String = "",
    var useAutoReconnect: Boolean = true,
    var autoReconnectInterval: Int = 0,
    var autoReconnectRetries: Short = 0,
    var unlimitedReconnectRetries: Boolean = true,
    var rejoinChannels: Boolean = true,
    var useCustomMessageRate: Boolean = false,
    var messageRateBurstSize: Int = 0,
    var messageRateDelay: Int = 0,
    var unlimitedMessageRate: Boolean = false
  ) {
    fun toVariantMap() = mapOf(
      "NetworkId" to QVariant.of(networkId, QType.NetworkId),
      "NetworkName" to QVariant.of(networkName, Type.QString),
      "Identity" to QVariant.of(identity, QType.IdentityId),
      "UseCustomEncodings" to QVariant.of(useCustomEncodings, Type.Bool),
      "CodecForServer" to QVariant.of(
        codecForServer.serializeString(StringSerializer.UTF8), Type.QByteArray
      ),
      "CodecForEncoding" to QVariant.of(
        codecForEncoding.serializeString(StringSerializer.UTF8), Type.QByteArray
      ),
      "CodecForDecoding" to QVariant.of(
        codecForDecoding.serializeString(StringSerializer.UTF8), Type.QByteArray
      ),
      "ServerList" to QVariant.of(serverList.map {
        QVariant.of(it.toVariantMap(), QType.Network_Server)
      }, Type.QVariantList),
      "UseRandomServer" to QVariant.of(useRandomServer, Type.Bool),
      "Perform" to QVariant.of(perform, Type.QStringList),
      "UseAutoIdentify" to QVariant.of(useAutoIdentify, Type.Bool),
      "AutoIdentifyService" to QVariant.of(autoIdentifyService, Type.QString),
      "AutoIdentifyPassword" to QVariant.of(autoIdentifyPassword, Type.QString),
      "UseSasl" to QVariant.of(useSasl, Type.Bool),
      "SaslAccount" to QVariant.of(saslAccount, Type.QString),
      "SaslPassword" to QVariant.of(saslPassword, Type.QString),
      "UseAutoReconnect" to QVariant.of(useAutoReconnect, Type.Bool),
      "AutoReconnectInterval" to QVariant.of(autoReconnectInterval, Type.UInt),
      "AutoReconnectRetries" to QVariant.of(autoReconnectRetries, Type.Int),
      "UnlimitedReconnectRetries" to QVariant.of(unlimitedReconnectRetries, Type.Bool),
      "RejoinChannels" to QVariant.of(rejoinChannels, Type.Bool),
      "UseCustomMessageRate" to QVariant.of(useCustomMessageRate, Type.Bool),
      "MessageRateBurstSize" to QVariant.of(messageRateBurstSize, Type.UInt),
      "MessageRateDelay" to QVariant.of(messageRateDelay, Type.UInt),
      "UnlimitedMessageRate" to QVariant.of(unlimitedMessageRate, Type.Bool)
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
        INetwork.Server.fromVariantMap(it.value(emptyMap()))
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

  /**
   * IRCv3 capability names and values
   */
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
