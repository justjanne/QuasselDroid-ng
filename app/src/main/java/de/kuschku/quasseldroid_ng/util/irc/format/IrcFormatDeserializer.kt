/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
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

package de.kuschku.quasseldroid_ng.util.irc.format


import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.UnderlineSpan
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.styledAttributes
import de.kuschku.quasseldroid_ng.util.irc.format.spans.IrcBackgroundColorSpan
import de.kuschku.quasseldroid_ng.util.irc.format.spans.IrcBoldSpan
import de.kuschku.quasseldroid_ng.util.irc.format.spans.IrcForegroundColorSpan
import de.kuschku.quasseldroid_ng.util.irc.format.spans.IrcItalicSpan
import java.util.*

/**
 * A helper class to turn mIRC formatted Strings into Android’s SpannableStrings with the same
 * color and format codes
 */
class IrcFormatDeserializer(private val context: Context) {

  /**
   * Function to handle mIRC formatted strings
   *
   * @param str mIRC formatted String
   * @return a CharSequence with Android’s span format representing the input string
   */
  fun formatString(str: String?, colorize: Boolean): CharSequence {
    if (str == null) return ""

    val plainText = SpannableStringBuilder()
    var bold: FormatDescription? = null
    var italic: FormatDescription? = null
    var underline: FormatDescription? = null
    var color: FormatDescription? = null

    // Iterating over every character
    var normalCount = 0
    var i = 0
    while (i < str.length) {
      val character = str[i]
      when (character) {
        CODE_BOLD      -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          if (bold != null) {
            if (colorize) bold.apply(plainText, plainText.length)
            bold = null
            // Otherwise create a new one
          } else {
            val format = fromId(character)
            bold = FormatDescription(plainText.length, format!!)
          }
        }
        CODE_ITALIC    -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          if (italic != null) {
            if (colorize) italic.apply(plainText, plainText.length)
            italic = null
            // Otherwise create a new one
          } else {
            val format = fromId(character)
            italic = FormatDescription(plainText.length, format!!)
          }
        }
        CODE_UNDERLINE -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          if (underline != null) {
            if (colorize) underline.apply(plainText, plainText.length)
            underline = null
            // Otherwise create a new one
          } else {
            val format = fromId(character)
            underline = FormatDescription(plainText.length, format!!)
          }
        }
        CODE_COLOR     -> {
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
                background = (color.format as ColorIrcFormat).background
            }
            // Add new format
            color = FormatDescription(plainText.length, ColorIrcFormat(foreground, background))

            // i points in front of the next character
            i = (if (backgroundEnd == -1) foregroundEnd else backgroundEnd) - 1

            // Otherwise assume this is a closing tag
          } else if (color != null) {
            if (colorize) color.apply(plainText, plainText.length)
            color = null
          }
        }
        CODE_SWAP      -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If we have a color tag before, apply it, and create a new one with swapped colors
          if (color != null) {
            if (colorize) color.apply(plainText, plainText.length)
            color = FormatDescription(
              plainText.length, (color.format as ColorIrcFormat).copySwapped()
            )
          }
        }
        CODE_RESET     -> {
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
        }
        else           -> {
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
    if (color != null) {
      if (colorize) color.apply(plainText, plainText.length)
    }
    plainText.append(str.substring(str.length - normalCount, str.length))
    return plainText
  }

  private interface IrcFormat {
    fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int)

    fun id(): Byte
  }

  private class FormatDescription(val start: Int, val format: IrcFormat) {

    fun apply(editable: SpannableStringBuilder, end: Int) {
      format.applyTo(editable, start, end)
    }
  }

  private class ItalicIrcFormat : IrcFormat {
    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
      editable.setSpan(IrcItalicSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    override fun id(): Byte {
      return CODE_ITALIC.toByte()
    }
  }

  private class UnderlineIrcFormat : IrcFormat {
    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
      editable.setSpan(UnderlineSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    override fun id(): Byte {
      return CODE_UNDERLINE.toByte()
    }
  }

  private class BoldIrcFormat : IrcFormat {
    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
      editable.setSpan(IrcBoldSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    override fun id(): Byte {
      return CODE_BOLD.toByte()
    }
  }

  private inner class ColorIrcFormat(val foreground: Byte, val background: Byte) : IrcFormat {

    override fun applyTo(editable: SpannableStringBuilder, from: Int, to: Int) {
      val mircColors = context.theme.styledAttributes(
        R.attr.mircColor0, R.attr.mircColor1, R.attr.mircColor2, R.attr.mircColor3,
        R.attr.mircColor4, R.attr.mircColor5, R.attr.mircColor6, R.attr.mircColor7,
        R.attr.mircColor8, R.attr.mircColor9, R.attr.mircColorA, R.attr.mircColorB,
        R.attr.mircColorC, R.attr.mircColorD, R.attr.mircColorE, R.attr.mircColorF
      ) {
        IntArray(16) {
          getColor(it, 0)
        }
      }

      if (foreground.toInt() != -1 && foreground.toInt() != 99) {
        editable.setSpan(
          IrcForegroundColorSpan(foreground.toInt(), mircColors[foreground % 16]), from, to,
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
      }
      if (background.toInt() != -1 && background.toInt() != 99) {
        editable.setSpan(
          IrcBackgroundColorSpan(background.toInt(), mircColors[background % 16]), from, to,
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
      }
    }

    fun copySwapped(): ColorIrcFormat {
      return ColorIrcFormat(background, foreground)
    }

    override fun id(): Byte {
      return CODE_COLOR.toByte()
    }
  }

  companion object {
    val CODE_BOLD = 0x02.toChar()
    val CODE_COLOR = 0x03.toChar()
    val CODE_ITALIC = 0x1D.toChar()
    val CODE_UNDERLINE = 0x1F.toChar()
    val CODE_SWAP = 0x16.toChar()
    val CODE_RESET = 0x0F.toChar()

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
        Integer.parseInt(result, 10).toByte()
    }

    /**
     * @param str   String to be searched in
     * @param start Start position (inclusive)
     * @return Index of first character that is not a digit
     */
    private fun findEndOfNumber(str: String, start: Int): Int {
      val validCharCodes = HashSet(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'))
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

    private fun fromId(id: Char) = when (id) {
      CODE_BOLD      -> BoldIrcFormat()
      CODE_ITALIC    -> ItalicIrcFormat()
      CODE_UNDERLINE -> UnderlineIrcFormat()
      else           -> null
    }
  }
}
