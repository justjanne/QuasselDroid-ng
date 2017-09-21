package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.QVariantMap
import java.nio.ByteBuffer

@Syncable(name = "Transfer")
interface ITransfer : ISyncableObject {
  @Slot
  fun accept(savePath: String)

  @Slot
  fun reject()

  @Slot
  fun requestAccepted(peer: Long)

  @Slot
  fun requestRejected(peer: Long)

  @Slot
  fun setStatus(status: Status)

  @Slot
  fun setError(errorString: String)

  @Slot
  fun dataReceived(peer: Long, data: ByteBuffer)

  @Slot
  fun cleanUp()

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
