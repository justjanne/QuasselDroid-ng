package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.ARG
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import org.threeten.bp.Instant

@Syncable(name = "IrcUser")
interface IIrcUser : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun addUserModes(modes: String) {
    SYNC("addUserModes", ARG(modes, Type.QString))
  }

  fun joinChannel(channel: IrcChannel, skip_channel_join: Boolean = false) {
  }

  @Slot
  fun joinChannel(channelname: String) {
    SYNC("joinChannel", ARG(channelname, Type.QString))
  }

  fun partChannel(channel: IrcChannel) {
  }

  @Slot
  fun partChannel(channelname: String) {
    SYNC("partChannel", ARG(channelname, Type.QString))
  }

  @Slot
  fun quit() {
    SYNC("quit")
  }

  @Slot
  fun removeUserModes(modes: String) {
    SYNC("removeUserModes", ARG(modes, Type.QString))
  }

  @Slot
  fun setAccount(account: String) {
    SYNC("setAccount", ARG(account, Type.QString))
  }

  @Slot
  fun setAway(away: Boolean) {
    SYNC("setAway", ARG(away, Type.Bool))
  }

  @Slot
  fun setAwayMessage(awayMessage: String) {
    SYNC("setAwayMessage", ARG(awayMessage, Type.QString))
  }

  @Slot
  fun setEncrypted(encrypted: Boolean) {
    SYNC("setEncrypted", ARG(encrypted, Type.Bool))
  }

  @Slot
  fun setHost(host: String) {
    SYNC("setHost", ARG(host, Type.QString))
  }

  @Slot
  fun setIdleTime(idleTime: Instant) {
    SYNC("setIdleTime", ARG(idleTime, Type.QDateTime))
  }

  @Slot
  fun setIrcOperator(ircOperator: String) {
    SYNC("setIrcOperator", ARG(ircOperator, Type.QString))
  }

  @Slot
  fun setLastAwayMessage(lastAwayMessage: Int) {
    SYNC("setLastAwayMessage", ARG(lastAwayMessage, Type.Int))
  }

  @Slot
  fun setLoginTime(loginTime: Instant) {
    SYNC("setLoginTime", ARG(loginTime, Type.QDateTime))
  }

  @Slot
  fun setNick(nick: String) {
    SYNC("setNick", ARG(nick, Type.QString))
  }

  @Slot
  fun setRealName(realName: String) {
    SYNC("setRealName", ARG(realName, Type.QString))
  }

  @Slot
  fun setServer(server: String) {
    SYNC("setServer", ARG(server, Type.QString))
  }

  @Slot
  fun setSuserHost(suserHost: String) {
    SYNC("setSuserHost", ARG(suserHost, Type.QString))
  }

  @Slot
  fun setUser(user: String) {
    SYNC("setUser", ARG(user, Type.QString))
  }

  @Slot
  fun setUserModes(modes: String) {
    SYNC("setUserModes", ARG(modes, Type.QString))
  }

  @Slot
  fun setWhoisServiceReply(whoisServiceReply: String) {
    SYNC("setWhoisServiceReply", ARG(whoisServiceReply, Type.QString))
  }

  @Slot
  fun updateHostmask(mask: String) {
    SYNC("updateHostmask", ARG(mask, Type.QString))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
