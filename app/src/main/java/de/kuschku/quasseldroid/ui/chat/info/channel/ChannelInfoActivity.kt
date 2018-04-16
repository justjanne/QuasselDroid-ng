package de.kuschku.quasseldroid.ui.chat.info.channel

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class ChannelInfoActivity : SettingsActivity(ChannelInfoFragment()) {
  companion object {
    fun launch(
      context: Context,
      openBuffer: Boolean,
      bufferId: Int
    ) = context.startActivity(intent(context, openBuffer, bufferId))

    fun intent(
      context: Context,
      openBuffer: Boolean,
      bufferId: Int
    ) = Intent(context, ChannelInfoActivity::class.java).apply {
      putExtra("bufferId", bufferId)
      putExtra("openBuffer", openBuffer)
    }
  }
}
