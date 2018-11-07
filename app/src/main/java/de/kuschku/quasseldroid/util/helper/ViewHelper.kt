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

package de.kuschku.quasseldroid.util.helper

import android.view.View
import androidx.appcompat.widget.TooltipCompat

fun View.visibleIf(check: Boolean) = if (check) {
  this.visibility = View.VISIBLE
} else {
  this.visibility = View.GONE
}

fun View.setTooltip(tooltipText: CharSequence? = this.contentDescription) =
  TooltipCompat.setTooltipText(this, tooltipText)
