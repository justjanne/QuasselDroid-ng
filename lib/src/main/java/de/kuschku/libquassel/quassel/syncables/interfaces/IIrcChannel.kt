package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.*
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
  fun addChannelMode(mode: Char, value: String) {
    SYNC("addChannelMode", ARG(mode, Type.QChar), ARG(value, Type.QString))
  }

  fun addUserMode(ircuser: IrcUser?, mode: String) {
  }

  @Slot
  fun addUserMode(nick: String, mode: String) {
    SYNC("addUserMode", ARG(nick, Type.QString), ARG(mode, Type.QString))
  }

  @Slot
  fun joinIrcUser(ircuser: IrcUser) {
    SYNC("joinIrcUser", ARG(ircuser.toVariantMap(), QType.IrcUser))
  }

  @Slot
  fun joinIrcUsers(nicks: QStringList, modes: QStringList) {
    SYNC("joinIrcUsers", ARG(nicks, Type.QStringList), ARG(modes, Type.QStringList))
  }

  fun part(ircuser: IrcUser?) {
  }

  @Slot
  fun part(nick: String) {
    SYNC("part", ARG(nick, Type.QString))
  }

  @Slot
  fun removeChannelMode(mode: Char, value: String) {
    SYNC("removeChannelMode", ARG(mode, Type.QChar), ARG(value, Type.QString))
  }

  fun removeUserMode(ircuser: IrcUser?, mode: String) {
  }

  @Slot
  fun removeUserMode(nick: String, mode: String) {
    SYNC("removeUserMode", ARG(nick, Type.QString), ARG(mode, Type.QString))
  }

  @Slot
  fun setEncrypted(encrypted: Boolean) {
    SYNC("setEncrypted", ARG(encrypted, Type.Bool))
  }

  @Slot
  fun setPassword(password: String) {
    SYNC("setPassword", ARG(password, Type.QString))
  }

  @Slot
  fun setTopic(topic: String) {
    SYNC("setTopic", ARG(topic, Type.QString))
  }

  fun setUserModes(ircuser: IrcUser?, modes: String) {
    SYNC("setUserModes", ARG(ircuser, QType.IrcUser), ARG(modes, Type.QString))
  }

  @Slot
  fun setUserModes(nick: String, modes: String) {
    SYNC("setUserModes", ARG(nick, Type.QString), ARG(modes, Type.QString))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
