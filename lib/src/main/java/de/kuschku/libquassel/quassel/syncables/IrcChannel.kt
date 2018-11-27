/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
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

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.quassel.syncables.interfaces.IIrcChannel
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.helpers.getOr
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.nio.charset.Charset

class IrcChannel(
  name: String,
  network: Network,
  proxy: SignalProxy
) : SyncableObject(proxy, "IrcChannel"), IIrcChannel {
  override fun init() {
    if (name().isEmpty()) {
      println("Error: channelName is empty")
    }
    renameObject("${network().networkId()}/${name()}")
  }

  override fun toVariantMap(): QVariantMap = mapOf(
    "ChanModes" to QVariant.of(initChanModes(), Type.QVariantMap),
    "UserModes" to QVariant.of(initUserModes(), Type.QVariantMap)
  ) + initProperties()

  private inline fun QVariant_?.indexed(index: Int?) = this?.let {
    index?.let { i ->
      it.valueOr<QVariantList>(::emptyList)[i]
    } ?: it
  }

  fun fromVariantMap(properties: QVariantMap, i: Int? = null) {
    initSetChanModes(properties["ChanModes"].indexed(i).valueOr(::emptyMap))
    initSetUserModes(properties["UserModes"].indexed(i).valueOr(::emptyMap))
    initSetProperties(properties, i)
  }

  override fun fromVariantMap(properties: QVariantMap) {
    initSetChanModes(properties["ChanModes"].valueOr(::emptyMap))
    initSetUserModes(properties["UserModes"].valueOr(::emptyMap))
    initSetProperties(properties)
  }

  override fun initChanModes(): QVariantMap = mapOf(
    "A" to QVariant.of(_A_channelModes.map { (key, value) ->
      key.toString() to QVariant.of(value.toList(), Type.QStringList)
    }.toMap(), Type.QVariantMap),
    "B" to QVariant.of(_B_channelModes.map { (key, value) ->
      key.toString() to QVariant.of(value, Type.QString)
    }.toMap(), Type.QVariantMap),
    "C" to QVariant.of(_C_channelModes.map { (key, value) ->
      key.toString() to QVariant.of(value, Type.QString)
    }.toMap(), Type.QVariantMap),
    "D" to QVariant.of(_D_channelModes.joinToString(), Type.QString)
  )

  override fun initUserModes(): QVariantMap = _userModes.entries.map { (key, value) ->
    key.nick() to QVariant.of(value, Type.QString)
  }.toMap()

  override fun initProperties(): QVariantMap = mapOf(
    "name" to QVariant.of(name(), Type.QString),
    "topic" to QVariant.of(topic(), Type.QString),
    "password" to QVariant.of(password(), Type.QString),
    "encrypted" to QVariant.of(encrypted(), Type.Bool)
  )

  override fun initSetChanModes(chanModes: QVariantMap) {
    chanModes["A"].valueOr<QVariantMap>(::emptyMap).forEach { (key, variant) ->
      _A_channelModes[key.toCharArray().first()] =
        variant.valueOr<QStringList>(::emptyList).filterNotNull().toMutableSet()
    }
    chanModes["B"].valueOr<QVariantMap>(::emptyMap).forEach { (key, variant) ->
      _B_channelModes[key.toCharArray().first()] = variant.value("")
    }
    chanModes["C"].valueOr<QVariantMap>(::emptyMap).forEach { (key, variant) ->
      _C_channelModes[key.toCharArray().first()] = variant.value("")
    }
    _D_channelModes = chanModes["D"].value("").toCharArray().toMutableSet()
  }

  override fun initSetUserModes(usermodes: QVariantMap) {
    val users = usermodes.map { (key, _) ->
      network().newIrcUser(key)
    }
    val modes = usermodes.map { (_, value) ->
      value.value("")
    }
    joinIrcUsersInternal(users, modes)
  }

  override fun initSetProperties(properties: QVariantMap, i: Int?) {
    setTopic(properties["topic"].indexed(i).valueOr(this::topic))
    setPassword(properties["password"].indexed(i).valueOr(this::password))
    setEncrypted(properties["encrypted"].indexed(i).valueOr(this::encrypted))
  }

  fun isKnownUser(ircUser: IrcUser): Boolean {
    return _userModes.contains(ircUser)
  }

  fun isValidChannelUserMode(mode: String): Boolean {
    return mode.length <= 1
  }

  fun name() = _name
  fun topic() = _topic
  fun password() = _password
  fun encrypted() = _encrypted
  fun network() = _network
  fun ircUsers() = _userModes.keys
  fun liveIrcUsers(): Observable<MutableSet<IrcUser>> =
    live_userModes.map(MutableMap<IrcUser, String>::keys)

  fun userModes(ircUser: IrcUser) = _userModes.getOr(ircUser, "")
  fun liveUserModes(ircUser: IrcUser) = live_userModes.map {
    _userModes.getOr(ircUser, "")
  }

  fun userCount() = _userCount

  fun userModes(): Map<IrcUser, String> = _userModes
  fun userModes(nick: String) = network().ircUser(nick)?.let { userModes(it) } ?: ""
  fun liveUserModes(nick: String) = network().ircUser(nick)?.let { userModes(it) } ?: ""

  fun updates(): Observable<IrcChannel> = live_updates.map { this }

  fun hasMode(mode: Char) = when (network().channelModeType(mode)) {
    INetwork.ChannelModeType.A_CHANMODE ->
      _A_channelModes.contains(mode)
    INetwork.ChannelModeType.B_CHANMODE ->
      _B_channelModes.contains(mode)
    INetwork.ChannelModeType.C_CHANMODE ->
      _C_channelModes.contains(mode)
    INetwork.ChannelModeType.D_CHANMODE ->
      _D_channelModes.contains(mode)
    else                                ->
      false
  }

  fun modeValue(mode: Char) = when (network().channelModeType(mode)) {
    INetwork.ChannelModeType.B_CHANMODE ->
      _B_channelModes.getOr(mode, "")
    INetwork.ChannelModeType.C_CHANMODE ->
      _C_channelModes.getOr(mode, "")
    else                                ->
      ""
  }

  fun modeValueList(mode: Char): Set<String> = when (network().channelModeType(mode)) {
    INetwork.ChannelModeType.A_CHANMODE ->
      _A_channelModes.getOrElse(mode, ::emptySet)
    else                                ->
      emptySet()
  }

  fun channelModeString(): String {
    val modeString = StringBuffer(_D_channelModes.joinToString())
    val params = mutableListOf<String>()
    _C_channelModes.entries.forEach { (key, value) ->
      modeString.append(key)
      params.add(value)
    }
    _B_channelModes.entries.forEach { (key, value) ->
      modeString.append(key)
      params.add(value)
    }
    return if (modeString.isBlank()) {
      ""
    } else {
      "+$modeString ${params.joinToString(" ")}"
    }
  }

  fun codecForEncoding() = _codecForEncoding
  fun codecForDecoding() = _codecForDecoding
  fun setCodecForEncoding(codecName: String) {
    val charset = Charset.availableCharsets()[codecName]
    if (charset != null) {
      setCodecForEncoding(charset)
    }
  }

  fun setCodecForEncoding(codec: Charset) {
    _codecForEncoding = codec
  }

  fun setCodecForDecoding(codecName: String) {
    val charset = Charset.availableCharsets()[codecName]
    if (charset != null) {
      setCodecForDecoding(charset)
    }
  }

  fun setCodecForDecoding(codec: Charset) {
    _codecForDecoding = codec
  }

  override fun setTopic(topic: String) {
    if (_topic == topic)
      return
    _topic = topic
  }

  override fun setPassword(password: String) {
    if (_password == password)
      return
    _password = password
  }

  override fun setEncrypted(encrypted: Boolean) {
    if (_encrypted == encrypted)
      return
    _encrypted = encrypted
  }

  override fun joinIrcUsers(nicks: QStringList, modes: QStringList) {
    val (rawUsers, rawModes) = nicks.zip(modes)
      .map { network().ircUser(it.first) to it.second }
      .filter { it.first != null }
      .map { Pair(it.first!!, it.second ?: "") }.unzip()
    joinIrcUsersInternal(rawUsers, rawModes)
  }

  private fun joinIrcUsersInternal(rawUsers: List<IrcUser>, rawModes: List<String>) {
    val users = rawUsers.zip(rawModes)
    val newNicks = users.filter { !_userModes.contains(it.first) }
    val oldNicks = users.filter { _userModes.contains(it.first) }
    for ((user, modes) in oldNicks) {
      modes.forEach { mode ->
        addUserMode(user, mode)
      }
    }
    for ((user, modes) in newNicks) {
      _userModes[user] = modes
      user.joinChannel(this, true)
    }
    updateUsers()
  }

  override fun joinIrcUser(ircuser: IrcUser) {
    joinIrcUsersInternal(listOf(ircuser), listOf(""))
  }

  override fun part(ircuser: IrcUser?) {
    if (ircuser == null)
      return
    if (!isKnownUser(ircuser))
      return
    _userModes.remove(ircuser)
    ircuser.partChannel(this)
    if (network().isMe(ircuser) || _userModes.isEmpty()) {
      for (user in _userModes.keys) {
        user.partChannel(this)
      }
      _userModes.clear()
      network().removeIrcChannel(this)
      proxy.stopSynchronize(this)
    }
    updateUsers()
  }

  override fun part(nick: String) {
    part(network().ircUser(nick))
  }

  override fun setUserModes(ircuser: IrcUser?, modes: String) {
    if (ircuser == null || !isKnownUser(ircuser))
      return
    _userModes[ircuser] = modes
    updateUsers()
  }

  override fun setUserModes(nick: String, modes: String) {
    setUserModes(network().ircUser(nick), modes)
  }

  fun addUserMode(ircuser: IrcUser, mode: Char) {
    addUserMode(ircuser, String(charArrayOf(mode)))
  }

  override fun addUserMode(ircuser: IrcUser?, mode: String) {
    if (ircuser == null || !isKnownUser(ircuser) || !isValidChannelUserMode(mode))
      return
    if (_userModes.getOr(ircuser, "").contains(mode, ignoreCase = true))
      return
    _userModes[ircuser] = _userModes.getOr(ircuser, "") + mode
    updateUsers()
  }

  override fun addUserMode(nick: String, mode: String) {
    addUserMode(network().ircUser(nick), mode)
  }

  override fun removeUserMode(ircuser: IrcUser?, mode: String) {
    if (ircuser == null || !isKnownUser(ircuser) || !isValidChannelUserMode(mode))
      return
    if (!_userModes.getOr(ircuser, "").contains(mode, ignoreCase = true))
      return
    _userModes[ircuser] = _userModes.getOr(ircuser, "")
      .replace(mode, "", ignoreCase = true)
    updateUsers()
  }

  override fun removeUserMode(nick: String, mode: String) {
    removeUserMode(network().ircUser(nick), mode)
  }

  override fun addChannelMode(mode: Char, value: String?) {
    when (network().channelModeType(mode)) {
      INetwork.ChannelModeType.A_CHANMODE     ->
        _A_channelModes.getOrPut(mode, ::mutableSetOf).add(value!!)
      INetwork.ChannelModeType.B_CHANMODE     ->
        _B_channelModes[mode] = value!!
      INetwork.ChannelModeType.C_CHANMODE     ->
        _C_channelModes[mode] = value!!
      INetwork.ChannelModeType.D_CHANMODE     ->
        _D_channelModes.add(mode)
      INetwork.ChannelModeType.NOT_A_CHANMODE ->
        throw IllegalArgumentException("Received invalid channel mode: $mode $value")
    }
  }

  override fun removeChannelMode(mode: Char, value: String?) {
    when (network().channelModeType(mode)) {
      INetwork.ChannelModeType.A_CHANMODE     ->
        _A_channelModes.getOrPut(mode, ::mutableSetOf).remove(value)
      INetwork.ChannelModeType.B_CHANMODE     ->
        _B_channelModes.remove(mode)
      INetwork.ChannelModeType.C_CHANMODE     ->
        _C_channelModes.remove(mode)
      INetwork.ChannelModeType.D_CHANMODE     ->
        _D_channelModes.remove(mode)
      INetwork.ChannelModeType.NOT_A_CHANMODE ->
        throw IllegalArgumentException("Received invalid channel mode: $mode $value")
    }
  }

  override fun update(properties: QVariantMap) {
    fromVariantMap(properties)
  }

  private val live_updates = BehaviorSubject.createDefault(Unit)
  private var _name: String = name
    set(value) {
      field = value
      live_updates.onNext(Unit)
    }

  private var _userCount: Int = 0
    set(value) {
      field = value
      live_updates.onNext(Unit)
    }

  private var _topic: String = ""
    set(value) {
      field = value
      live_updates.onNext(Unit)
    }

  private var _password: String = ""
    set(value) {
      field = value
      live_updates.onNext(Unit)
    }

  private var _encrypted: Boolean = false
    set(value) {
      field = value
      live_updates.onNext(Unit)
    }

  private fun updateUsers() {
    _userCount = _userModes.size
    live_userModes.onNext(_userModes)
  }

  private val live_userModes = BehaviorSubject.createDefault(mutableMapOf<IrcUser, String>())
  private var _userModes: MutableMap<IrcUser, String>
    get() = live_userModes.value!!
    set(value) {
      updateUsers()
    }

  private var _network: Network = network

  private var _codecForEncoding: Charset? = null
  private var _codecForDecoding: Charset? = null

  var _A_channelModes: MutableMap<Char, MutableSet<String>> = mutableMapOf()
  var _B_channelModes: MutableMap<Char, String> = mutableMapOf()
  var _C_channelModes: MutableMap<Char, String> = mutableMapOf()
  var _D_channelModes: MutableSet<Char> = mutableSetOf()

  companion object {
    val NULL = IrcChannel("", Network.NULL, SignalProxy.NULL)
  }
}
