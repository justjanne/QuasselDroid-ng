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

import android.content.Context
import android.graphics.Typeface
import android.text.Spanned
import android.text.style.*
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.styledAttributes
import java.util.*
import javax.inject.Inject

class IrcFormatSerializer @Inject constructor(context: Context) {
  private val mircColors = context.theme.styledAttributes(
    R.attr.mircColor00, R.attr.mircColor01, R.attr.mircColor02, R.attr.mircColor03,
    R.attr.mircColor04, R.attr.mircColor05, R.attr.mircColor06, R.attr.mircColor07,
    R.attr.mircColor08, R.attr.mircColor09, R.attr.mircColor10, R.attr.mircColor11,
    R.attr.mircColor12, R.attr.mircColor13, R.attr.mircColor14, R.attr.mircColor15,
    R.attr.mircColor16, R.attr.mircColor17, R.attr.mircColor18, R.attr.mircColor19,
    R.attr.mircColor20, R.attr.mircColor21, R.attr.mircColor22, R.attr.mircColor23,
    R.attr.mircColor24, R.attr.mircColor25, R.attr.mircColor26, R.attr.mircColor27,
    R.attr.mircColor28, R.attr.mircColor29, R.attr.mircColor30, R.attr.mircColor31,
    R.attr.mircColor32, R.attr.mircColor33, R.attr.mircColor34, R.attr.mircColor35,
    R.attr.mircColor36, R.attr.mircColor37, R.attr.mircColor38, R.attr.mircColor39,
    R.attr.mircColor40, R.attr.mircColor41, R.attr.mircColor42, R.attr.mircColor43,
    R.attr.mircColor44, R.attr.mircColor45, R.attr.mircColor46, R.attr.mircColor47,
    R.attr.mircColor48, R.attr.mircColor49, R.attr.mircColor50, R.attr.mircColor51,
    R.attr.mircColor52, R.attr.mircColor53, R.attr.mircColor54, R.attr.mircColor55,
    R.attr.mircColor56, R.attr.mircColor57, R.attr.mircColor58, R.attr.mircColor59,
    R.attr.mircColor60, R.attr.mircColor61, R.attr.mircColor62, R.attr.mircColor63,
    R.attr.mircColor64, R.attr.mircColor65, R.attr.mircColor66, R.attr.mircColor67,
    R.attr.mircColor68, R.attr.mircColor69, R.attr.mircColor70, R.attr.mircColor71,
    R.attr.mircColor72, R.attr.mircColor73, R.attr.mircColor74, R.attr.mircColor75,
    R.attr.mircColor76, R.attr.mircColor77, R.attr.mircColor78, R.attr.mircColor79,
    R.attr.mircColor80, R.attr.mircColor81, R.attr.mircColor82, R.attr.mircColor83,
    R.attr.mircColor84, R.attr.mircColor85, R.attr.mircColor86, R.attr.mircColor87,
    R.attr.mircColor88, R.attr.mircColor89, R.attr.mircColor90, R.attr.mircColor91,
    R.attr.mircColor92, R.attr.mircColor93, R.attr.mircColor94, R.attr.mircColor95,
    R.attr.mircColor96, R.attr.mircColor97, R.attr.mircColor98
  ) {
    IntArray(99) {
      getColor(it, 0)
    }
  }

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
