package de.kuschku.quasseldroid.ui.coresettings.ignorelist

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class IgnoreListActivity : ServiceBoundSettingsActivity(IgnoreListFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, IgnoreListActivity::class.java)
  }
}
