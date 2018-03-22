package de.kuschku.quasseldroid.viewmodel.data

import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork

data class SelectedBufferItem(
  val info: BufferInfo? = null,
  val connectionState: INetwork.ConnectionState = INetwork.ConnectionState.Disconnected,
  val joined: Boolean = false,
  val hiddenState: BufferHiddenState = BufferHiddenState.VISIBLE
)