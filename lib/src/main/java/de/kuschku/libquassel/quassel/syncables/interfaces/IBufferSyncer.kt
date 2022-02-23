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
import de.kuschku.libquassel.protocol.*

@SyncedObject(name = "BufferSyncer")
interface IBufferSyncer : ISyncableObject {
  fun initActivities(): QVariantList
  fun initHighlightCounts(): QVariantList
  fun initLastSeenMsg(): QVariantList
  fun initMarkerLines(): QVariantList
  fun initSetActivities(data: QVariantList)
  fun initSetHighlightCounts(data: QVariantList)
  fun initSetLastSeenMsg(data: QVariantList)
  fun initSetMarkerLines(data: QVariantList)

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun markBufferAsRead(buffer: BufferId) {
    sync(
      target = ProtocolSide.CLIENT,
      "markBufferAsRead",
      qVariant(buffer, QuasselType.BufferId),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestMarkBufferAsRead(buffer: BufferId) {
    sync(
      target = ProtocolSide.CORE,
      "requestMarkBufferAsRead",
      qVariant(buffer, QuasselType.BufferId),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun mergeBuffersPermanently(buffer: BufferId, buffer2: BufferId) {
    sync(
      target = ProtocolSide.CLIENT,
      "mergeBuffersPermanently",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(buffer2, QuasselType.BufferId),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestMergeBuffersPermanently(buffer: BufferId, buffer2: BufferId) {
    sync(
      target = ProtocolSide.CORE,
      "requestMergeBuffersPermanently",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(buffer2, QuasselType.BufferId),
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
  fun renameBuffer(buffer: BufferId, newName: String) {
    sync(
      target = ProtocolSide.CLIENT,
      "renameBuffer",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(newName, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestRenameBuffer(buffer: BufferId, newName: String) {
    sync(
      target = ProtocolSide.CORE,
      "requestRenameBuffer",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(newName, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setMarkerLine(buffer: BufferId, msgId: MsgId) {
    sync(
      target = ProtocolSide.CLIENT,
      "setMarkerLine",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(msgId, QuasselType.MsgId),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetLastSeenMsg(buffer: BufferId, msgId: MsgId) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetLastSeenMsg",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(msgId, QuasselType.MsgId),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setLastSeenMsg(buffer: BufferId, msgId: MsgId) {
    sync(
      target = ProtocolSide.CLIENT,
      "setLastSeenMsg",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(msgId, QuasselType.MsgId),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetMarkerLine(buffer: BufferId, msgId: MsgId) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetMarkerLine",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(msgId, QuasselType.MsgId),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setBufferActivity(buffer: BufferId, types: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setBufferActivity",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(types, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setHighlightCount(buffer: BufferId, count: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setHighlightCount",
      qVariant(buffer, QuasselType.BufferId),
      qVariant(count, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun requestPurgeBufferIds() {
    sync(
      target = ProtocolSide.CLIENT,
      "requestPurgeBufferIds"
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)
}
