package de.kuschku.quasseldroid.ui.coresettings.network

import android.content.Context
import android.content.Intent
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class NetworkEditActivity : SettingsActivity(NetworkEditFragment()) {
  companion object {
    fun launch(
      context: Context,
      network: NetworkId
    ) = context.startActivity(intent(context, network))

    fun intent(
      context: Context,
      network: NetworkId
    ) = Intent(context, NetworkEditActivity::class.java).apply {
      putExtra("network", network)
    }
  }
}
