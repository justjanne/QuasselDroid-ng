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

package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import org.threeten.bp.Instant

@Syncable(name = "IrcUser")
interface IIrcUser : ISyncableObject {
  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap, index: Int? = null)
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
  fun setLastAwayMessageTime(lastAwayMessageTime: Instant)

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
