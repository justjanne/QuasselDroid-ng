package de.kuschku.quasseldroid.ui.coresettings.chatlist

import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.util.helpers.value

class ChatListCreateFragment : ChatListBaseFragment() {
  override fun onSave() = viewModel.session.value?.orNull()?.let { session ->
    BufferViewConfig(-1, session.proxy).let { data ->
      applyChanges(data, null)
      viewModel.bufferViewManager.value?.orNull()?.requestCreateBufferView(data.toVariantMap())
      true
    }
  } ?: false
}
