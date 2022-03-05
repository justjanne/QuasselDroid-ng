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

@SyncedObject(name = "IgnoreListManager")
interface IIgnoreListManager : ISyncableObject {
  fun initIgnoreList(): QVariantMap
  fun initSetIgnoreList(ignoreList: QVariantMap)

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addIgnoreListItem(
    type: Int,
    ignoreRule: String?,
    isRegEx: Boolean,
    strictness: Int,
    scope: Int,
    scopeRule: String?,
    isActive: Boolean
  ) {
    sync(
      target = ProtocolSide.CLIENT,
      "addIgnoreListItem",
      qVariant(type, QtType.Int),
      qVariant(ignoreRule, QtType.QString),
      qVariant(isRegEx, QtType.Bool),
      qVariant(strictness, QtType.Int),
      qVariant(scope, QtType.Int),
      qVariant(scopeRule, QtType.QString),
      qVariant(isActive, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun removeIgnoreListItem(ignoreRule: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "removeIgnoreListItem",
      qVariant(ignoreRule, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestAddIgnoreListItem(
    type: Int,
    ignoreRule: String?,
    isRegEx: Boolean,
    strictness: Int,
    scope: Int,
    scopeRule: String?,
    isActive: Boolean
  ) {
    sync(
      target = ProtocolSide.CORE,
      "requestAddIgnoreListItem",
      qVariant(type, QtType.Int),
      qVariant(ignoreRule, QtType.QString),
      qVariant(isRegEx, QtType.Bool),
      qVariant(strictness, QtType.Int),
      qVariant(scope, QtType.Int),
      qVariant(scopeRule, QtType.QString),
      qVariant(isActive, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestRemoveIgnoreListItem(ignoreRule: String?) {
    sync(
      target = ProtocolSide.CORE,
      "requestRemoveIgnoreListItem",
      qVariant(ignoreRule, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestToggleIgnoreRule(ignoreRule: String?) {
    sync(
      target = ProtocolSide.CORE,
      "requestToggleIgnoreRule",
      qVariant(ignoreRule, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun toggleIgnoreRule(ignoreRule: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "requestToggleIgnoreRule",
      qVariant(ignoreRule, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)
}
