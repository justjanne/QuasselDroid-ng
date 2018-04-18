package de.kuschku.quasseldroid.ui.coresettings

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class CoreSettingsActivity : ServiceBoundSettingsActivity(CoreSettingsFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, CoreSettingsActivity::class.java)
  }
}
