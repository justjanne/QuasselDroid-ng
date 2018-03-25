package de.kuschku.quasseldroid.ui.settings.app

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.settings.SettingsActivity
import de.kuschku.quasseldroid.ui.settings.about.AboutSettingsActivity
import de.kuschku.quasseldroid.ui.settings.crash.CrashSettingsActivity

class AppSettingsActivity : SettingsActivity(AppSettingsFragment()) {
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.activity_settings, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    R.id.action_about   -> {
      startActivity(Intent(applicationContext, AboutSettingsActivity::class.java))
      true
    }
    R.id.action_crashes -> {
      startActivity(Intent(applicationContext, CrashSettingsActivity::class.java))
      true
    }
    else                -> super.onOptionsItemSelected(item)
  }
}