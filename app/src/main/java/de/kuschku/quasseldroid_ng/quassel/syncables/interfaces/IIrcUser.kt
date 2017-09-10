package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.ARG
import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.SLOT
import de.kuschku.quasseldroid_ng.protocol.Type
import de.kuschku.quasseldroid_ng.quassel.syncables.IrcChannel
import org.threeten.bp.Instant

@Syncable(name = "IrcUser")
interface IIrcUser : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun addUserModes(modes: String) {
    SYNC(SLOT, ARG(modes, Type.QString))
  }

  fun joinChannel(channel: IrcChannel, skip_channel_join: Boolean = false) {
  }

  @Slot
  fun joinChannel(channelname: String) {
    SYNC(SLOT, ARG(channelname, Type.QString))
  }

  fun partChannel(channel: IrcChannel) {
  }

  @Slot
  fun partChannel(channelname: String) {
    SYNC(SLOT, ARG(channelname, Type.QString))
  }

  @Slot
  fun quit() {
    SYNC(SLOT)
  }

  @Slot
  fun removeUserModes(modes: String) {
    SYNC(SLOT, ARG(modes, Type.QString))
  }

  @Slot
  fun setAccount(account: String) {
    SYNC(SLOT, ARG(account, Type.QString))
  }

  @Slot
  fun setAway(away: Boolean) {
    SYNC(SLOT, ARG(away, Type.Bool))
  }

  @Slot
  fun setAwayMessage(awayMessage: String) {
    SYNC(SLOT, ARG(awayMessage, Type.QString))
  }

  @Slot
  fun setEncrypted(encrypted: Boolean) {
    SYNC(SLOT, ARG(encrypted, Type.Bool))
  }

  @Slot
  fun setHost(host: String) {
    SYNC(SLOT, ARG(host, Type.QString))
  }

  @Slot
  fun setIdleTime(idleTime: Instant) {
    SYNC(SLOT, ARG(idleTime, Type.QDateTime))
  }

  @Slot
  fun setIrcOperator(ircOperator: String) {
    SYNC(SLOT, ARG(ircOperator, Type.QString))
  }

  @Slot
  fun setLastAwayMessage(lastAwayMessage: Int) {
    SYNC(SLOT, ARG(lastAwayMessage, Type.Int))
  }

  @Slot
  fun setLoginTime(loginTime: Instant) {
    SYNC(SLOT, ARG(loginTime, Type.QDateTime))
  }

  @Slot
  fun setNick(nick: String) {
    SYNC(SLOT, ARG(nick, Type.QString))
  }

  @Slot
  fun setRealName(realName: String) {
    SYNC(SLOT, ARG(realName, Type.QString))
  }

  @Slot
  fun setServer(server: String) {
    SYNC(SLOT, ARG(server, Type.QString))
  }

  @Slot
  fun setSuserHost(suserHost: String) {
    SYNC(SLOT, ARG(suserHost, Type.QString))
  }

  @Slot
  fun setUser(user: String) {
    SYNC(SLOT, ARG(user, Type.QString))
  }

  @Slot
  fun setUserModes(modes: String) {
    SYNC(SLOT, ARG(modes, Type.QString))
  }

  @Slot
  fun setWhoisServiceReply(whoisServiceReply: String) {
    SYNC(SLOT, ARG(whoisServiceReply, Type.QString))
  }

  @Slot
  fun updateHostmask(mask: String) {
    SYNC(SLOT, ARG(mask, Type.QString))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
