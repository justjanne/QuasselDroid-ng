package de.kuschku.quasseldroid.ui.clientsettings.license

import android.content.Context
import android.content.Intent
import android.support.annotation.StringRes
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class LicenseSettingsActivity : SettingsActivity(LicenseSettingsFragment()) {
  companion object {
    fun launch(
      context: Context,
      license_name: String,
      @StringRes license_text: Int
    ) = context.startActivity(intent(context, license_name, license_text))

    fun intent(
      context: Context,
      license_name: String,
      @StringRes license_text: Int
    ) = Intent(context, LicenseSettingsActivity::class.java).apply {
      putExtra("license_name", license_name)
      putExtra("license_text", license_text)
    }
  }
}
