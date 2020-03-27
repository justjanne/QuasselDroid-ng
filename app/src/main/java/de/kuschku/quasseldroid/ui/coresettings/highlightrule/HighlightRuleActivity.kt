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

import android.content.Context
import android.content.Intent
import de.kuschku.libquassel.quassel.syncables.HighlightRuleManager
import de.kuschku.quasseldroid.util.ui.settings.ServiceBoundSettingsActivity

class HighlightRuleActivity : ServiceBoundSettingsActivity(HighlightRuleFragment()) {
  companion object {
    fun launch(
      context: Context,
      rule: HighlightRuleManager.HighlightRule? = null,
      ignore: Boolean = false
    ) = context.startActivity(intent(context, rule, ignore))

    fun intent(
      context: Context,
      rule: HighlightRuleManager.HighlightRule? = null,
      ignore: Boolean = false
    ) = Intent(context, HighlightRuleActivity::class.java).apply {
      if (rule != null) {
        putExtra("item", rule)
      }
      putExtra("inverse", ignore)
    }
  }
}
