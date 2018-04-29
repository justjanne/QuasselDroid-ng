/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.clientsettings.client

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
import de.kuschku.quasseldroid.ui.clientsettings.about.AboutActivity
import de.kuschku.quasseldroid.ui.clientsettings.crash.CrashActivity
import de.kuschku.quasseldroid.ui.clientsettings.whitelist.WhitelistActivity
import de.kuschku.quasseldroid.util.backport.DaggerPreferenceFragmentCompat
import javax.inject.Inject

class ClientSettingsFragment : DaggerPreferenceFragmentCompat(),
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
