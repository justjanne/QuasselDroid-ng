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

import android.text.Editable
import android.text.Spanned
import de.kuschku.quasseldroid.util.irc.format.spans.Copyable

inline fun <reified U, T> Editable.removeSpans(
  range: IntRange, removeInvalid: Boolean = false, f: (U) -> T?
) where T : Copyable<T> {
  for (raw in getSpans<U>(range.start, range.endInclusive + 1, U::class.java)) {
    val spanFlags = getSpanFlags(raw)
    if (spanFlags and Spanned.SPAN_COMPOSING != 0) continue

    val spanEnd = getSpanEnd(raw)
    val spanStart = getSpanStart(raw)

    val span = f(raw)
    if (span == null) {
      if (removeInvalid)
        removeSpan(raw)
    } else {
      removeSpan(raw)

      for (spanRange in spanStart until spanEnd without range) {
        setSpan(
          span.copy(),
          spanRange.start,
          spanRange.endInclusive + 1,
          (spanFlags and 0x03.inv()) or 0x01
        )
      }
    }
  }
}
