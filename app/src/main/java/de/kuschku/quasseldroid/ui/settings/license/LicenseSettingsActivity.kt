package de.kuschku.quasseldroid.ui.settings.license

import android.os.Bundle
import de.kuschku.quasseldroid.ui.settings.SettingsActivity

class LicenseSettingsActivity : SettingsActivity() {
  private lateinit var arguments: Bundle

  override fun onCreate(savedInstanceState: Bundle?) {
    arguments = intent.extras
    super.onCreate(savedInstanceState)
  }

  override fun fragment(): LicenseSettingsFragment {
    val fragment = LicenseSettingsFragment()
    fragment.arguments = arguments
    return fragment
  }
}