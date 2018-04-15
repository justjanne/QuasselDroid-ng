package de.kuschku.quasseldroid.ui.clientsettings.crash

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class CrashSettingsActivity : SettingsActivity(CrashSettingsFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, CrashSettingsActivity::class.java)
  }
}
