/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
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
import de.kuschku.libquassel.protocol.ARG
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig

@Syncable(name = "BufferViewManager")
interface IBufferViewManager : ISyncableObject {
  fun initBufferViewIds(): QVariantList
  fun initSetBufferViewIds(bufferViewIds: QVariantList)

  fun addBufferViewConfig(config: BufferViewConfig)

  @Slot
  fun addBufferViewConfig(bufferViewConfigId: Int)

  @Slot
  fun deleteBufferViewConfig(bufferViewConfigId: Int)

  @Slot
  fun newBufferViewConfig(bufferViewConfigId: Int) {
    addBufferViewConfig(bufferViewConfigId)
  }

  @Slot
  fun requestCreateBufferView(properties: QVariantMap) {
    REQUEST("requestCreateBufferView", ARG(properties, Type.QVariantMap))
  }

  @Slot
  fun requestCreateBufferViews(properties: QVariantList) {
    REQUEST("requestCreateBufferViews", ARG(properties, Type.QVariantList))
  }

  @Slot
  fun requestDeleteBufferView(bufferViewId: Int) {
    REQUEST("requestDeleteBufferView", ARG(bufferViewId, Type.Int))
  }

  @Slot
  fun requestDeleteBufferViews(bufferViews: QVariantList) {
    REQUEST("requestDeleteBufferViews", ARG(bufferViews, Type.QVariantList))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
