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

package de.kuschku.quasseldroid.util.irc.format

import android.text.SpannableStringBuilder
import android.text.Spanned
import de.kuschku.quasseldroid.util.irc.format.spans.*
import javax.inject.Inject

/**
 * A helper class to turn mIRC formatted Strings into Android’s SpannableStrings with the same
 * color and format codes
 */
class IrcFormatDeserializer @Inject constructor() {
  /**
   * Function to handle mIRC formatted strings
   *
   * @param str mIRC formatted String
   * @return a CharSequence with Android’s span format representing the input string
   */
  fun formatString(mircColors: IntArray, str: String?, colorize: Boolean): CharSequence {
    if (str == null) return ""

    val plainText = SpannableStringBuilder()
    var bold: FormatDescription<BoldIrcFormat>? = null
    var italic: FormatDescription<ItalicIrcFormat>? = null
    var underline: FormatDescription<UnderlineIrcFormat>? = null
    var strikethrough: FormatDescription<StrikethroughIrcFormat>? = null
    var monospace: FormatDescription<MonospaceIrcFormat>? = null
    var color: FormatDescription<ColorIrcFormat>? = null
    var hexColor: FormatDescription<HexIrcFormat>? = null

    // Iterating over every character
    var normalCount = 0
    var i = 0
    while (i < str.length) {
      val character = str[i]
      when (character) {
        CODE_BOLD          -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          bold = if (bold != null) {
            if (colorize) bold.apply(plainText, plainText.length)
            null
            // Otherwise create a new one
          } else {
            FormatDescription(plainText.length, BoldIrcFormat())
          }
        }
        CODE_ITALIC        -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          italic = if (italic != null) {
            if (colorize) italic.apply(plainText, plainText.length)
            null
            // Otherwise create a new one
          } else {
            FormatDescription(plainText.length, ItalicIrcFormat())
          }
        }
        CODE_UNDERLINE     -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          underline = if (underline != null) {
            if (colorize) underline.apply(plainText, plainText.length)
            null
            // Otherwise create a new one
          } else {
            FormatDescription(plainText.length, UnderlineIrcFormat())
          }
        }
        CODE_STRIKETHROUGH -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          strikethrough = if (strikethrough != null) {
            if (colorize) strikethrough.apply(plainText, plainText.length)
            null
            // Otherwise create a new one
          } else {
            FormatDescription(plainText.length, StrikethroughIrcFormat())
          }
        }
        CODE_MONOSPACE     -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          monospace = if (monospace != null) {
            if (colorize) monospace.apply(plainText, plainText.length)
            null
            // Otherwise create a new one
          } else {
            FormatDescription(plainText.length, MonospaceIrcFormat())
          }
        }
        CODE_COLOR         -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          val foregroundStart = i + 1
          val foregroundEnd = findEndOfNumber(str, foregroundStart)
          // If we have a foreground element
          if (foregroundEnd > foregroundStart) {
            val foreground = readNumber(str, foregroundStart, foregroundEnd)

            var background: Byte = -1
            var backgroundEnd = -1
            // If we have a background code, read it
            if (str.length > foregroundEnd && str[foregroundEnd] == ',') {
              backgroundEnd = findEndOfNumber(str, foregroundEnd + 1)
              background = readNumber(str, foregroundEnd + 1, backgroundEnd)
            }
            // If previous element was also a color element, try to reuse background
            if (color != null) {
              // Apply old format
              if (colorize) color.apply(plainText, plainText.length)
              // Reuse old background, if possible
              if (background.toInt() == -1)
                background = color.format.background
            }
            // Add new format
            color = FormatDescription(
              plainText.length, ColorIrcFormat(foreground, background, mircColors)
            )

            // i points in front of the next character
            i = (if (backgroundEnd == -1) foregroundEnd else backgroundEnd) - 1

            // Otherwise assume this is a closing tag
          } else if (color != null) {
            if (colorize) color.apply(plainText, plainText.length)
            color = null
          }
        }
        CODE_HEXCOLOR      -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          val foregroundStart = i + 1
          val foregroundEnd = findEndOfHexNumber(str, foregroundStart)
          // If we have a foreground element
          if (foregroundEnd > foregroundStart) {
            val foreground = readHexNumber(str, foregroundStart, foregroundEnd)

            var background: Int = -1
            var backgroundEnd = -1
            // If we have a background code, read it
            if (str.length > foregroundEnd && str[foregroundEnd] == ',') {
              backgroundEnd = findEndOfHexNumber(str, foregroundEnd + 1)
              background = readHexNumber(str, foregroundEnd + 1, backgroundEnd)
            }
            // If previous element was also a color element, try to reuse background
            if (hexColor != null) {
              // Apply old format
              if (colorize) hexColor.apply(plainText, plainText.length)
              // Reuse old background, if possible
              if (background == -1)
                background = hexColor.format.background
            }
            // Add new format
            hexColor = FormatDescription(plainText.length, HexIrcFormat(foreground, background))

            // i points in front of the next character
            i = (if (backgroundEnd == -1) foregroundEnd else backgroundEnd) - 1

            // Otherwise assume this is a closing tag
          } else if (hexColor != null) {
            if (colorize) hexColor.apply(plainText, plainText.length)
            hexColor = null
          }
        }
        CODE_SWAP          -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If we have a color tag before, apply it, and create a new one with swapped colors
          if (color != null) {
            if (colorize) color.apply(plainText, plainText.length)
            color = FormatDescription(
              plainText.length, color.format.copySwapped()
            )
          }
        }
        CODE_RESET         -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // End all formatting tags
          if (bold != null) {
            if (colorize) bold.apply(plainText, plainText.length)
            bold = null
          }
          if (italic != null) {
            if (colorize) italic.apply(plainText, plainText.length)
            italic = null
          }
          if (underline != null) {
            if (colorize) underline.apply(plainText, plainText.length)
            underline = null
          }
          if (color != null) {
            if (colorize) color.apply(plainText, plainText.length)
            color = null
          }
          if (hexColor != null) {
            if (colorize) hexColor.apply(plainText, plainText.length)
            hexColor = null
          }
        }
        else               -> {
          // Just append it, if it’s not special
          normalCount++
        }
      }
      i++
    }

    // End all formatting tags
    if (bold != null) {
      if (colorize) bold.apply(plainText, plainText.length)
    }
    if (italic != null) {
      if (colorize) italic.apply(plainText, plainText.length)
    }
    if (underline != null) {
      if (colorize) underline.apply(plainText, plainText.length)
    }
    if (strikethrough != null) {
      if (colorize) strikethrough.apply(plainText, plainText.length)
    }
    if (monospace != null) {
      if (colorize) monospace.apply(plainText, plainText.length)
    }
    if (color != null) {
      if (colorize) color.apply(plainText, plainText.length)
    }
    if (hexColor != null) {
      if (colorize) hexColor.apply(plainText, plainText.length)
    }
    plainText.append(str.substring(str.length - normalCount, str.length))
    return plainText
  }

  private interface IrcFormat {
    fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int)
  }

  private class FormatDescription<out U : IrcFormat>(val start: Int, val format: U) {
    fun apply(editable: SpannableStringBuilder, end: Int) {
      format.applyTo(editable, start, end)
    }
  }

  private class ItalicIrcFormat : IrcFormat {
    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
      editable.setSpan(IrcItalicSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  private class UnderlineIrcFormat : IrcFormat {
    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
      editable.setSpan(IrcUnderlineSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  private class StrikethroughIrcFormat : IrcFormat {
    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
      editable.setSpan(IrcStrikethroughSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  private class MonospaceIrcFormat : IrcFormat {
    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
      editable.setSpan(IrcMonospaceSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  private class BoldIrcFormat : IrcFormat {
    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
      editable.setSpan(IrcBoldSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

  private inner class HexIrcFormat(val foreground: Int, val background: Int) : IrcFormat {
    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
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

  private inner class ColorIrcFormat(val foreground: Byte, val background: Byte,
                                     val mircColors: IntArray) : IrcFormat {
    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
      if (foreground.toInt() >= 0 && foreground.toInt() < mircColors.size) {
        editable.setSpan(
          IrcForegroundColorSpan.MIRC(foreground.toInt(), mircColors[foreground.toInt()]), from, to,
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
      }
      if (background.toInt() >= 0 && background.toInt() < mircColors.size) {
        editable.setSpan(
          IrcBackgroundColorSpan.MIRC(background.toInt(), mircColors[background.toInt()]), from, to,
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
      }
    }

    fun copySwapped(): ColorIrcFormat {
      return ColorIrcFormat(background, foreground, mircColors)
    }
  }

  companion object {
    private const val CODE_BOLD = 0x02.toChar()
    private const val CODE_COLOR = 0x03.toChar()
    private const val CODE_HEXCOLOR = 0x04.toChar()
    private const val CODE_ITALIC = 0x1D.toChar()
    private const val CODE_UNDERLINE = 0x1F.toChar()
    private const val CODE_STRIKETHROUGH = 0x1E.toChar()
    private const val CODE_MONOSPACE = 0x11.toChar()
    private const val CODE_SWAP = 0x16.toChar()
    private const val CODE_RESET = 0x0F.toChar()

    /**
     * Try to read a number from a String in specified bounds
     *
     * @param str   String to be read from
     * @param start Start index (inclusive)
     * @param end   End index (exclusive)
     * @return The byte represented by the digits read from the string
     */
    fun readNumber(str: String, start: Int, end: Int): Byte {
      val result = str.substring(start, end)
      return if (result.isEmpty())
        -1
      else
        result.toByteOrNull(10) ?: -1
    }

    /**
     * Try to read a number from a String in specified bounds
     *
     * @param str   String to be read from
     * @param start Start index (inclusive)
     * @param end   End index (exclusive)
     * @return The byte represented by the digits read from the string
     */
    fun readHexNumber(str: String, start: Int, end: Int): Int {
      val result = str.substring(start, end)
      return if (result.isEmpty())
        -1
      else
        result.toIntOrNull(16) ?: -1
    }

    /**
     * @param str   String to be searched in
     * @param start Start position (inclusive)
     * @return Index of first character that is not a digit
     */
    private fun findEndOfNumber(str: String, start: Int): Int {
      val validCharCodes = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
      val searchFrame = str.substring(start)
      var i = 0
      while (i < 2 && i < searchFrame.length) {
        if (!validCharCodes.contains(searchFrame[i])) {
          break
        }
        i++
      }
      return start + i
    }

    /**
     * @param str   String to be searched in
     * @param start Start position (inclusive)
     * @return Index of first character that is not a digit
     */
    private fun findEndOfHexNumber(str: String, start: Int): Int {
      val validCharCodes = setOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b',
        'c', 'd', 'e', 'f'
      )
      val searchFrame = str.substring(start)
      var i = 0
      while (i < 6 && i < searchFrame.length) {
        if (!validCharCodes.contains(searchFrame[i])) {
          break
        }
        i++
      }
      return start + i
    }
  }
}
