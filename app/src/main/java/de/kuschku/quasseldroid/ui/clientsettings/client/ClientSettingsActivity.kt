/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
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

import android.content.Context
import android.content.Intent
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import de.kuschku.quasseldroid.util.ui.settings.ListPreferenceDialogFragmentCompat
import de.kuschku.quasseldroid.util.ui.settings.SettingsActivity

class ClientSettingsActivity : SettingsActivity(ClientSettingsFragment()),
                               PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {
  override fun onPreferenceDisplayDialog(caller: PreferenceFragmentCompat, pref: Preference?) =
    when (pref) {
      is ListPreference -> {
        val f = ListPreferenceDialogFragmentCompat.newInstance(pref.getKey())
        f.setTargetFragment(fragment, 0)
        f.show(supportFragmentManager!!, DIALOG_FRAGMENT_TAG)
        true
      }
      else              -> false
    }

  companion object {
    private const val DIALOG_FRAGMENT_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG"

    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, ClientSettingsActivity::class.java)
  }
}
