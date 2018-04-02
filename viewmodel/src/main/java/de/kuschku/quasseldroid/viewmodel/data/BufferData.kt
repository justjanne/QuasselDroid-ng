package de.kuschku.quasseldroid.viewmodel.data

import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.Network

data class BufferData(
  val info: BufferInfo? = null,
  val network: Network? = null,
  val description: String? = null
)
