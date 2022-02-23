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
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.QStringList
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.QuasselType
import de.kuschku.libquassel.protocol.qVariant

@SyncedObject(name = "Identity")
interface IIdentity : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoAwayEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoAwayEnabled",
      qVariant(enabled, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoAwayReason(reason: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoAwayReason",
      qVariant(reason, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoAwayReasonEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoAwayReasonEnabled",
      qVariant(enabled, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoAwayTime(time: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoAwayTime",
      qVariant(time, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAwayNick(awayNick: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAwayNick",
      qVariant(awayNick, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAwayNickEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAwayNickEnabled",
      qVariant(enabled, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAwayReason(awayReason: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAwayReason",
      qVariant(awayReason, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAwayReasonEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAwayReasonEnabled",
      qVariant(enabled, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setDetachAwayEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setDetachAwayEnabled",
      qVariant(enabled, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setDetachAwayReason(reason: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "setDetachAwayReason",
      qVariant(reason, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setDetachAwayReasonEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setDetachAwayReasonEnabled",
      qVariant(enabled, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setId(id: IdentityId) {
    sync(
      target = ProtocolSide.CLIENT,
      "setId",
      qVariant(id, QuasselType.IdentityId),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setIdent(ident: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "setIdent",
      qVariant(ident, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setIdentityName(name: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "setIdentityName",
      qVariant(name, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setKickReason(reason: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "setKickReason",
      qVariant(reason, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setNicks(nicks: QStringList) {
    sync(
      target = ProtocolSide.CLIENT,
      "setNicks",
      qVariant(nicks, QtType.QStringList),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setPartReason(reason: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "setPartReason",
      qVariant(reason, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setQuitReason(reason: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "setQuitReason",
      qVariant(reason, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setRealName(realName: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "setRealName",
      qVariant(realName, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)
}
