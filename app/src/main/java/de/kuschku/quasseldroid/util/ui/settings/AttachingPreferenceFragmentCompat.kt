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

package de.kuschku.quasseldroid.util.ui.settings

import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceCategory
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceScreen
import de.kuschku.quasseldroid.util.helper.preferences
import java.util.concurrent.atomic.AtomicInteger

abstract class AttachingPreferenceFragmentCompat : PreferenceFragmentCompat(), ActivityLauncher {
  private val nextRequestCode = AtomicInteger(0)

  private var activityResultListeners = emptySet<OnActivityResultListener>()

  override fun registerOnActivityResultListener(listener: OnActivityResultListener) {
    activityResultListeners += listener
  }

  override fun unregisterOnActivityResultListener(listener: OnActivityResultListener) {
    activityResultListeners -= listener
  }

  override fun getNextRequestCode() = nextRequestCode.getAndIncrement()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    attachPreference(preferenceScreen)
  }

  private fun attachPreference(preference: Preference) {
    when (preference) {
      is PreferenceScreen         -> preference.preferences().forEach(::attachPreference)
      is PreferenceCategory       -> preference.preferences().forEach(::attachPreference)
      is RequiresActivityLauncher -> preference.activityLauncher = this
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    for (it in activityResultListeners) {
      it.onActivityResult(requestCode, resultCode, data)
    }
  }
}
