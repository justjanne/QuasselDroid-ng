package de.kuschku.quasseldroid.ui.coresettings.networkserver

import android.content.Context
import android.content.Intent
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class NetworkServerActivity : ServiceBoundSettingsActivity(NetworkServerFragment()) {
  companion object {
    fun launch(
      context: Context,
      server: INetwork.Server? = null
    ) = context.startActivity(intent(context, server))

    fun intent(
      context: Context,
      server: INetwork.Server? = null
    ) = Intent(context, NetworkServerActivity::class.java).apply {
      if (server != null) {
        putExtra("server", server)
      }
    }
  }
}
