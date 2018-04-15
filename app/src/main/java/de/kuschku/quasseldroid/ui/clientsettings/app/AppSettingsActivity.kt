package de.kuschku.quasseldroid.ui.clientsettings.app

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.clientsettings.about.AboutSettingsActivity
import de.kuschku.quasseldroid.ui.clientsettings.crash.CrashSettingsActivity
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class AppSettingsActivity : SettingsActivity(AppSettingsFragment()) {
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.activity_settings, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    R.id.action_about   -> {
      AboutSettingsActivity.launch(this)
      true
    }
    R.id.action_crashes -> {
      CrashSettingsActivity.launch(this)
      true
    }
    else                -> super.onOptionsItemSelected(item)
  }

  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, AppSettingsActivity::class.java)
  }
}
