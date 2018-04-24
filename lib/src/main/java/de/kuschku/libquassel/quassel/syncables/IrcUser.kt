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

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
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
    "user" to QVariant.of(user(), Type.QString),
    "host" to QVariant.of(host(), Type.QString),
    "nick" to QVariant.of(nick(), Type.QString),
    "realName" to QVariant.of(realName(), Type.QString),
    "account" to QVariant.of(account(), Type.QString),
    "away" to QVariant.of(isAway(), Type.Bool),
    "awayMessage" to QVariant.of(awayMessage(), Type.QString),
    "idleTime" to QVariant.of(idleTime(), Type.QDateTime),
    "loginTime" to QVariant.of(loginTime(), Type.QDateTime),
    "server" to QVariant.of(server(), Type.QString),
    "ircOperator" to QVariant.of(ircOperator(), Type.QString),
    "lastAwayMessage" to QVariant.of(lastAwayMessage(), Type.Int),
    "whoisServiceReply" to QVariant.of(whoisServiceReply(), Type.QString),
    "suserHost" to QVariant.of(suserHost(), Type.QString),
    "encrypted" to QVariant.of(encrypted(), Type.Bool),

    "channels" to QVariant.of(channels(), Type.QStringList),
    "userModes" to QVariant.of(userModes(), Type.QString)
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

  fun updates(): Observable<IrcUser> = hasChangedNotification.map { this }

  fun nick() = _nick
  fun user() = _user
  fun host() = _host
  fun realName() = _realName
  fun account() = _account
  fun hostMask() = "${nick()}!${user()}@${host()}"
  fun isAway() = _away
  fun awayMessage() = _awayMessage
  fun server() = _server
  fun idleTime(): Instant {
    if (Instant.now().epochSecond - _idleTimeSet.epochSecond > 1200)
      _idleTime = Instant.EPOCH
    return _idleTime
  }

  fun loginTime() = _loginTime
  fun ircOperator() = _ircOperator
  fun lastAwayMessage() = _lastAwayMessage
  fun whoisServiceReply() = _whoisServiceReply
  fun suserHost() = _suserHost
  fun encrypted() = _encrypted
  fun network() = _network
  fun userModes() = _userModes
  fun channels() = _channels.map(IrcChannel::name)

  override fun addUserModes(modes: String) {
    (_userModes.toSet() + modes.toSet()).joinToString()
  }

  override fun removeUserModes(modes: String) {
    (_userModes.toSet() - modes.toSet()).joinToString()
  }

  override fun setUser(user: String) {
    if (_user != user) {
      _user = user
    }
  }

  override fun setHost(host: String) {
    if (_host != host) {
      _host = host
    }
  }

  override fun setNick(nick: String) {
    if (nick.isNotEmpty() && _nick != nick) {
      network().ircUserNickChanged(_nick, nick)
      _nick = nick
      updateObjectName()
    }
  }

  override fun setRealName(realName: String) {
    if (_realName != realName) {
      _realName = realName
    }
  }

  override fun setAccount(account: String) {
    if (_account != account) {
      _account = account
    }
  }

  override fun setAway(away: Boolean) {
    if (_away != away) {
      _away = away
    }
  }

  override fun setAwayMessage(awayMessage: String) {
    if (_awayMessage != awayMessage) {
      _awayMessage = awayMessage
    }
  }

  override fun setIdleTime(idleTime: Instant) {
    if (_idleTime != idleTime) {
      _idleTime = idleTime
      _idleTimeSet = Instant.now()
    }
  }

  override fun setLoginTime(loginTime: Instant) {
    if (_loginTime != loginTime) {
      _loginTime = loginTime
    }
  }

  override fun setIrcOperator(ircOperator: String) {
    if (_ircOperator != ircOperator) {
      _ircOperator = ircOperator
    }
  }

  override fun setLastAwayMessage(lastAwayMessage: Int) {
    if (lastAwayMessage > _lastAwayMessage) {
      _lastAwayMessage = lastAwayMessage
    }
  }

  override fun setWhoisServiceReply(whoisServiceReply: String) {
    if (_whoisServiceReply != whoisServiceReply) {
      _whoisServiceReply = whoisServiceReply
    }
  }

  override fun setSuserHost(suserHost: String) {
    if (_suserHost != suserHost) {
      _suserHost = suserHost
    }
  }

  override fun setEncrypted(encrypted: Boolean) {
    if (_encrypted != encrypted) {
      _encrypted = encrypted
    }
  }

  override fun setServer(server: String) {
    if (_server != server) {
      _server = server
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
    proxy.stopSynchronize(this)
  }

  fun updateObjectName() {
    val identifier = "${network().networkId()}/${nick()}"
    renameObject(identifier)
  }

  private val hasChangedNotification = BehaviorSubject.createDefault(Unit)

  private var _nick: String = HostmaskHelper.nick(hostmask)
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _user: String = HostmaskHelper.user(hostmask)
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _host: String = HostmaskHelper.host(hostmask)
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _realName: String = ""
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _account: String = ""
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _awayMessage: String = ""
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _away: Boolean = false
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _server: String = ""
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _idleTime: Instant = Instant.EPOCH
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _idleTimeSet: Instant = Instant.EPOCH
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _loginTime: Instant = Instant.EPOCH
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _ircOperator: String = ""
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _lastAwayMessage: Int = 0
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _whoisServiceReply: String = ""
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _suserHost: String = ""
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _encrypted: Boolean = false
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _channels: MutableSet<IrcChannel> = mutableSetOf()
  private var _userModes: String = ""
    set(value) {
      field = value
      hasChangedNotification.onNext(Unit)
    }
  private var _network: Network = network
  private var _codecForEncoding: Charset? = null
  private var _codecForDecoding: Charset? = null

  companion object {
    val NULL = IrcUser("", Network.NULL, SignalProxy.NULL)
  }
}
