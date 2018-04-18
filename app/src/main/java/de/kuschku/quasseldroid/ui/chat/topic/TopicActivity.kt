package de.kuschku.quasseldroid.ui.chat.topic

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class TopicActivity : ServiceBoundSettingsActivity(TopicFragment()) {
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
