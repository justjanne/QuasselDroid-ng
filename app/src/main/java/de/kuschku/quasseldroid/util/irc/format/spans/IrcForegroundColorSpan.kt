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

package de.kuschku.quasseldroid.util.irc.format.spans

import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt

sealed class IrcForegroundColorSpan<T : IrcForegroundColorSpan<T>>(@ColorInt color: Int) :
  ForegroundColorSpan(color), Copyable<T> {

  override fun updateDrawState(ds: TextPaint) {
    ds.color = foregroundColor
    ds.linkColor = foregroundColor
  }

  class MIRC(private val mircColor: Int, @ColorInt color: Int) :
    IrcForegroundColorSpan<MIRC>(color), Copyable<MIRC> {
    override fun copy() = MIRC(mircColor, foregroundColor)
    override fun toString(): String {
      return "IrcForegroundColorSpan.MIRC(mircColor=$mircColor, color=${foregroundColor.toUInt().toString(
        16)})"
    }

    override fun equals(other: Any?) = when (other) {
      is MIRC -> other.mircColor == mircColor
      else    -> false
    }

    override fun hashCode(): Int {
      return mircColor
    }
  }

  class HEX(@ColorInt color: Int) :
    IrcForegroundColorSpan<HEX>(color), Copyable<HEX> {
    override fun copy() = HEX(foregroundColor)
    override fun toString(): String {
      return "IrcBackgroundColorSpan.HEX(color=${foregroundColor.toUInt().toString(16)})"
    }

    override fun equals(other: Any?) = when (other) {
      is HEX -> other.foregroundColor == foregroundColor
      else   -> false
    }

    override fun hashCode(): Int {
      return foregroundColor
    }
  }
}
