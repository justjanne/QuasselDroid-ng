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

package de.kuschku.quasseldroid.util.ui.settings.fragment

import android.app.Activity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import de.kuschku.quasseldroid.R

class SettingsFragmentHelper(
  private val saveable: Savable?,
  private val deletable: Deletable?
) {
  fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.context_setting, menu)
    menu.findItem(R.id.action_save)?.isVisible = saveable != null
    menu.findItem(R.id.action_delete)?.isVisible = deletable != null
  }

  fun onOptionsItemSelected(activity: Activity?, item: MenuItem) = when (item.itemId) {
    R.id.action_save   -> {
      saveable?.let {
        if (it.onSave()) activity?.finish()
      }
      true
    }
    R.id.action_delete -> {
      activity?.let {
        deletable?.let {
          MaterialDialog.Builder(activity)
            .content(R.string.delete_confirmation)
            .positiveText(R.string.label_yes)
            .negativeText(R.string.label_no)
            .negativeColorAttr(R.attr.colorTextPrimary)
            .backgroundColorAttr(R.attr.colorBackgroundCard)
            .contentColorAttr(R.attr.colorTextPrimary)
            .onPositive { _, _ ->
              it.onDelete()
              activity.finish()
            }
            .build()
            .show()
        }
      }
      true
    }
    else               -> null
  }
}
