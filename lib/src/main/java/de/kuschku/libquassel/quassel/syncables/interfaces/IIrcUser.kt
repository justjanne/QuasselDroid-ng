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
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.qVariant
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import org.threeten.bp.temporal.Temporal

@SyncedObject("IrcUser")
interface IIrcUser : ISyncableObject {
  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap, index: Int? = null)

  fun joinChannel(channel: IrcChannel, skip_channel_join: Boolean = false)
  fun partChannel(channel: IrcChannel)


  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addUserModes(modes: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "addUserModes",
      qVariant(modes, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun joinChannel(channelname: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "joinChannel",
      qVariant(channelname, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun partChannel(channelname: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "partChannel",
      qVariant(channelname, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun quit() {
    sync(
      target = ProtocolSide.CLIENT,
      "quit",
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun removeUserModes(modes: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "removeUserModes",
      qVariant(modes, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAccount(account: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAccount",
      qVariant(account, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAway(away: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAway",
      qVariant(away, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAwayMessage(awayMessage: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAwayMessage",
      qVariant(awayMessage, QtType.QString),
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
  fun setHost(host: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setHost",
      qVariant(host, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setIdleTime(idleTime: Temporal) {
    sync(
      target = ProtocolSide.CLIENT,
      "setIdleTime",
      qVariant(idleTime, QtType.QDateTime),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setIrcOperator(ircOperator: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setIrcOperator",
      qVariant(ircOperator, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setLastAwayMessage(lastAwayMessage: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setLastAwayMessage",
      qVariant(lastAwayMessage, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setLastAwayMessageTime(lastAwayMessageTime: Temporal) {
    sync(
      target = ProtocolSide.CLIENT,
      "setLastAwayMessageTime",
      qVariant(lastAwayMessageTime, QtType.QDateTime),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setLoginTime(loginTime: Temporal) {
    sync(
      target = ProtocolSide.CLIENT,
      "setLoginTime",
      qVariant(loginTime, QtType.QDateTime),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setNick(nick: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setNick",
      qVariant(nick, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setRealName(realName: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setRealName",
      qVariant(realName, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setServer(server: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setServer",
      qVariant(server, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setSuserHost(suserHost: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setSuserHost",
      qVariant(suserHost, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUser(user: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUser",
      qVariant(user, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUserModes(modes: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUserModes",
      qVariant(modes, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setWhoisServiceReply(whoisServiceReply: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setWhoisServiceReply",
      qVariant(whoisServiceReply, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun updateHostmask(mask: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "updateHostmask",
      qVariant(mask, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)
}
