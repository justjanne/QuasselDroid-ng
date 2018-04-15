package de.kuschku.quasseldroid.ui.coresettings.identity

import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.util.helpers.value


class IdentityCreateFragment : IdentityBaseFragment() {
  override fun onSave() = viewModel.session.value?.orNull()?.let { session ->
    Identity(session.proxy).let { data ->
      applyChanges(data)
      session.rpcHandler?.createIdentity(data, mapOf())
      true
    }
  } ?: false
}
