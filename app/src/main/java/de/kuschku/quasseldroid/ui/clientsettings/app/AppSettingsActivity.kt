package de.kuschku.quasseldroid.ui.clientsettings.app

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class AppSettingsActivity : SettingsActivity(AppSettingsFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, AppSettingsActivity::class.java)
  }
}
