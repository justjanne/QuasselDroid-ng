/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.quassel.syncables.interfaces

import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.annotations.SyncedCall
import de.justjanne.libquassel.annotations.SyncedObject
import de.kuschku.libquassel.protocol.QStringList
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.qVariant
import de.kuschku.libquassel.quassel.syncables.IrcUser

@SyncedObject(name = "IrcChannel")
interface IIrcChannel : ISyncableObject {
  fun initChanModes(): QVariantMap
  fun initUserModes(): QVariantMap
  fun initSetChanModes(chanModes: QVariantMap)
  fun initSetUserModes(usermodes: QVariantMap)

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap, i: Int? = null)

  fun addUserMode(ircuser: IrcUser?, mode: String? = null)
  fun part(ircuser: IrcUser?)
  fun removeUserMode(ircuser: IrcUser?, mode: String? = null)
  fun setUserModes(ircuser: IrcUser?, modes: String? = null)
  fun joinIrcUser(ircuser: IrcUser)

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addChannelMode(mode: Char, value: String? = null) {
    sync(
      target = ProtocolSide.CLIENT,
      "addChannelMode",
      qVariant(mode, QtType.QChar),
      qVariant(value, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addUserMode(nick: String, mode: String? = null) {
    sync(
      target = ProtocolSide.CLIENT,
      "addUserMode",
      qVariant(nick, QtType.QString),
      qVariant(mode, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun joinIrcUsers(nicks: QStringList, modes: QStringList) {
    sync(
      target = ProtocolSide.CLIENT,
      "joinIrcUsers",
      qVariant(nicks, QtType.QStringList),
      qVariant(modes, QtType.QStringList),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun part(nick: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "part",
      qVariant(nick, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun removeChannelMode(mode: Char, value: String? = null) {
    sync(
      target = ProtocolSide.CLIENT,
      "removeChannelMode",
      qVariant(mode, QtType.QChar),
      qVariant(value, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun removeUserMode(nick: String, mode: String? = null) {
    sync(
      target = ProtocolSide.CLIENT,
      "removeUserMode",
      qVariant(nick, QtType.QString),
      qVariant(mode, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setEncrypted(encrypted: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setEncrypted",
      qVariant(encrypted, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setPassword(password: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setPassword",
      qVariant(password, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setTopic(topic: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setTopic",
      qVariant(topic, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUserModes(nick: String, modes: String? = null) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUserModes",
      qVariant(nick, QtType.QString),
      qVariant(modes, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)
}
