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
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.qVariant
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig

@SyncedObject(name = "BufferViewManager")
interface IBufferViewManager : ISyncableObject {
  fun initBufferViewIds(): QVariantList
  fun initSetBufferViewIds(bufferViewIds: QVariantList)

  fun addBufferViewConfig(config: BufferViewConfig)

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addBufferViewConfig(bufferViewConfigId: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "addBufferViewConfig",
      qVariant(bufferViewConfigId, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun newBufferViewConfig(bufferViewConfigId: Int) {
    addBufferViewConfig(bufferViewConfigId)
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestCreateBufferView(properties: QVariantMap) {
    sync(
      target = ProtocolSide.CORE,
      "requestCreateBufferView",
      qVariant(properties, QtType.QVariantMap),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestCreateBufferViews(properties: QVariantList) {
    sync(
      target = ProtocolSide.CORE,
      "requestCreateBufferViews",
      qVariant(properties, QtType.QVariantList),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun deleteBufferViewConfig(bufferViewConfigId: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "deleteBufferViewConfig",
      qVariant(bufferViewConfigId, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestDeleteBufferView(bufferViewConfigId: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestDeleteBufferView",
      qVariant(bufferViewConfigId, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)
}
