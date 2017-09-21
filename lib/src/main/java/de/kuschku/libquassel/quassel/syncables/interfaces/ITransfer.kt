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
