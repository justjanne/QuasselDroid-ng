package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.*
import de.kuschku.quasseldroid_ng.quassel.syncables.IrcUser

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
    SYNC(SLOT, ARG(mode, Type.QChar), ARG(value, Type.QString))
  }

  fun addUserMode(ircuser: IrcUser?, mode: String) {
  }

  @Slot
  fun addUserMode(nick: String, mode: String) {
    SYNC(SLOT, ARG(nick, Type.QString), ARG(mode, Type.QString))
  }

  @Slot
  fun joinIrcUser(ircuser: IrcUser) {
    SYNC(SLOT, ARG(ircuser.toVariantMap(), QType.IrcUser))
  }

  @Slot
  fun joinIrcUsers(nicks: QStringList, modes: QStringList) {
    SYNC(SLOT, ARG(nicks, Type.QStringList), ARG(modes, Type.QStringList))
  }

  fun part(ircuser: IrcUser?) {
  }

  @Slot
  fun part(nick: String) {
    SYNC(SLOT, ARG(nick, Type.QString))
  }

  @Slot
  fun removeChannelMode(mode: Char, value: String) {
    SYNC(SLOT, ARG(mode, Type.QChar), ARG(value, Type.QString))
  }

  fun removeUserMode(ircuser: IrcUser?, mode: String) {
  }

  @Slot
  fun removeUserMode(nick: String, mode: String) {
    SYNC(SLOT, ARG(nick, Type.QString), ARG(mode, Type.QString))
  }

  @Slot
  fun setEncrypted(encrypted: Boolean) {
    SYNC(SLOT, ARG(encrypted, Type.Bool))
  }

  @Slot
  fun setPassword(password: String) {
    SYNC(SLOT, ARG(password, Type.QString))
  }

  @Slot
  fun setTopic(topic: String) {
    SYNC(SLOT, ARG(topic, Type.QString))
  }

  fun setUserModes(ircuser: IrcUser?, modes: String) {
    SYNC(SLOT, ARG(ircuser, QType.IrcUser), ARG(modes, Type.QString))
  }

  @Slot
  fun setUserModes(nick: String, modes: String) {
    SYNC(SLOT, ARG(nick, Type.QString), ARG(modes, Type.QString))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
