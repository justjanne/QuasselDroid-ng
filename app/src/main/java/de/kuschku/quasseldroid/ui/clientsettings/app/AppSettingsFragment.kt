package de.kuschku.quasseldroid.ui.clientsettings.app

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceGroup
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ui.clientsettings.about.AboutSettingsActivity
import de.kuschku.quasseldroid.ui.clientsettings.crash.CrashSettingsActivity
import de.kuschku.quasseldroid.util.backport.DaggerPreferenceFragmentCompat
import javax.inject.Inject

class AppSettingsFragment : DaggerPreferenceFragmentCompat(),
                            SharedPreferences.OnSharedPreferenceChangeListener {
  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

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
      (0 until preference.preferenceCount).asSequence().map(preference::getPreference).forEach(::initSummary)
    } else {
      updateSummary(preference)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    inflater?.inflate(R.menu.activity_settings, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    R.id.action_about   -> {
      AboutSettingsActivity.launch(requireContext())
      true
    }
    R.id.action_crashes -> {
      CrashSettingsActivity.launch(requireContext())
      true
    }
    else                -> super.onOptionsItemSelected(item)
  }
}
