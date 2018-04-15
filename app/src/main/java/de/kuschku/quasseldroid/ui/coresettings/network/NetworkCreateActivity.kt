package de.kuschku.quasseldroid.ui.coresettings.network

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class NetworkCreateActivity : SettingsActivity(NetworkCreateFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, NetworkEditActivity::class.java)
  }
}
