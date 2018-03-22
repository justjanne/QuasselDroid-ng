package de.kuschku.quasseldroid_ng.viewmodel.data

import de.kuschku.libquassel.protocol.Buffer_Activities
import de.kuschku.libquassel.protocol.Message_Types
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork

data class BufferProps(
  val info: BufferInfo,
  val network: INetwork.NetworkInfo,
  val bufferStatus: BufferStatus,
  val description: CharSequence,
  val activity: Message_Types,
  val highlights: Int = 0,
  val bufferActivity: Buffer_Activities = BufferInfo.Activity.of(
    BufferInfo.Activity.NoActivity
  ),
  val hiddenState: BufferHiddenState
)