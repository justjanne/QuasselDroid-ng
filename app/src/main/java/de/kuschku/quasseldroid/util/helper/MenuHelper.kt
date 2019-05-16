/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.quasseldroid.util.helper

import android.content.Context
import android.view.Menu
import androidx.core.graphics.drawable.DrawableCompat
import de.kuschku.quasseldroid.R

fun Menu.retint(context: Context) {
  context.theme.styledAttributes(R.attr.colorControlNormal) {
    val color = getColor(0, 0)

    for (item in (0 until size()).map { getItem(it) }) {
      item.icon?.mutate()?.let { drawable ->
        DrawableCompat.setTint(drawable, color)
        item.icon = drawable
      }
    }
  }
}
