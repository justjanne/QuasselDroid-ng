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

package de.kuschku.quasseldroid.util.ui.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.ui.ThemedActivity
import de.kuschku.quasseldroid.util.ui.settings.fragment.Changeable

abstract class SettingsActivity(protected val fragment: Fragment? = null) : ThemedActivity() {
  protected open fun fragment(): Fragment? = null

  private var changeable: Changeable? = null
  protected var actualFragment: Fragment? = null

  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  override fun onCreate(savedInstanceState: Bundle?) {
    val arguments = intent.extras
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    ButterKnife.bind(this)

    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    val existingFragment = savedInstanceState?.let {
      supportFragmentManager.getFragment(it, "settings_content")
    }

    actualFragment = existingFragment ?: this.fragment ?: this.fragment()
    if (existingFragment == null) {
      actualFragment?.let {
        val transaction = supportFragmentManager.beginTransaction()
        it.arguments = arguments
        transaction.replace(R.id.fragment_container, it)
        transaction.commit()
      }
    }

    this.changeable = fragment as? Changeable
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    actualFragment?.let {
      supportFragmentManager.putFragment(outState, "settings_content", it)
    }
  }

  private fun shouldNavigateAway(callback: () -> Unit) {
    val changeable = this.changeable
    if (changeable?.hasChanged() == true) {
      MaterialDialog.Builder(this)
        .content(R.string.cancel_confirmation)
        .positiveText(R.string.label_yes)
        .negativeText(R.string.label_no)
        .negativeColorAttr(R.attr.colorTextPrimary)
        .backgroundColorAttr(R.attr.colorBackgroundCard)
        .contentColorAttr(R.attr.colorTextPrimary)
        .onPositive { _, _ ->
          callback()
        }
        .build()
        .show()
    } else callback()
  }

  override fun onBackPressed() = shouldNavigateAway {
    super.onBackPressed()
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    android.R.id.home -> {
      shouldNavigateAway {
        if (supportParentActivityIntent != null) {
          startActivity(supportParentActivityIntent)
          finish()
        } else {
          super.onBackPressed()
        }
      }
      true
    }
    else              -> super.onOptionsItemSelected(item)
  }
}
