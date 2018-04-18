package de.kuschku.quasseldroid.ui.coresettings.highlightlist

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class HighlightListActivity : ServiceBoundSettingsActivity(HighlightListFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, HighlightListActivity::class.java)
  }
}
