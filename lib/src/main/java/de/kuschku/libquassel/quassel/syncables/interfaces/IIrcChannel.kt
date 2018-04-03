package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.QStringList
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.quassel.syncables.IrcUser

@Syncable(name = "IrcChannel")
interface IIrcChannel : ISyncableObject {
  fun initChanModes(): QVariantMap
  fun initUserModes(): QVariantMap
  fun initSetChanModes(chanModes: QVariantMap)
  fun initSetUserModes(usermodes: QVariantMap)

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun addChannelMode(mode: Char, value: String?)

  fun addUserMode(ircuser: IrcUser?, mode: String)

  @Slot
  fun addUserMode(nick: String, mode: String)

  @Slot
  fun joinIrcUser(ircuser: IrcUser)

  @Slot
  fun joinIrcUsers(nicks: QStringList, modes: QStringList)

  fun part(ircuser: IrcUser?)

  @Slot
  fun part(nick: String)

  @Slot
  fun removeChannelMode(mode: Char, value: String?)

  fun removeUserMode(ircuser: IrcUser?, mode: String)

  @Slot
  fun removeUserMode(nick: String, mode: String)

  @Slot
  fun setEncrypted(encrypted: Boolean)

  @Slot
  fun setPassword(password: String)

  @Slot
  fun setTopic(topic: String)

  fun setUserModes(ircuser: IrcUser?, modes: String)

  @Slot
  fun setUserModes(nick: String, modes: String)

  @Slot
  override fun update(properties: QVariantMap)
}
