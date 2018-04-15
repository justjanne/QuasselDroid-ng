package de.kuschku.quasseldroid.ui.coresettings.identity

import android.content.Context
import android.content.Intent
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class IdentityCreateActivity : SettingsActivity(IdentityCreateFragment()) {
  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, IdentityCreateActivity::class.java)
  }
}
