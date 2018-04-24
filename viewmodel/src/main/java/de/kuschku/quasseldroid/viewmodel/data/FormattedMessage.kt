/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.viewmodel.data

import android.graphics.drawable.Drawable

class FormattedMessage(
  val id: Int,
  val time: CharSequence,
  val name: CharSequence? = null,
  val content: CharSequence? = null,
  val combined: CharSequence,
  val fallbackDrawable: Drawable? = null,
  val realName: CharSequence? = null,
  val avatarUrls: List<String> = emptyList(),
  val isSelected: Boolean,
  val isExpanded: Boolean,
  val isMarkerLine: Boolean
)
