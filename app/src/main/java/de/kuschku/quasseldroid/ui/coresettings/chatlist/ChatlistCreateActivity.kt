package de.kuschku.quasseldroid.ui.coresettings.chatlist

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class ChatlistCreateActivity : SettingsActivity(ChatListCreateFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, ChatlistCreateActivity::class.java)
  }
}
