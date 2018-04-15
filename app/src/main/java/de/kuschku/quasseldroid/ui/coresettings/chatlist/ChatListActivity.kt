package de.kuschku.quasseldroid.ui.coresettings.chatlist

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class ChatListActivity : SettingsActivity(ChatListFragment()) {
  companion object {
    fun launch(
      context: Context,
      chatlist: Int
    ) = context.startActivity(intent(context, chatlist))

    fun intent(
      context: Context,
      chatlist: Int
    ) = Intent(context, ChatListActivity::class.java).apply {
      putExtra("chatlist", chatlist)
    }
  }
}
