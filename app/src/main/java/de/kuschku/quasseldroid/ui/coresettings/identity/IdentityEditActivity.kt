package de.kuschku.quasseldroid.ui.coresettings.identity

import android.content.Context
import android.content.Intent
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class IdentityEditActivity : ServiceBoundSettingsActivity(IdentityEditFragment()) {
  companion object {
    fun launch(
      context: Context,
      identity: IdentityId
    ) = context.startActivity(intent(context, identity))

    fun intent(
      context: Context,
      identity: IdentityId
    ) = Intent(context, IdentityEditActivity::class.java).apply {
      putExtra("identity", identity)
    }
  }
}
