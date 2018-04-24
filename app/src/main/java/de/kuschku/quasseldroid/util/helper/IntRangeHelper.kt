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

package de.kuschku.quasseldroid.util.helper

import clamp

infix fun IntRange.without(other: IntRange): Iterable<IntRange> {
  val otherStart = minOf(other.start, other.last + 1).clamp(this.start, this.last + 1)
  val otherLast = maxOf(other.start, other.last + 1).clamp(this.start, this.last + 1)

  val startingFragment: IntRange = this.start until otherStart
  val endingFragment: IntRange = otherLast + 1 until this.last + 1

  return listOf(startingFragment, endingFragment).filter { it.last >= it.start }
}
