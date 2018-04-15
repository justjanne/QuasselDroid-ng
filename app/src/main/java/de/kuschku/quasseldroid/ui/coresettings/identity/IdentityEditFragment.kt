package de.kuschku.quasseldroid.ui.coresettings.identity

import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment

class IdentityEditFragment : IdentityBaseFragment(), SettingsFragment.Deletable {
  override fun onSave() = identity?.let { (it, data) ->
    applyChanges(data)
    it?.requestUpdate(data.toVariantMap())
    true
  } ?: false

  override fun onDelete() {
    identity?.let { (it, _) ->
      it?.let {
        viewModel.session.value?.orNull()?.rpcHandler?.removeIdentity(it.id())
      }
    }
  }
}
