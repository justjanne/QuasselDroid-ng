package de.kuschku.quasseldroid.ui.coresettings.network

import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.util.helpers.value

class NetworkCreateFragment : NetworkBaseFragment() {
  override fun onSave() = viewModel.session.value?.orNull()?.let { session ->
    Network(-1, session.proxy).let { data ->
      applyChanges(data)
      session.rpcHandler?.createNetwork(data.networkInfo(), emptyList())
      true
    }
  } ?: false
}
