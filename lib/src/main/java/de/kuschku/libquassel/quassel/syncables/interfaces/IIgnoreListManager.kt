/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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
import de.kuschku.libquassel.protocol.ARG
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type

@Syncable(name = "IgnoreListManager")
interface IIgnoreListManager : ISyncableObject {
  fun initIgnoreList(): QVariantMap
  fun initSetIgnoreList(ignoreList: QVariantMap)

  @Slot
  fun addIgnoreListItem(type: Int, ignoreRule: String, isRegEx: Boolean, strictness: Int,
                        scope: Int, scopeRule: String, isActive: Boolean)

  @Slot
  fun removeIgnoreListItem(ignoreRule: String)

  @Slot
  fun requestAddIgnoreListItem(type: Int, ignoreRule: String, isRegEx: Boolean, strictness: Int,
                               scope: Int, scopeRule: String, isActive: Boolean) {
    REQUEST(
      "requestAddIgnoreListItem", ARG(type, Type.Int), ARG(ignoreRule, Type.QString),
      ARG(isRegEx, Type.Bool),
      ARG(strictness, Type.Int), ARG(scope, Type.Int), ARG(scopeRule, Type.QString),
      ARG(isActive, Type.Bool)
    )
  }

  @Slot
  fun requestRemoveIgnoreListItem(ignoreRule: String) {
    REQUEST("requestRemoveIgnoreListItem", ARG(ignoreRule, Type.QString))
  }

  @Slot
  fun requestToggleIgnoreRule(ignoreRule: String) {
    REQUEST("requestToggleIgnoreRule", ARG(ignoreRule, Type.QString))
  }

  @Slot
  fun toggleIgnoreRule(ignoreRule: String)

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
