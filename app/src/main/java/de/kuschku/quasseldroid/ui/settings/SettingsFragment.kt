package de.kuschku.quasseldroid.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceGroup
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.util.backport.DaggerPreferenceFragmentCompat
import javax.inject.Inject

class SettingsFragment : DaggerPreferenceFragmentCompat(),
                         SharedPreferences.OnSharedPreferenceChangeListener {
  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences, rootKey)
  }

  override fun onStart() {
    super.onStart()
    preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    initSummary(preferenceScreen)
  }

  override fun onStop() {
    preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    super.onStop()
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    updateSummary(findPreference(key))
    if (appearanceSettings.theme != Settings.appearance(context!!).theme) {
      activity?.recreate()
    }
  }

  fun updateSummary(preference: Preference) {
    if (preference is ListPreference) {
      preference.summary = preference.entry
    }
  }

  fun initSummary(preference: Preference) {
    if (preference is PreferenceGroup) {
      (0 until preference.preferenceCount).map(preference::getPreference).forEach(::initSummary)
    } else {
      updateSummary(preference)
    }
  }
}