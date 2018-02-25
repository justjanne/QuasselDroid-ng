package de.kuschku.quasseldroid_ng.util.irc.format

import android.content.Context
import android.text.Spanned
import android.text.style.*
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.styledAttributes
import de.kuschku.quasseldroid_ng.util.irc.format.spans.*
import java.util.*

class IrcFormatSerializer internal constructor(private val context: Context) {
  val mircColors = context.theme.styledAttributes(
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

  fun toEscapeCodes(text: Spanned): String {
    val out = StringBuilder()
    withinParagraph(out, text, 0, text.length)
    return out.toString()
  }

  private fun withinParagraph(out: StringBuilder, text: Spanned,
                              start: Int, end: Int) {
    var next: Int
    var foreground = -1
    var background = -1
    var bold = false
    var underline = false
    var italic = false
    var strikethrough = false
    var monospace = false

    var i = start
    while (i < end) {
      next = text.nextSpanTransition(i, end, CharacterStyle::class.java)
      val style = text.getSpans(i, next, CharacterStyle::class.java)

      var afterForeground = -1
      var afterBackground = -1
      var afterBold = false
      var afterUnderline = false
      var afterItalic = false
      var afterStrikethrough = false
      var afterMonospace = false

      for (aStyle in style) {
        if (text.getSpanFlags(aStyle) and Spanned.SPAN_COMPOSING != 0)
          continue

        if (aStyle is IrcBoldSpan) {
          afterBold = true
        } else if (aStyle is IrcItalicSpan) {
          afterItalic = true
        } else if (aStyle is UnderlineSpan) {
          afterUnderline = true
        } else if (aStyle is StrikethroughSpan) {
          afterStrikethrough = true
        } else if (aStyle is IrcMonospaceSpan) {
          afterMonospace = true
        } else if (aStyle is IrcForegroundColorSpan) {
          afterForeground = aStyle.mircColor
        } else if (aStyle is IrcBackgroundColorSpan) {
          afterBackground = aStyle.mircColor
        } else if (aStyle is ForegroundColorSpan) {
          afterForeground = -1
        } else if (aStyle is BackgroundColorSpan) {
          afterBackground = -1
        }
      }

      if (afterBold != bold) {
        out.append(CODE_BOLD)
      }

      if (afterUnderline != underline) {
        out.append(CODE_UNDERLINE)
      }

      if (afterItalic != italic) {
        out.append(CODE_ITALIC)
      }

      if (afterStrikethrough != strikethrough) {
        out.append(CODE_STRIKETHROUGH)
      }

      if (afterMonospace != monospace) {
        out.append(CODE_MONOSPACE)
      }

      if (afterForeground != foreground || afterBackground != background) {
        if (afterForeground == background && afterBackground == foreground) {
          out.append(CODE_SWAP)
        } else {
          out.append(CODE_COLOR)
          if (afterBackground == -1) {
            if (afterForeground == -1) {
              // Foreground changed from a value to null, we don’t set any new foreground
              // Background changed from a value to null, we don’t set any new background
            } else {
              out.append(CODE_COLOR)
              out.append(String.format(Locale.US, "%02d", afterForeground))
            }
          } else if (background == afterBackground) {
            if (afterForeground == -1) {
              out.append(
                String.format(
                  Locale.US, "%02d",
                  context.theme.styledAttributes(R.attr.colorForegroundMirc) {
                    getColor(0, 0)
                  }
                )
              )
            } else {
              out.append(String.format(Locale.US, "%02d", afterForeground))
            }
          } else {
            if (afterForeground == -1) {
              out.append(
                String.format(
                  Locale.US, "%02d,%02d",
                  context.theme.styledAttributes(R.attr.colorForegroundMirc) {
                    getColor(0, 0)
                  },
                  afterBackground
                )
              )
            } else {
              out.append(String.format(Locale.US, "%02d,%02d", afterForeground, afterBackground))
            }
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

    if (bold || italic || underline || background != -1 || foreground != -1)
      out.append(CODE_RESET)
  }

  companion object {
    val CODE_BOLD: Char = 0x02.toChar()
    val CODE_COLOR: Char = 0x03.toChar()
    val CODE_ITALIC: Char = 0x1D.toChar()
    val CODE_UNDERLINE: Char = 0x1F.toChar()
    val CODE_STRIKETHROUGH = 0x1E.toChar()
    val CODE_MONOSPACE = 0x11.toChar()
    val CODE_SWAP: Char = 0x16.toChar()
    val CODE_RESET: Char = 0x0F.toChar()
  }
}
