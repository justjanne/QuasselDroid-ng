/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
 * Copyright (c) 2019 The Quassel Project
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
  fun initSetProperties(properties: QVariantMap, i: Int? = null)

  @Slot
  fun addChannelMode(mode: Char, value: String? = null)

  fun addUserMode(ircuser: IrcUser?, mode: String? = null)

  @Slot
  fun addUserMode(nick: String?, mode: String? = null)

  @Slot
  fun joinIrcUser(ircuser: IrcUser)

  @Slot
  fun joinIrcUsers(nicks: QStringList, modes: QStringList)

  fun part(ircuser: IrcUser?)

  @Slot
  fun part(nick: String?)

  @Slot
  fun removeChannelMode(mode: Char, value: String? = null)

  fun removeUserMode(ircuser: IrcUser?, mode: String? = null)

  @Slot
  fun removeUserMode(nick: String?, mode: String? = null)

  @Slot
  fun setEncrypted(encrypted: Boolean)

  @Slot
  fun setPassword(password: String?)

  @Slot
  fun setTopic(topic: String?)

  fun setUserModes(ircuser: IrcUser?, modes: String? = null)

  @Slot
  fun setUserModes(nick: String?, modes: String? = null)

  @Slot
  override fun update(properties: QVariantMap)
}
