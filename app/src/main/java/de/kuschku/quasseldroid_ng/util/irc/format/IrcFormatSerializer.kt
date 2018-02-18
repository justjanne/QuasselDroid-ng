package de.kuschku.quasseldroid_ng.util.irc.format

import android.content.Context
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.styledAttributes
import de.kuschku.quasseldroid_ng.util.irc.format.spans.IrcBackgroundColorSpan
import de.kuschku.quasseldroid_ng.util.irc.format.spans.IrcBoldSpan
import de.kuschku.quasseldroid_ng.util.irc.format.spans.IrcForegroundColorSpan
import de.kuschku.quasseldroid_ng.util.irc.format.spans.IrcItalicSpan
import java.util.*

class IrcFormatSerializer internal constructor(private val context: Context) {

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

    var i = start
    while (i < end) {
      next = text.nextSpanTransition(i, end, CharacterStyle::class.java)
      val style = text.getSpans(i, next, CharacterStyle::class.java)

      var afterForeground = -1
      var afterBackground = -1
      var afterBold = false
      var afterUnderline = false
      var afterItalic = false

      for (aStyle in style) {
        if (text.getSpanFlags(aStyle) and Spanned.SPAN_COMPOSING != 0)
          continue

        if (aStyle is IrcBoldSpan) {
          afterBold = true
        } else if (aStyle is IrcItalicSpan) {
          afterItalic = true
        } else if (aStyle is UnderlineSpan) {
          afterUnderline = true
        } else if (aStyle is IrcForegroundColorSpan) {
          afterForeground = aStyle.mircColor
        } else if (aStyle is IrcBackgroundColorSpan) {
          afterBackground = aStyle.mircColor
        } else if (aStyle is ForegroundColorSpan) {
          afterForeground = 0
        } else if (aStyle is BackgroundColorSpan) {
          afterBackground = 0
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
    val CODE_SWAP: Char = 0x16.toChar()
    val CODE_RESET: Char = 0x0F.toChar()
  }
}
