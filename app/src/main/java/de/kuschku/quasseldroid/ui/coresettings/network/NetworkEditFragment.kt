package de.kuschku.quasseldroid.ui.coresettings.network

import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment

class NetworkEditFragment : NetworkBaseFragment(), SettingsFragment.Deletable {
  override fun onSave() = network?.let { (it, data) ->
    applyChanges(data)
    it?.requestUpdate(data.toVariantMap())
    true
  } ?: false

  override fun onDelete() {
    network?.let { (it, _) ->
      it?.let {
        viewModel.session.value?.orNull()?.rpcHandler?.removeNetwork(it.networkId())
      }
    }
  }
}
