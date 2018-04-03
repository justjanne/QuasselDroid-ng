package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import org.threeten.bp.Instant

@Syncable(name = "IrcUser")
interface IIrcUser : ISyncableObject {
  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)
  @Slot
  fun addUserModes(modes: String)

  fun joinChannel(channel: IrcChannel, skip_channel_join: Boolean = false)
  @Slot
  fun joinChannel(channelname: String)

  fun partChannel(channel: IrcChannel)
  @Slot
  fun partChannel(channelname: String)

  @Slot
  fun quit()

  @Slot
  fun removeUserModes(modes: String)

  @Slot
  fun setAccount(account: String)

  @Slot
  fun setAway(away: Boolean)

  @Slot
  fun setAwayMessage(awayMessage: String)

  @Slot
  fun setEncrypted(encrypted: Boolean)

  @Slot
  fun setHost(host: String)

  @Slot
  fun setIdleTime(idleTime: Instant)

  @Slot
  fun setIrcOperator(ircOperator: String)

  @Slot
  fun setLastAwayMessage(lastAwayMessage: Int)

  @Slot
  fun setLoginTime(loginTime: Instant)

  @Slot
  fun setNick(nick: String)

  @Slot
  fun setRealName(realName: String)

  @Slot
  fun setServer(server: String)

  @Slot
  fun setSuserHost(suserHost: String)

  @Slot
  fun setUser(user: String)

  @Slot
  fun setUserModes(modes: String)

  @Slot
  fun setWhoisServiceReply(whoisServiceReply: String)

  @Slot
  fun updateHostmask(mask: String)

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
