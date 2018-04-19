package de.kuschku.quasseldroid.ui.clientsettings.whitelist

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class WhitelistActivity : SettingsActivity(WhitelistFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, WhitelistActivity::class.java)
  }
}
