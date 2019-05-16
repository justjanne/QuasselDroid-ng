/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.quasseldroid.util.irc.format

import android.content.Context
import android.text.SpannableStringBuilder
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.getColorCompat
import de.kuschku.quasseldroid.util.irc.format.model.FormatDescription
import de.kuschku.quasseldroid.util.irc.format.model.FormatInfo
import de.kuschku.quasseldroid.util.irc.format.model.IrcFormat
import javax.inject.Inject

/**
 * A helper class to turn mIRC formatted Strings into Android’s SpannableStrings with the same
 * color and format codes
 */
class IrcFormatDeserializer(private val mircColors: IntArray) {
  @Inject
  constructor(context: Context) : this(
    mircColors = listOf(
      R.color.mircColor00, R.color.mircColor01, R.color.mircColor02, R.color.mircColor03,
      R.color.mircColor04, R.color.mircColor05, R.color.mircColor06, R.color.mircColor07,
      R.color.mircColor08, R.color.mircColor09, R.color.mircColor10, R.color.mircColor11,
      R.color.mircColor12, R.color.mircColor13, R.color.mircColor14, R.color.mircColor15,
      R.color.mircColor16, R.color.mircColor17, R.color.mircColor18, R.color.mircColor19,
      R.color.mircColor20, R.color.mircColor21, R.color.mircColor22, R.color.mircColor23,
      R.color.mircColor24, R.color.mircColor25, R.color.mircColor26, R.color.mircColor27,
      R.color.mircColor28, R.color.mircColor29, R.color.mircColor30, R.color.mircColor31,
      R.color.mircColor32, R.color.mircColor33, R.color.mircColor34, R.color.mircColor35,
      R.color.mircColor36, R.color.mircColor37, R.color.mircColor38, R.color.mircColor39,
      R.color.mircColor40, R.color.mircColor41, R.color.mircColor42, R.color.mircColor43,
      R.color.mircColor44, R.color.mircColor45, R.color.mircColor46, R.color.mircColor47,
      R.color.mircColor48, R.color.mircColor49, R.color.mircColor50, R.color.mircColor51,
      R.color.mircColor52, R.color.mircColor53, R.color.mircColor54, R.color.mircColor55,
      R.color.mircColor56, R.color.mircColor57, R.color.mircColor58, R.color.mircColor59,
      R.color.mircColor60, R.color.mircColor61, R.color.mircColor62, R.color.mircColor63,
      R.color.mircColor64, R.color.mircColor65, R.color.mircColor66, R.color.mircColor67,
      R.color.mircColor68, R.color.mircColor69, R.color.mircColor70, R.color.mircColor71,
      R.color.mircColor72, R.color.mircColor73, R.color.mircColor74, R.color.mircColor75,
      R.color.mircColor76, R.color.mircColor77, R.color.mircColor78, R.color.mircColor79,
      R.color.mircColor80, R.color.mircColor81, R.color.mircColor82, R.color.mircColor83,
      R.color.mircColor84, R.color.mircColor85, R.color.mircColor86, R.color.mircColor87,
      R.color.mircColor88, R.color.mircColor89, R.color.mircColor90, R.color.mircColor91,
      R.color.mircColor92, R.color.mircColor93, R.color.mircColor94, R.color.mircColor95,
      R.color.mircColor96, R.color.mircColor97, R.color.mircColor98
    ).map(context::getColorCompat).toIntArray()
  )

