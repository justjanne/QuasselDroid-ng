package de.kuschku.quasseldroid.ui.clientsettings.client

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class ClientSettingsActivity : SettingsActivity(ClientSettingsFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, ClientSettingsActivity::class.java)
  }
}
