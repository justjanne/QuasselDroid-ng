package de.kuschku.quasseldroid.ui.coresettings.networkconfig

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class NetworkConfigActivity : ServiceBoundSettingsActivity(NetworkConfigFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, NetworkConfigActivity::class.java)
  }
}
