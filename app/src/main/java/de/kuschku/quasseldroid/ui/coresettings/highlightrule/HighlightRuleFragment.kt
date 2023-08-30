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

package de.kuschku.quasseldroid.ui.coresettings.highlightrule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import de.kuschku.libquassel.quassel.syncables.HighlightRuleManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.ui.settings.fragment.Changeable
import de.kuschku.quasseldroid.util.ui.settings.fragment.Savable
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment

class HighlightRuleFragment : ServiceBoundSettingsFragment(), Savable,
                              Changeable {
  lateinit var enabled: SwitchCompat
  lateinit var name: EditText
  lateinit var isRegex: SwitchCompat
  lateinit var isCaseSensitive: SwitchCompat
  lateinit var sender: EditText
  lateinit var channel: EditText

  private var rule: HighlightRuleManager.HighlightRule? = null

  private var isInverse: Boolean? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    val view = inflater.inflate(R.layout.settings_highlightrule, container, false)
    this.enabled = view.findViewById(R.id.enabled)
    this.name = view.findViewById(R.id.name)
    this.isRegex = view.findViewById(R.id.is_regex)
    this.isCaseSensitive = view.findViewById(R.id.is_case_sensitive)
    this.sender = view.findViewById(R.id.sender)
    this.channel = view.findViewById(R.id.channel)

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
    id = rule?.id ?: -1,
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
