package de.kuschku.quasseldroid.ui.chat.topic

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class TopicActivity : SettingsActivity(TopicFragment()) {
  companion object {
    fun launch(
      context: Context,
      buffer: Int
    ) = context.startActivity(intent(context, buffer))

    fun intent(
      context: Context,
      buffer: Int
    ) = Intent(context, TopicActivity::class.java).apply {
      putExtra("buffer", buffer)
    }
  }
}
