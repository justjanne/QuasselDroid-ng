package de.kuschku.quasseldroid.ui.coresettings.network

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class NetworkCreateActivity : ServiceBoundSettingsActivity(NetworkCreateFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, NetworkCreateActivity::class.java)
  }
}
