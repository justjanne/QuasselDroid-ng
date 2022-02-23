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
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.protocol.QuasselType
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.qVariant

@SyncedObject(name = "BacklogManager")
interface IBacklogManager : ISyncableObject {
  @SyncedCall(target = ProtocolSide.CORE)
  fun requestBacklog(
    bufferId: BufferId,
    first: MsgId = MsgId(-1),
    last: MsgId = MsgId(-1),
    limit: Int = -1,
    additional: Int = 0
  ) {
    sync(
      target = ProtocolSide.CORE,
      "requestBacklog",
      qVariant(bufferId, QuasselType.BufferId),
      qVariant(first, QuasselType.MsgId),
      qVariant(last, QuasselType.MsgId),
      qVariant(limit, QtType.Int),
      qVariant(additional, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestBacklogFiltered(
    bufferId: BufferId,
    first: MsgId = MsgId(-1),
    last: MsgId = MsgId(-1),
    limit: Int = -1,
    additional: Int = 0,
    type: Int = -1,
    flags: Int = -1
  ) {
    sync(
      target = ProtocolSide.CORE,
      "requestBacklogFiltered",
      qVariant(bufferId, QuasselType.BufferId),
      qVariant(first, QuasselType.MsgId),
      qVariant(last, QuasselType.MsgId),
      qVariant(limit, QtType.Int),
      qVariant(additional, QtType.Int),
      qVariant(type, QtType.Int),
      qVariant(flags, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestBacklogAll(
    first: MsgId = MsgId(-1),
    last: MsgId = MsgId(-1),
    limit: Int = -1,
    additional: Int = 0
  ) {
    sync(
      target = ProtocolSide.CORE,
      "requestBacklogAll",
      qVariant(first, QuasselType.MsgId),
      qVariant(last, QuasselType.MsgId),
      qVariant(limit, QtType.Int),
      qVariant(additional, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestBacklogAllFiltered(
    first: MsgId = MsgId(-1),
    last: MsgId = MsgId(-1),
    limit: Int = -1,
    additional: Int = 0,
    type: Int = -1,
    flags: Int = -1
  ) {
    sync(
      target = ProtocolSide.CORE,
      "requestBacklogAll",
      qVariant(first, QuasselType.MsgId),
      qVariant(last, QuasselType.MsgId),
      qVariant(limit, QtType.Int),
      qVariant(additional, QtType.Int),
      qVariant(type, QtType.Int),
      qVariant(flags, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun receiveBacklog(
    bufferId: BufferId,
    first: MsgId = MsgId(-1),
    last: MsgId = MsgId(-1),
    limit: Int = -1,
    additional: Int = 0,
    messages: QVariantList
  ) {
    sync(
      target = ProtocolSide.CLIENT,
      "receiveBacklog",
      qVariant(bufferId, QuasselType.BufferId),
      qVariant(first, QuasselType.MsgId),
      qVariant(last, QuasselType.MsgId),
      qVariant(limit, QtType.Int),
      qVariant(additional, QtType.Int),
      qVariant(messages, QtType.QVariantList),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun receiveBacklogFiltered(
    bufferId: BufferId,
    first: MsgId = MsgId(-1),
    last: MsgId = MsgId(-1),
    limit: Int = -1,
    additional: Int = 0,
    type: Int = -1,
    flags: Int = -1,
    messages: QVariantList
  ) {
    sync(
      target = ProtocolSide.CLIENT,
      "receiveBacklogFiltered",
      qVariant(bufferId, QuasselType.BufferId),
      qVariant(first, QuasselType.MsgId),
      qVariant(last, QuasselType.MsgId),
      qVariant(limit, QtType.Int),
      qVariant(additional, QtType.Int),
      qVariant(type, QtType.Int),
      qVariant(flags, QtType.Int),
      qVariant(messages, QtType.QVariantList),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun receiveBacklogAll(
    first: MsgId = MsgId(-1),
    last: MsgId = MsgId(-1),
    limit: Int = -1,
    additional: Int = 0,
    messages: QVariantList
  ) {
    sync(
      target = ProtocolSide.CLIENT,
      "receiveBacklogAll",
      qVariant(first, QuasselType.MsgId),
      qVariant(last, QuasselType.MsgId),
      qVariant(limit, QtType.Int),
      qVariant(additional, QtType.Int),
      qVariant(messages, QtType.QVariantList),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun receiveBacklogAllFiltered(
    first: MsgId = MsgId(-1),
    last: MsgId = MsgId(-1),
    limit: Int = -1,
    additional: Int = 0,
    type: Int = -1,
    flags: Int = -1,
    messages: QVariantList
  ) {
    sync(
      target = ProtocolSide.CLIENT,
      "receiveBacklogAllFiltered",
      qVariant(first, QuasselType.MsgId),
      qVariant(last, QuasselType.MsgId),
      qVariant(limit, QtType.Int),
      qVariant(additional, QtType.Int),
      qVariant(type, QtType.Int),
      qVariant(flags, QtType.Int),
      qVariant(messages, QtType.QVariantList),
    )
  }
}
