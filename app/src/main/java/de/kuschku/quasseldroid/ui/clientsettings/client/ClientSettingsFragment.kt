/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.clientsettings.client

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ui.clientsettings.about.AboutActivity
import de.kuschku.quasseldroid.ui.clientsettings.crash.CrashActivity
import de.kuschku.quasseldroid.ui.clientsettings.whitelist.WhitelistActivity
import de.kuschku.quasseldroid.util.ui.settings.DaggerPreferenceFragmentCompat
import javax.inject.Inject

class ClientSettingsFragment : DaggerPreferenceFragmentCompat(),
                               SharedPreferences.OnSharedPreferenceChangeListener {

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  private lateinit var handlerThread: HandlerThread
  private lateinit var handler: Handler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)

    handlerThread = HandlerThread("ClientSettings")
    handlerThread.start()
    handler = Handler(handlerThread.looper)
  }

  override fun onDestroy() {
    super.onDestroy()
    handlerThread.quit()
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences, rootKey)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      findPreference<Preference>(getString(R.string.preference_notification_sound_key))?.isVisible = false
      findPreference<Preference>(getString(R.string.preference_notification_vibration_key))?.isVisible = false
      findPreference<Preference>(getString(R.string.preference_notification_light_key))?.isVisible = false
    } else {
      findPreference<Preference>(getString(R.string.preference_notification_configure_key))?.isVisible = false
    }
    findPreference<Preference>(getString(R.string.preference_clear_cache_key))?.setOnPreferenceClickListener {
      activity?.let {
        handler.post {
          QuasselDatabase.Creator.init(it).message().clearMessages()
        }
      }
      true
    }
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

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String) {
    updateSummary(findPreference<ListPreference>(key))
    val appearanceSettings = Settings.appearance(context!!)
    if (this.appearanceSettings.theme != appearanceSettings.theme ||
        this.appearanceSettings.language != appearanceSettings.language) {
      activity?.recreate()
    }
  }

  private fun updateSummary(preference: ListPreference?) {
    if (preference != null) {
      preference.summary = preference.entry
    }
  }

  private fun initSummary(preference: Preference) {
    if (preference is PreferenceGroup) {
      (0 until preference.preferenceCount).asSequence().map(preference::getPreference).forEach(::initSummary)
    } else if (preference is ListPreference) {
      updateSummary(preference)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.activity_settings, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.action_certificates -> {
      WhitelistActivity.launch(requireContext())
      true
    }
    R.id.action_crashes      -> {
      CrashActivity.launch(requireContext())
      true
    }
    R.id.action_about        -> {
      AboutActivity.launch(requireContext())
      true
    }
    else                     -> super.onOptionsItemSelected(item)
  }
}
