package de.kuschku.quasseldroid.ui.coresettings.identity

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class IdentityCreateActivity : ServiceBoundSettingsActivity(IdentityCreateFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, IdentityCreateActivity::class.java)
  }
}
