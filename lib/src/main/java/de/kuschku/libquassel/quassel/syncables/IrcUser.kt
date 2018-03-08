package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.valueOr
import de.kuschku.libquassel.quassel.syncables.interfaces.IIrcUser
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.irc.HostmaskHelper
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.Instant
import java.nio.charset.Charset

class IrcUser(
  hostmask: String,
  network: Network,
  proxy: SignalProxy
) : SyncableObject(proxy, "IrcUser"), IIrcUser {
  override fun init() {
    updateObjectName()
  }

  override fun toVariantMap() = initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetProperties(properties)
  }

  override fun initProperties(): QVariantMap = mapOf(
    "user" to QVariant_(user(), Type.QString),
    "host" to QVariant_(host(), Type.QString),
    "nick" to QVariant_(nick(), Type.QString),
    "realName" to QVariant_(realName(), Type.QString),
    "account" to QVariant_(account(), Type.QString),
    "away" to QVariant_(isAway(), Type.Bool),
    "awayMessage" to QVariant_(awayMessage(), Type.QString),
    "idleTime" to QVariant_(idleTime(), Type.QDateTime),
    "loginTime" to QVariant_(loginTime(), Type.QDateTime),
    "server" to QVariant_(server(), Type.QString),
    "ircOperator" to QVariant_(ircOperator(), Type.QString),
    "lastAwayMessage" to QVariant_(lastAwayMessage(), Type.Int),
    "whoisServiceReply" to QVariant_(whoisServiceReply(), Type.QString),
    "suserHost" to QVariant_(suserHost(), Type.QString),
    "encrypted" to QVariant_(encrypted(), Type.Bool),

    "channels" to QVariant_(channels(), Type.QStringList),
    "userModes" to QVariant_(userModes(), Type.QString)
  )

  override fun initSetProperties(properties: QVariantMap) {
    setUser(properties["user"].valueOr(this::user))
    setHost(properties["host"].valueOr(this::host))
    setNick(properties["nick"].valueOr(this::nick))
    setRealName(properties["realName"].valueOr(this::realName))
    setAccount(properties["account"].valueOr(this::account))
    setAway(properties["away"].valueOr(this::isAway))
    setAwayMessage(properties["awayMessage"].valueOr(this::awayMessage))
    setIdleTime(properties["idleTime"].valueOr(this::idleTime))
    setLoginTime(properties["loginTime"].valueOr(this::loginTime))
    setServer(properties["server"].valueOr(this::server))
    setIrcOperator(properties["ircOperator"].valueOr(this::ircOperator))
    setLastAwayMessage(properties["lastAwayMessage"].valueOr(this::lastAwayMessage))
    setWhoisServiceReply(properties["whoisServiceReply"].valueOr(this::whoisServiceReply))
    setSuserHost(properties["suserHost"].valueOr(this::suserHost))
    setEncrypted(properties["encrypted"].valueOr(this::encrypted))

    setUserModes(properties["userModes"].valueOr(this::userModes))
  }

  fun nick() = _nick
  fun liveNick(): Observable<String> = live_nick

  fun user() = _user
  fun liveUser(): Observable<String> = live_user

  fun host() = _host
  fun liveHost(): Observable<String> = live_host

  fun realName() = _realName
  fun liveRealName(): Observable<String> = live_realName

  fun account() = _account
  fun liveAccount(): Observable<String> = live_account

  fun hostMask() = "${nick()}!${user()}@${host()}"
  fun liveHostMask() = liveNick().switchMap { nick ->
    liveUser().switchMap { user ->
      liveHost().map { host ->
        "$nick!$user@$host"
      }
    }
  }

  fun isAway() = _away
  fun liveIsAway(): Observable<Boolean> = live_away

  fun awayMessage() = _awayMessage
  fun liveAwayMessage(): Observable<String> = live_awayMessage

  fun server() = _server
  fun liveServer(): Observable<String> = live_server

  fun idleTime(): Instant {
    if (Instant.now().epochSecond - _idleTimeSet.epochSecond > 1200)
      _idleTime = Instant.EPOCH
    return _idleTime
  }

  fun liveIdleTime(): Observable<Instant> = live_idleTime

  fun loginTime() = _loginTime
  fun liveLoginTime(): Observable<Instant> = live_loginTime

  fun ircOperator() = _ircOperator
  fun liveIrcOperator(): Observable<String> = live_ircOperator

  fun lastAwayMessage() = _lastAwayMessage
  fun liveLastAwayMessage(): Observable<Int> = live_lastAwayMessage

  fun whoisServiceReply() = _whoisServiceReply
  fun liveWhoisServiceReply(): Observable<String> = live_whoisServiceReply

  fun suserHost() = _suserHost
  fun liveSuserHost(): Observable<String> = live_suserHost

  fun encrypted() = _encrypted
  fun liveEncrypted(): Observable<Boolean> = live_encrypted

  fun network() = _network

  fun userModes() = _userModes
  fun liveUserModes(): Observable<String> = live_userModes

  fun channels() = _channels.map(IrcChannel::name)
  fun codecForEncoding() = _codecForEncoding
  fun codecForDecoding() = _codecForDecoding
  fun setCodecForEncoding(codecName: String) = setCodecForEncoding(Charset.forName(codecName))
  fun setCodecForEncoding(codec: Charset) {
    _codecForEncoding = codec
  }

  fun setCodecForDecoding(codecName: String) = setCodecForDecoding(Charset.forName(codecName))
  fun setCodecForDecoding(codec: Charset) {
    _codecForDecoding = codec
  }

  override fun setUser(user: String) {
    if (_user != user) {
      _user = user
      super.setUser(user)
    }
  }

  override fun setHost(host: String) {
    if (_host != host) {
      _host = host
      super.setHost(host)
    }
  }

  override fun setNick(nick: String) {
    if (nick.isNotEmpty() && _nick != nick) {
      network().ircUserNickChanged(_nick, nick)
      _nick = nick
      updateObjectName()
      super.setNick(nick)
    }
  }

  override fun setRealName(realName: String) {
    if (_realName != realName) {
      _realName = realName
      super.setRealName(realName)
    }
  }

  override fun setAccount(account: String) {
    if (_account != account) {
      _account = account
      super.setAccount(account)
    }
  }

  override fun setAway(away: Boolean) {
    if (_away != away) {
      _away = away
      super.setAway(away)
    }
  }

  override fun setAwayMessage(awayMessage: String) {
    if (_awayMessage != awayMessage) {
      _awayMessage = awayMessage
      super.setAwayMessage(awayMessage)
    }
  }

  override fun setIdleTime(idleTime: Instant) {
    if (_idleTime != idleTime) {
      _idleTime = idleTime
      _idleTimeSet = Instant.now()
      super.setIdleTime(idleTime)
    }
  }

  override fun setLoginTime(loginTime: Instant) {
    if (_loginTime != loginTime) {
      _loginTime = loginTime
      super.setLoginTime(loginTime)
    }
  }

  override fun setIrcOperator(ircOperator: String) {
    if (_ircOperator != ircOperator) {
      _ircOperator = ircOperator
      super.setIrcOperator(ircOperator)
    }
  }

  override fun setLastAwayMessage(lastAwayMessage: Int) {
    if (lastAwayMessage > _lastAwayMessage) {
      _lastAwayMessage = lastAwayMessage
      super.setLastAwayMessage(lastAwayMessage)
    }
  }

  override fun setWhoisServiceReply(whoisServiceReply: String) {
    if (_whoisServiceReply != whoisServiceReply) {
      _whoisServiceReply = whoisServiceReply
      super.setWhoisServiceReply(whoisServiceReply)
    }
  }

  override fun setSuserHost(suserHost: String) {
    if (_suserHost != suserHost) {
      _suserHost = suserHost
      super.setSuserHost(suserHost)
    }
  }

  override fun setEncrypted(encrypted: Boolean) {
    if (_encrypted != encrypted) {
      _encrypted = encrypted
      super.setEncrypted(encrypted)
    }
  }

  override fun updateHostmask(mask: String) {
    if (hostMask() != mask) {
      val (user, host, _) = HostmaskHelper.split(mask)
      setUser(user)
      setHost(host)
    }
  }

  override fun setUserModes(modes: String) {
    if (_userModes != modes) {
      _userModes = modes
      super.setUserModes(modes)
    }
  }

  override fun joinChannel(channel: IrcChannel, skip_channel_join: Boolean) {
    if (!_channels.contains(channel)) {
      _channels.add(channel)
      if (!skip_channel_join)
        channel.joinIrcUser(this)
    }
  }

  override fun joinChannel(channelname: String) {
    joinChannel(network().newIrcChannel(channelname))
  }

  override fun partChannel(channel: IrcChannel) {
    if (_channels.contains(channel)) {
      _channels.remove(channel)
      channel.part(this)
      super.partChannel(channel.name())
      if (_channels.isEmpty() && !network().isMe(this))
        quit()
    }
  }

  override fun partChannel(channelname: String) {
    val channel = network().ircChannel(channelname) ?: throw IllegalArgumentException(
      "Received part for unknown channel : $channelname"
    )
    partChannel(channel)
  }

  override fun quit() {
    for (channel in _channels.toList()) {
      channel.part(this)
    }
    _channels.clear()
    network().removeIrcUser(this)
    super.quit()
    proxy.stopSynchronize(this)
  }

  override fun addUserModes(modes: String) {
    super.addUserModes(modes)
  }

  override fun removeUserModes(modes: String) {
    super.removeUserModes(modes)
  }

  fun updateObjectName() {
    val identifier = "${network().networkId()}/${nick()}"
    renameObject(identifier)
  }

  private val live_nick = BehaviorSubject.createDefault(HostmaskHelper.nick(hostmask))
  private var _nick: String
    get() = live_nick.value
    set(value) = live_nick.onNext(value)

  private val live_user = BehaviorSubject.createDefault(HostmaskHelper.user(hostmask))
  private var _user: String
    get() = live_user.value
    set(value) = live_user.onNext(value)

  private val live_host = BehaviorSubject.createDefault(HostmaskHelper.host(hostmask))
  private var _host: String
    get() = live_host.value
    set(value) = live_host.onNext(value)

  private val live_realName = BehaviorSubject.createDefault("")
  private var _realName: String
    get() = live_realName.value
    set(value) = live_realName.onNext(value)

  private val live_account = BehaviorSubject.createDefault("")
  private var _account: String
    get() = live_account.value
    set(value) = live_account.onNext(value)

  private val live_awayMessage = BehaviorSubject.createDefault("")
  private var _awayMessage: String
    get() = live_awayMessage.value
    set(value) = live_awayMessage.onNext(value)

  private val live_away = BehaviorSubject.createDefault(false)
  private var _away: Boolean
    get() = live_away.value
    set(value) = live_away.onNext(value)

  private val live_server = BehaviorSubject.createDefault("")
  private var _server: String
    get() = live_server.value
    set(value) = live_server.onNext(value)

  private val live_idleTime = BehaviorSubject.createDefault(Instant.EPOCH)
  private var _idleTime: Instant
    get() = live_idleTime.value
    set(value) = live_idleTime.onNext(value)

  private val live_idleTimeSet = BehaviorSubject.createDefault(Instant.EPOCH)
  private var _idleTimeSet: Instant
    get() = live_idleTimeSet.value
    set(value) = live_idleTimeSet.onNext(value)

  private val live_loginTime = BehaviorSubject.createDefault(Instant.EPOCH)
  private var _loginTime: Instant
    get() = live_loginTime.value
    set(value) = live_loginTime.onNext(value)

  private val live_ircOperator = BehaviorSubject.createDefault("")
  private var _ircOperator: String
    get() = live_ircOperator.value
    set(value) = live_ircOperator.onNext(value)

  private val live_lastAwayMessage = BehaviorSubject.createDefault(0)
  private var _lastAwayMessage: Int
    get() = live_lastAwayMessage.value
    set(value) = live_lastAwayMessage.onNext(value)

  private val live_whoisServiceReply = BehaviorSubject.createDefault("")
  private var _whoisServiceReply: String
    get() = live_whoisServiceReply.value
    set(value) = live_whoisServiceReply.onNext(value)

  private val live_suserHost = BehaviorSubject.createDefault("")
  private var _suserHost: String
    get() = live_suserHost.value
    set(value) = live_suserHost.onNext(value)

  private val live_encrypted = BehaviorSubject.createDefault(false)
  private var _encrypted: Boolean
    get() = live_encrypted.value
    set(value) = live_encrypted.onNext(value)

  private var _channels: MutableSet<IrcChannel> = mutableSetOf()

  private val live_userModes = BehaviorSubject.createDefault("")
  private var _userModes: String
    get() = live_userModes.value
    set(value) = live_userModes.onNext(value)

  private var _network: Network = network
  private var _codecForEncoding: Charset? = null
  private var _codecForDecoding: Charset? = null

  companion object {
    val NULL = IrcUser("", Network.NULL, SignalProxy.NULL)
  }
}
