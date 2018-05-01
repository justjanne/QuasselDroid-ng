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

import android.content.res.Resources
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes

@ColorInt
fun Resources.getColorBackport(@ColorRes color: Int, theme: Resources.Theme): Int {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    getColor(color, theme)
  } else {
    // We have to use this method on older systems that don’t yet support the new method
    // which is used above
    getColor(color)
  }
}
