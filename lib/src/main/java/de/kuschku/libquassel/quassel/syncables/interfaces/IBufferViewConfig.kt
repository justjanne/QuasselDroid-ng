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
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.QuasselType
import de.kuschku.libquassel.protocol.qVariant

@SyncedObject(name = "BufferViewConfig")
interface IBufferViewConfig : ISyncableObject {
  fun initBufferList(): QVariantList
  fun initRemovedBuffers(): QVariantList
  fun initTemporarilyRemovedBuffers(): QVariantList
  fun initSetBufferList(buffers: QVariantList)
  fun initSetRemovedBuffers(buffers: QVariantList)
  fun initSetTemporarilyRemovedBuffers(buffers: QVariantList)

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addBuffer(buffer: BufferId, pos: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "addBuffer",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(pos, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestAddBuffer(buffer: BufferId, pos: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestAddBuffer",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(pos, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun moveBuffer(buffer: BufferId, pos: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "moveBuffer",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(pos, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestMoveBuffer(buffer: BufferId, pos: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestMoveBuffer",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(pos, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun removeBuffer(buffer: BufferId) {
    sync(
      target = ProtocolSide.CLIENT,
      "removeBuffer",
      qVariant(buffer, QuasselType.BufferId),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestRemoveBuffer(buffer: BufferId) {
    sync(
      target = ProtocolSide.CORE,
      "requestRemoveBuffer",
      qVariant(buffer, QuasselType.BufferId),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun removeBufferPermanently(buffer: BufferId) {
    sync(
      target = ProtocolSide.CLIENT,
      "removeBufferPermanently",
      qVariant(buffer, QuasselType.BufferId),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestRemoveBufferPermanently(buffer: BufferId) {
    sync(
      target = ProtocolSide.CORE,
      "requestRemoveBufferPermanently",
      qVariant(buffer, QuasselType.BufferId),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setBufferViewName(value: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "setBufferViewName",
      qVariant(value, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetBufferViewName(value: String) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetBufferViewName",
      qVariant(value, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAddNewBuffersAutomatically(value: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAddNewBuffersAutomatically",
      qVariant(value, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAllowedBufferTypes(value: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAllowedBufferTypes",
      qVariant(value, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setDisableDecoration(value: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setDisableDecoration",
      qVariant(value, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setHideInactiveBuffers(value: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setHideInactiveBuffers",
      qVariant(value, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setHideInactiveNetworks(value: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setHideInactiveNetworks",
      qVariant(value, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setMinimumActivity(value: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setMinimumActivity",
      qVariant(value, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setNetworkId(value: NetworkId) {
    sync(
      target = ProtocolSide.CLIENT,
      "setNetworkId",
      qVariant(value, QuasselType.NetworkId),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setShowSearch(value: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setShowSearch",
      qVariant(value, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setSortAlphabetically(value: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setSortAlphabetically",
      qVariant(value, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)
}
