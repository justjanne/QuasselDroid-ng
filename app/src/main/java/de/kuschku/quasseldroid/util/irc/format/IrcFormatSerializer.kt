/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
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

import android.content.Context
import android.graphics.Typeface
import android.text.Spanned
import android.text.style.*
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.getColorCompat
import de.kuschku.quasseldroid.util.helper.styledAttributes
import java.util.*
import javax.inject.Inject

class IrcFormatSerializer @Inject constructor(context: Context) {
  private val mircColors = listOf(
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

  private val mircColorMap = mircColors.take(16).mapIndexed { index: Int, color: Int ->
    color to index
  }.toMap()

  private val colorForegroundMirc = context.theme.styledAttributes(R.attr.colorForegroundMirc) {
    getColor(0, 0)
  }

  fun toEscapeCodes(text: Spanned): String {
    val out = StringBuilder()
    withinParagraph(out, text, 0, text.length)
    return out.toString()
  }

  private fun withinParagraph(out: StringBuilder, text: Spanned, start: Int, end: Int) {
    fun writeBold() {
      out.append(CODE_BOLD)
    }

    fun writeItalic() {
      out.append(CODE_ITALIC)
    }

    fun writeUnderline() {
      out.append(CODE_UNDERLINE)
    }

    fun writeStrikethrough() {
      out.append(CODE_STRIKETHROUGH)
    }

    fun writeMonospace() {
      out.append(CODE_MONOSPACE)
    }

    fun writeColor(foreground: Int?, background: Int?) {
      out.append(CODE_COLOR)
      if (foreground == null && background != null) {
        out.append(
          String.format(Locale.US, "%02d,%02d", this.colorForegroundMirc, background)
        )
      } else if (background == null && foreground != null) {
        out.append(String.format(Locale.US, "%02d", foreground))
      } else if (background != null && foreground != null) {
        out.append(String.format(Locale.US, "%02d,%02d", foreground, background))
      }
    }

    fun writeSwap(foreground: Int?, background: Int?) {
      // Nothing supports this. Nothing. So we fall back to writing the colors manually
      // out.append(CODE_SWAP)
      writeColor(foreground, background)
    }

    fun writeHexColor(foreground: Int?, background: Int?) {
      out.append(CODE_HEXCOLOR)
      if (foreground != null) {
        out.append(String.format(Locale.US, "%06x", foreground and 0x00FFFFFF))
        if (background != null) {
          out.append(',')
          out.append(String.format(Locale.US, "%06x", background and 0x00FFFFFF))
        }
      }
    }

    fun writeReset() {
      out.append(CODE_RESET)
    }

    var next: Int
    var foreground: Int? = null
    var background: Int? = null
    var bold = false
    var underline = false
    var italic = false
    var strikethrough = false
    var monospace = false

    var i = start
    while (i < end) {
      next = text.nextSpanTransition(i, end, CharacterStyle::class.java)
      val style = text.getSpans(i, next, CharacterStyle::class.java)

      var afterForeground: Int? = null
      var afterBackground: Int? = null
      var afterBold = false
      var afterUnderline = false
      var afterItalic = false
      var afterStrikethrough = false
      var afterMonospace = false

      for (aStyle in style) {
        if (text.getSpanFlags(aStyle) and Spanned.SPAN_COMPOSING != 0)
          continue

        if (text.getSpanEnd(aStyle) <= i)
          continue

        when (aStyle) {
          is StyleSpan           -> {
            afterBold = afterBold || aStyle.style and Typeface.BOLD != 0
            afterItalic = afterItalic || aStyle.style and Typeface.ITALIC != 0
          }
          is UnderlineSpan       -> afterUnderline = true
          is StrikethroughSpan   -> afterStrikethrough = true
          is TypefaceSpan        -> afterMonospace = aStyle.family == "monospace"
          is ForegroundColorSpan -> afterForeground = aStyle.foregroundColor
          is BackgroundColorSpan -> afterBackground = aStyle.backgroundColor
        }
      }

      if (afterBold != bold) {
        writeBold()
      }

      if (afterUnderline != underline) {
        writeUnderline()
      }

      if (afterItalic != italic) {
        writeItalic()
      }

      if (afterStrikethrough != strikethrough) {
        writeStrikethrough()
      }

      if (afterMonospace != monospace) {
        writeMonospace()
      }

      if (afterForeground != foreground || afterBackground != background) {
        val foregroundCode = this.mircColorMap[foreground]
        val backgroundCode = this.mircColorMap[background]
        val afterForegroundCode = this.mircColorMap[afterForeground]
        val afterBackgroundCode = this.mircColorMap[afterBackground]

        val hasForegroundBefore = foreground != null
        val hasBackgroundBefore = background != null
        val foregroundBeforeCodeValid = foregroundCode != null && foregroundCode < 16
        val backgroundBeforeCodeValid = backgroundCode != null && backgroundCode < 16

        val hasForegroundAfter = afterForeground != null
        val hasBackgroundAfter = afterBackground != null
        val foregroundAfterCodeValid = afterForegroundCode != null && afterForegroundCode < 16
        val backgroundAfterCodeValid = afterBackgroundCode != null && afterBackgroundCode < 16

        if ((!hasBackgroundAfter || backgroundAfterCodeValid) &&
            (!hasForegroundAfter || foregroundAfterCodeValid) &&
            (hasBackgroundAfter || hasForegroundAfter)) {
          if (afterForegroundCode == backgroundCode && afterBackgroundCode == foregroundCode) {
            writeSwap(afterForegroundCode, afterBackgroundCode)
          } else {
            writeColor(afterForegroundCode, afterBackgroundCode)
          }
        } else if (hasForegroundAfter || hasBackgroundAfter) {
          writeHexColor(afterForeground, afterBackground)
        } else {
          if ((!hasBackgroundBefore || backgroundBeforeCodeValid) &&
              (!hasForegroundBefore || foregroundBeforeCodeValid)) {
            writeColor(afterForeground, afterBackground)
          } else {
            writeHexColor(afterForeground, afterBackground)
          }
        }
      }

      out.append(text.subSequence(i, next))

      bold = afterBold
      italic = afterItalic
      underline = afterUnderline
      strikethrough = afterStrikethrough
      monospace = afterMonospace
      background = afterBackground
      foreground = afterForeground
      i = next
    }

    if (bold || italic || underline || strikethrough || monospace || background != null || foreground != null)
      writeReset()
  }

  companion object {
    private const val CODE_BOLD: Char = 0x02.toChar()
    private const val CODE_COLOR: Char = 0x03.toChar()
    private const val CODE_HEXCOLOR = 0x04.toChar()
    private const val CODE_ITALIC: Char = 0x1D.toChar()
    private const val CODE_UNDERLINE: Char = 0x1F.toChar()
    private const val CODE_STRIKETHROUGH = 0x1E.toChar()
    private const val CODE_MONOSPACE = 0x11.toChar()
    private const val CODE_SWAP: Char = 0x16.toChar()
    private const val CODE_RESET: Char = 0x0F.toChar()
  }
}
