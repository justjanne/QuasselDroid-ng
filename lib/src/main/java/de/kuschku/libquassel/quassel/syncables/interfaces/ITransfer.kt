/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
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
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import java.nio.ByteBuffer

@Syncable(name = "Transfer")
interface ITransfer : ISyncableObject {
  @Slot
  fun accept(savePath: String) {
    SYNC("accept", ARG(savePath, Type.QString))
  }

  @Slot
  fun reject() {
    SYNC("reject")
  }

  @Slot
  fun requestAccepted(peer: Long) {
    TODO()
  }

  @Slot
  fun requestRejected(peer: Long) {
    TODO()
  }

  @Slot
  fun setStatus(status: Status) {
    TODO()
  }

  @Slot
  fun setError(errorString: String) {
    SYNC("setError", ARG(errorString, Type.QString))
  }

  @Slot
  fun dataReceived(peer: Long, data: ByteBuffer) {
    TODO()
  }

  @Slot
  fun cleanUp() {
    SYNC("cleanUp")
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }

  enum class Status {
    New,
    Pending,
    Connecting,
    Transferring,
    Paused,
    Completed,
    Failed,
    Rejected
  }

  enum class Direction {
    Send,
    Receive
  }
}
