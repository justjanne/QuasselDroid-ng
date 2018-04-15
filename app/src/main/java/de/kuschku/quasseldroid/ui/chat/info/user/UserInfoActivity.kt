package de.kuschku.quasseldroid.ui.chat.info.user

import android.content.Context
import android.content.Intent
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class UserInfoActivity : SettingsActivity(UserInfoFragment()) {
  companion object {
    fun launch(
      context: Context,
      openBuffer: Boolean,
      bufferId: BufferId? = null,
      nick: String? = null,
      networkId: NetworkId? = null
    ) = context.startActivity(intent(context, openBuffer, bufferId, nick, networkId))

    fun intent(
      context: Context,
      openBuffer: Boolean,
      bufferId: BufferId? = null,
      nick: String? = null,
      networkId: NetworkId? = null
    ) = Intent(context, UserInfoActivity::class.java).apply {
      putExtra("openBuffer", openBuffer)
      if (bufferId != null) {
        putExtra("bufferId", bufferId)
      }
      if (nick != null) {
        putExtra("nick", nick)
      }
      if (networkId != null) {
        putExtra("networkId", networkId)
      }
    }
  }
}