  /**
   * Function to handle mIRC formatted strings
   *
   * @param content mIRC formatted String
   * @return a CharSequence with Android’s span format representing the input string
   */
  fun formatString(str: String?, colorize: Boolean,
                   output: MutableList<FormatInfo>? = null): CharSequence {
    if (str == null) return ""

    val plainText = SpannableStringBuilder()
    var bold: FormatDescription<IrcFormat.Bold>? = null
    var italic: FormatDescription<IrcFormat.Italic>? = null
    var underline: FormatDescription<IrcFormat.Underline>? = null
    var strikethrough: FormatDescription<IrcFormat.Strikethrough>? = null
    var monospace: FormatDescription<IrcFormat.Monospace>? = null
    var color: FormatDescription<IrcFormat.Color>? = null
    var hexColor: FormatDescription<IrcFormat.Hex>? = null

    fun applyFormat(desc: FormatDescription<IrcFormat>) {
      if (output != null) {
        output.add(FormatInfo(desc.start, plainText.length, desc.format))
      } else {
        desc.apply(plainText, plainText.length)
      }
    }

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
            if (colorize) applyFormat(bold)
            null
            // Otherwise create a new one
          } else {
            FormatDescription(plainText.length,
                              IrcFormat.Bold)
          }
        }
        CODE_ITALIC        -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          italic = if (italic != null) {
            if (colorize) applyFormat(italic)
            null
            // Otherwise create a new one
          } else {
            FormatDescription(plainText.length,
                              IrcFormat.Italic)
          }
        }
        CODE_UNDERLINE     -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          underline = if (underline != null) {
            if (colorize) applyFormat(underline)
            null
            // Otherwise create a new one
          } else {
            FormatDescription(plainText.length,
                              IrcFormat.Underline)
          }
        }
        CODE_STRIKETHROUGH -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          strikethrough = if (strikethrough != null) {
            if (colorize) applyFormat(strikethrough)
            null
            // Otherwise create a new one
          } else {
            FormatDescription(plainText.length,
                              IrcFormat.Strikethrough)
          }
        }
        CODE_MONOSPACE     -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If there is an element on stack with the same code, close it
          monospace = if (monospace != null) {
            if (colorize) applyFormat(monospace)
            null
            // Otherwise create a new one
          } else {
            FormatDescription(plainText.length,
                              IrcFormat.Monospace)
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
              if (colorize) applyFormat(color)
              // Reuse old background, if possible
              if (background.toInt() == -1)
                background = color.format.background
            }
            // Add new format
            color = FormatDescription(
              plainText.length, IrcFormat.Color(foreground, background, this.mircColors)
            )

            // i points in front of the next character
            i = (if (backgroundEnd == -1) foregroundEnd else backgroundEnd) - 1

            // Otherwise assume this is a closing tag
          } else if (color != null) {
            if (colorize) applyFormat(color)
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
              if (colorize) applyFormat(hexColor)
              // Reuse old background, if possible
              if (background == -1)
                background = hexColor.format.background
            }
            // Add new format
            hexColor = FormatDescription(plainText.length,
                                         IrcFormat.Hex(
                                           foreground,
                                           background))

            // i points in front of the next character
            i = (if (backgroundEnd == -1) foregroundEnd else backgroundEnd) - 1

            // Otherwise assume this is a closing tag
          } else if (hexColor != null) {
            if (colorize) applyFormat(hexColor)
            hexColor = null
          }
        }
        CODE_SWAP          -> {
          plainText.append(str.substring(i - normalCount, i))
          normalCount = 0

          // If we have a color tag before, apply it, and create a new one with swapped colors
          if (color != null) {
            if (colorize) applyFormat(color)
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
            if (colorize) applyFormat(bold)
            bold = null
          }
          if (italic != null) {
            if (colorize) applyFormat(italic)
            italic = null
          }
          if (underline != null) {
            if (colorize) applyFormat(underline)
            underline = null
          }
          if (color != null) {
            if (colorize) applyFormat(color)
            color = null
          }
          if (hexColor != null) {
            if (colorize) applyFormat(hexColor)
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

    plainText.append(str.substring(str.length - normalCount, str.length))

    // End all formatting tags
    if (bold != null) {
      if (colorize) applyFormat(bold)
    }
    if (italic != null) {
      if (colorize) applyFormat(italic)
    }
    if (underline != null) {
      if (colorize) applyFormat(underline)
    }
    if (strikethrough != null) {
      if (colorize) applyFormat(strikethrough)
    }
    if (monospace != null) {
      if (colorize) applyFormat(monospace)
    }
    if (color != null) {
      if (colorize) applyFormat(color)
    }
    if (hexColor != null) {
      if (colorize) applyFormat(hexColor)
    }
    return plainText
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
      val searchFrame = str.substring(start)
      var i = 0
      loop@ while (i < 2 && i < searchFrame.length) {
        when (searchFrame[i]) {
          '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
            // Do nothing
          }
          else                                             -> break@loop
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
      val searchFrame = str.substring(start)
      var i = 0
      loop@ while (i < 6 && i < searchFrame.length) {
        when (searchFrame[i]) {
          '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b',
          'c', 'd', 'e', 'f' -> {
            // Do nothing
          }
          else               -> break@loop
        }
        i++
      }
      return start + i
    }
  }
}
