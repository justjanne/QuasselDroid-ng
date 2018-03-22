package de.kuschku.quasseldroid_ng.viewmodel.data

import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork

data class BufferData(
  val info: BufferInfo? = null,
  val network: INetwork.NetworkInfo? = null,
  val description: String? = null
)