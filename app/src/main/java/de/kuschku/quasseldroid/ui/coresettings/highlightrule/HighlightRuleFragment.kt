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

package de.kuschku.quasseldroid.ui.coresettings.highlightrule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.HighlightRuleManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment

class HighlightRuleFragment : SettingsFragment(), SettingsFragment.Savable,
                              SettingsFragment.Changeable {
  @BindView(R.id.enabled)
  lateinit var enabled: SwitchCompat

  @BindView(R.id.name)
  lateinit var name: EditText

  @BindView(R.id.is_regex)
  lateinit var isRegex: SwitchCompat

  @BindView(R.id.is_case_sensitive)
  lateinit var isCaseSensitive: SwitchCompat

  @BindView(R.id.sender)
  lateinit var sender: EditText

  @BindView(R.id.channel)
  lateinit var channel: EditText

  private var rule: HighlightRuleManager.HighlightRule? = null

  private var isInverse: Boolean? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    val view = inflater.inflate(R.layout.settings_highlightrule, container, false)
    ButterKnife.bind(this, view)

    isInverse = arguments?.getBoolean("inverse")
    (arguments?.getSerializable("item") as? HighlightRuleManager.HighlightRule)?.let {
      rule = it
    }

    rule?.let { data ->
      enabled.isChecked = data.isEnabled
      name.setText(data.name)
      isRegex.isChecked = data.isRegEx
      isCaseSensitive.isChecked = data.isCaseSensitive
      sender.setText(data.sender)
      channel.setText(data.channel)
    }

    return view
  }

  private fun applyChanges() = HighlightRuleManager.HighlightRule(
    isInverse = rule?.isInverse ?: isInverse ?: false,
    isEnabled = enabled.isChecked,
    name = name.text.toString(),
    isRegEx = isRegex.isChecked,
    isCaseSensitive = isCaseSensitive.isChecked,
    sender = sender.text.toString(),
    channel = channel.text.toString()
  )

  override fun onSave() = rule.let { data ->
    requireActivity().setResult(
      Activity.RESULT_OK,
      Intent().also {
        it.putExtra("old", data)
        it.putExtra("new", applyChanges())
      }
    )
    true
  }

  override fun hasChanged() = rule != applyChanges()
}
