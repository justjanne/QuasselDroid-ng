package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.util.Flag
import de.kuschku.libquassel.util.Flags
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
  fun acknowledgeCap(capability: String) {
    SYNC(SLOT, ARG(capability, Type.QString))
  }

  @Slot
  fun addCap(capability: String, value: String = "") {
    SYNC(SLOT, ARG(capability, Type.QString), ARG(value, Type.QString))
  }

  @Slot
  fun addIrcChannel(channel: String) {
    SYNC(SLOT, ARG(channel, Type.QString))
  }

  @Slot
  fun addIrcUser(hostmask: String) {
    SYNC(SLOT, ARG(hostmask, Type.QString))
  }

  @Slot
  fun addSupport(param: String, value: String = String()) {
    SYNC(SLOT, ARG(param, Type.QString), ARG(value, Type.QString))
  }

  @Slot
  fun clearCaps() {
    SYNC(SLOT)
  }

  @Slot
  fun emitConnectionError(error: String) {
    SYNC(SLOT, ARG(error, Type.QString))
  }

  @Slot
  fun ircUserNickChanged(newnick: String)

  @Slot
  fun removeCap(capability: String) {
    SYNC(SLOT, ARG(capability, Type.QString))
  }

  @Slot
  fun removeSupport(param: String) {
    SYNC(SLOT, ARG(param, Type.QString))
  }

  @Slot
  fun requestConnect() {
    REQUEST(SLOT)
  }

  @Slot
  fun requestDisconnect() {
    REQUEST(SLOT)
  }

  @Slot
  fun requestSetNetworkInfo(info: NetworkInfo) {
    REQUEST(SLOT, ARG(info, QType.NetworkInfo))
  }

  @Slot
  fun setAutoIdentifyPassword(password: String) {
    SYNC(SLOT, ARG(password, Type.QString))
  }

  @Slot
  fun setAutoIdentifyService(service: String) {
    SYNC(SLOT, ARG(service, Type.QString))
  }

  @Slot
  fun setAutoReconnectInterval(interval: UInt) {
    SYNC(SLOT, ARG(interval, Type.UInt))
  }

  @Slot
  fun setAutoReconnectRetries(retries: UShort) {
    SYNC(SLOT, ARG(retries, Type.UShort))
  }

  @Slot
  fun setCodecForDecoding(codecName: ByteBuffer?) {
    SYNC(SLOT, ARG(codecName, Type.QByteArray))
  }

  @Slot
  fun setCodecForEncoding(codecName: ByteBuffer?) {
    SYNC(SLOT, ARG(codecName, Type.QByteArray))
  }

  @Slot
  fun setCodecForServer(codecName: ByteBuffer?) {
    SYNC(SLOT, ARG(codecName, Type.QByteArray))
  }

  @Slot
  fun setConnected(isConnected: Boolean) {
    SYNC(SLOT, ARG(isConnected, Type.Bool))
  }

  @Slot
  fun setConnectionState(state: Int) {
    SYNC(SLOT, ARG(state, Type.Int))
  }

  @Slot
  fun setCurrentServer(currentServer: String) {
    SYNC(SLOT, ARG(currentServer, Type.QString))
  }

  @Slot
  fun setIdentity(identity: IdentityId) {
    SYNC(SLOT, ARG(identity, QType.IdentityId))
  }

  @Slot
  fun setLatency(latency: Int) {
    SYNC(SLOT, ARG(latency, Type.Int))
  }

  @Slot
  fun setMessageRateBurstSize(burstSize: UInt) {
    SYNC(SLOT, ARG(burstSize, Type.UInt))
  }

  @Slot
  fun setMessageRateDelay(messageDelay: UInt) {
    SYNC(SLOT, ARG(messageDelay, Type.UInt))
  }

  @Slot
  fun setMyNick(mynick: String) {
    SYNC(SLOT, ARG(mynick, Type.QString))
  }

  @Slot
  fun setNetworkName(networkName: String) {
    SYNC(SLOT, ARG(networkName, Type.QString))
  }

  @Slot
  fun setPerform(perform: QStringList) {
    SYNC(SLOT, ARG(perform, Type.QStringList))
  }

  @Slot
  fun setRejoinChannels(rejoinChannels: Boolean) {
    SYNC(SLOT, ARG(rejoinChannels, Type.Bool))
  }

  @Slot
  fun setSaslAccount(account: String) {
    SYNC(SLOT, ARG(account, Type.QString))
  }

  @Slot
  fun setSaslPassword(password: String) {
    SYNC(SLOT, ARG(password, Type.QString))
  }

  @Slot
  fun setServerList(serverList: QVariantList) {
    SYNC(SLOT, ARG(serverList, Type.QVariantList))
  }

  @Slot
  fun setUnlimitedMessageRate(unlimitedRate: Boolean) {
    SYNC(SLOT, ARG(unlimitedRate, Type.Bool))
  }

  @Slot
  fun setUnlimitedReconnectRetries(unlimitedRetries: Boolean) {
    SYNC(SLOT, ARG(unlimitedRetries, Type.Bool))
  }

  @Slot
  fun setUseAutoIdentify(autoIdentify: Boolean) {
    SYNC(SLOT, ARG(autoIdentify, Type.Bool))
  }

  @Slot
  fun setUseAutoReconnect(autoReconnect: Boolean) {
    SYNC(SLOT, ARG(autoReconnect, Type.Bool))
  }

  @Slot
  fun setUseCustomMessageRate(useCustomRate: Boolean) {
    SYNC(SLOT, ARG(useCustomRate, Type.Bool))
  }

  @Slot
  fun setUseRandomServer(randomServer: Boolean) {
    SYNC(SLOT, ARG(randomServer, Type.Bool))
  }

  @Slot
  fun setUseSasl(sasl: Boolean) {
    SYNC(SLOT, ARG(sasl, Type.Bool))
  }

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
        ConnectionState::value)

      fun of(value: Int) = byId[value] ?: Disconnected
    }
  }

  /**
   * {@see http://www.irc.org/tech_docs/005.html}
   * {@see http://www.irc.org/tech_docs/draft-brocklesby-irc-isupport-03.txt}
   */
  enum class ChannelModeType(override val bit: Int) : Flag<ChannelModeType> {
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
  ) {
    fun toVariantMap(): QVariantMap = mapOf(
      "Host" to QVariant_(host, Type.QString),
      "Port" to QVariant_(port, Type.UInt),
      "Password" to QVariant_(password, Type.QString),
      "UseSSL" to QVariant_(useSsl, Type.Bool),
      "sslVerify" to QVariant_(sslVerify, Type.Bool),
      "sslVersion" to QVariant_(sslVersion, Type.Int),
      "UseProxy" to QVariant_(useProxy, Type.Bool),
      "ProxyType" to QVariant_(proxyType, Type.Bool),
      "ProxyHost" to QVariant_(proxyHost, Type.QString),
      "ProxyPort" to QVariant_(proxyPort, Type.UInt),
      "ProxyUser" to QVariant_(proxyUser, Type.QString),
      "ProxyPass" to QVariant_(proxyPass, Type.QString)
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
      private val byId = enumValues<ProxyType>().associateBy(
        ProxyType::value)

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
  )

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
