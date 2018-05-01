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
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type

@Syncable(name = "Identity")
interface IIdentity : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun copyFrom(other: IIdentity) {
    SYNC("copyFrom", ARG(other, QType.Identity))
  }

  @Slot
  fun setAutoAwayEnabled(enabled: Boolean) {
    SYNC("setAutoAwayEnabled", ARG(enabled, Type.Bool))
  }

  @Slot
  fun setAutoAwayReason(reason: String?) {
    SYNC("setAutoAwayReason", ARG(reason, Type.QString))
  }

  @Slot
  fun setAutoAwayReasonEnabled(enabled: Boolean) {
    SYNC("setAutoAwayReasonEnabled", ARG(enabled, Type.Bool))
  }

  @Slot
  fun setAutoAwayTime(time: Int) {
    SYNC("setAutoAwayTime", ARG(time, Type.Int))
  }

  @Slot
  fun setAwayNick(awayNick: String?) {
    SYNC("setAwayNick", ARG(awayNick, Type.QString))
  }

  @Slot
  fun setAwayNickEnabled(enabled: Boolean) {
    SYNC("setAwayNickEnabled", ARG(enabled, Type.Bool))
  }

  @Slot
  fun setAwayReason(awayReason: String?) {
    SYNC("setAwayReason", ARG(awayReason, Type.QString))
  }

  @Slot
  fun setAwayReasonEnabled(enabled: Boolean) {
    SYNC("setAwayReasonEnabled", ARG(enabled, Type.Bool))
  }

  @Slot
  fun setDetachAwayEnabled(enabled: Boolean) {
    SYNC("setDetachAwayEnabled", ARG(enabled, Type.Bool))
  }

  @Slot
  fun setDetachAwayReason(reason: String?) {
    SYNC("setDetachAwayReason", ARG(reason, Type.QString))
  }

  @Slot
  fun setDetachAwayReasonEnabled(enabled: Boolean) {
    SYNC("setDetachAwayReasonEnabled", ARG(enabled, Type.Bool))
  }

  @Slot
  fun setId(id: IdentityId) {
    SYNC("setId", ARG(id, QType.IdentityId))
  }

  @Slot
  fun setIdent(ident: String?) {
    SYNC("setIdent", ARG(ident, Type.QString))
  }

  @Slot
  fun setIdentityName(name: String?) {
    SYNC("setIdentityName", ARG(name, Type.QString))
  }

  @Slot
  fun setKickReason(reason: String?) {
    SYNC("setKickReason", ARG(reason, Type.QString))
  }

  @Slot
  fun setNicks(nicks: QStringList) {
    SYNC("setNicks", ARG(nicks, Type.QStringList))
  }

  @Slot
  fun setPartReason(reason: String?) {
    SYNC("setPartReason", ARG(reason, Type.QString))
  }

  @Slot
  fun setQuitReason(reason: String?) {
    SYNC("setQuitReason", ARG(reason, Type.QString))
  }

  @Slot
  fun setRealName(realName: String?) {
    SYNC("setRealName", ARG(realName, Type.QString))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
