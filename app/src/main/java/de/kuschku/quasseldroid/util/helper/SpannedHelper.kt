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

import android.text.Spanned

inline fun <reified U> Spanned.spans(range: IntRange) =
  getSpans(range.start, range.endInclusive + 1, U::class.java).filter {
    getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
    (getSpanEnd(it) != range.start ||
     getSpanFlags(it) and 0x02 != 0)
  }

inline fun <reified U> Spanned.spans(range: IntRange, f: (U) -> Boolean) =
  getSpans(range.start, range.last + 1, U::class.java).filter {
    f(it) &&
    getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
    (getSpanEnd(it) != range.start ||
     getSpanFlags(it) and 0x02 != 0)
  }

inline fun <reified U> Spanned.hasSpans(range: IntRange) =
  getSpans(range.start, range.endInclusive + 1, U::class.java).any {
    getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
    (getSpanEnd(it) != range.start ||
     getSpanFlags(it) and 0x02 != 0)
  }

inline fun <reified U> Spanned.hasSpans(range: IntRange, f: (U) -> Boolean) =
  getSpans(range.start, range.last + 1, U::class.java).any {
    f(it) &&
    getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
    (getSpanEnd(it) != range.start ||
     getSpanFlags(it) and 0x02 != 0)
  }
