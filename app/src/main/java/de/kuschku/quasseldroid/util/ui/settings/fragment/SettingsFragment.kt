/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.quasseldroid.util.ui.settings.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import dagger.android.support.DaggerFragment

abstract class SettingsFragment : DaggerFragment() {
  private lateinit var helper: SettingsFragmentHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
    helper = SettingsFragmentHelper(
      this as? Savable,
      this as? Deletable
    )
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    helper.onCreateOptionsMenu(menu, inflater)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem) =
    helper.onOptionsItemSelected(activity, item)
    ?: super.onOptionsItemSelected(item)
}
