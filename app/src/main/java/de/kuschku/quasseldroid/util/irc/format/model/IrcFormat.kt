/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.quasseldroid.util.irc.format.model

import android.text.Spannable
import android.text.Spanned
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.quasseldroid.util.irc.format.spans.*

sealed class IrcFormat {
  abstract fun applyTo(editable: Spannable, from: Int, to: Int)

  object Italic : IrcFormat() {
    override fun applyTo(editable: Spannable, from: Int, to: Int) {
      editable.setSpan(IrcItalicSpan(), from, to,
                       Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  object Underline : IrcFormat() {
    override fun applyTo(editable: Spannable, from: Int, to: Int) {
      editable.setSpan(IrcUnderlineSpan(), from, to,
                       Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  object Strikethrough : IrcFormat() {
    override fun applyTo(editable: Spannable, from: Int, to: Int) {
      editable.setSpan(IrcStrikethroughSpan(), from, to,
                       Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  object Monospace : IrcFormat() {
    override fun applyTo(editable: Spannable, from: Int, to: Int) {
      editable.setSpan(IrcMonospaceSpan(), from, to,
                       Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  object Bold : IrcFormat() {
    override fun applyTo(editable: Spannable, from: Int, to: Int) {
      editable.setSpan(IrcBoldSpan(), from, to,
                       Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  data class Hex(val foreground: Int, val background: Int) : IrcFormat() {
    override fun applyTo(editable: Spannable, from: Int, to: Int) {
      if (foreground >= 0) {
        editable.setSpan(
          IrcForegroundColorSpan.HEX(foreground or 0xFFFFFF.inv()), from, to,
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
      }
      if (background >= 0) {
        editable.setSpan(
          IrcBackgroundColorSpan.HEX(background or 0xFFFFFF.inv()), from, to,
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
      }
    }
  }

  data class Color(val foreground: Byte, val background: Byte,
                   private val mircColors: IntArray) : IrcFormat() {
    override fun applyTo(editable: Spannable, from: Int, to: Int) {
      if (foreground.toInt() >= 0 && foreground.toInt() < mircColors.size) {
        editable.setSpan(
          IrcForegroundColorSpan.MIRC(foreground.toInt(),
                                      mircColors[foreground.toInt()]),
          from,
          to,
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
      }
      if (background.toInt() >= 0 && background.toInt() < mircColors.size) {
        editable.setSpan(
          IrcBackgroundColorSpan.MIRC(background.toInt(),
                                      mircColors[background.toInt()]),
          from,
          to,
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
      }
    }

    fun copySwapped(): Color {
      return Color(background, foreground, mircColors)
    }

    override fun toString(): String {
      return "Color(foreground=$foreground, background=$background)"
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Color

      if (foreground != other.foreground) return false
      if (background != other.background) return false

      return true
    }

    override fun hashCode(): Int {
      var result = foreground.toInt()
      result = 31 * result + background
      return result
    }
  }

  data class Url(val target: String, val highlight: Boolean) : IrcFormat() {
    override fun applyTo(editable: Spannable, from: Int, to: Int) {
      editable.setSpan(
        QuasselURLSpan(target, highlight), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
      )
    }
  }

  data class Channel(val networkId: NetworkId, val target: String, val highlight: Boolean) :
    IrcFormat() {
    override fun applyTo(editable: Spannable, from: Int, to: Int) {
      editable.setSpan(
        ChannelLinkSpan(networkId, target, highlight), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
      )
    }
  }
}
