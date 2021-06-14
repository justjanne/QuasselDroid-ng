/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package de.kuschku.quasseldroid.util

import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout

fun DrawerLayout.setEdgeSize(edgeSize: Int) {
  val leftDragger: ViewDragHelper = getField("mLeftDragger") ?: return
  val rightDragger: ViewDragHelper = getField("mRightDragger") ?: return

  val density = context.resources.displayMetrics.density
  leftDragger.setField("mEdgeSize", (edgeSize * density + 0.5f).toInt())
  rightDragger.setField("mEdgeSize", (edgeSize * density + 0.5f).toInt())
}
