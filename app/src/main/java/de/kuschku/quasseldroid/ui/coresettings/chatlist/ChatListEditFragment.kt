package de.kuschku.quasseldroid.ui.coresettings.chatlist

import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment

class ChatListEditFragment : ChatListBaseFragment(), SettingsFragment.Deletable {
  override fun onSave() = chatlist?.let { (it, data) ->
    applyChanges(data, it)
    it?.requestUpdate(data.toVariantMap())
    true
  } ?: false

  override fun onDelete() {
    chatlist?.let { (it, _) ->
      it?.let {
        viewModel.bufferViewManager.value?.orNull()?.requestDeleteBufferView(it.bufferViewId())
      }
    }
  }
}
