/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
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

package de.kuschku.quasseldroid.ui.chat.input

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.text.InputType
import android.text.Spanned
import android.text.style.*
import android.util.AttributeSet
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.spans.*
import de.kuschku.quasseldroid.util.ui.DoubleClickHelper
import de.kuschku.quasseldroid.util.ui.EditTextSelectionChange

class RichEditText : EditTextSelectionChange {
  private val mircColors = this.context.theme.styledAttributes(
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
    IntArray(length(), { getColor(it, 0) })
  }
  private val mircColorMap = mircColors.withIndex().map { (key, value) -> key to value }.toMap()

  private var formattingListener: ((Boolean, Boolean, Boolean, Boolean, Boolean, Int?, Int?) -> Unit)? = null

  private val doubleClickHelper = DoubleClickHelper(this)

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr)

  init {
    setSelectionChangeListener(this::selectedFormattingChanged)
    setOnTouchListener(doubleClickHelper)
  }

  fun setFormattingListener(
    listener: ((Boolean, Boolean, Boolean, Boolean, Boolean, Int?, Int?) -> Unit)?) {
    this.formattingListener = listener
  }

  fun setDoubleClickListener(listener: (() -> Unit)?) {
    this.doubleClickHelper.doubleClickListener = listener
  }

  fun isBold(range: IntRange = selection) = this.text.hasSpans<StyleSpan>(range) {
    it.style == Typeface.BOLD || it.style == Typeface.BOLD_ITALIC
  }

  fun toggleBold(range: IntRange = selection, createNew: Boolean = true) {
    val bold = isBold(range)
    this.text.removeSpans<StyleSpan, IrcBoldSpan>(range) { span ->
      when {
        span is IrcBoldSpan         -> span
        span.style == Typeface.BOLD -> IrcBoldSpan()
        else                        -> null
      }
    }

    if (!bold && createNew) {
      this.text.setSpan(
        IrcBoldSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
    selectedFormattingChanged()
  }

  fun isItalic(range: IntRange = selection) = this.text.hasSpans<StyleSpan>(range) {
    it.style == Typeface.ITALIC || it.style == Typeface.BOLD_ITALIC
  }

  fun toggleItalic(range: IntRange = selection, createNew: Boolean = true) {
    val italic = isItalic(range)

    this.text.removeSpans<StyleSpan, IrcItalicSpan>(range) { span ->
      when {
        span is IrcItalicSpan         -> span
        span.style == Typeface.ITALIC -> IrcItalicSpan()
        else                          -> null
      }
    }

    if (!italic && createNew) {
      this.text.setSpan(
        IrcItalicSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
    selectedFormattingChanged()
  }

  fun isUnderline(range: IntRange = selection) = this.text.hasSpans<UnderlineSpan>(range)

  fun toggleUnderline(range: IntRange = selection, createNew: Boolean = true) {
    val underline = isUnderline(range)

    this.text.removeSpans<UnderlineSpan, IrcUnderlineSpan>(range) { span ->
      when (span) {
        is IrcUnderlineSpan -> span
        else                -> IrcUnderlineSpan()
      }
    }

    if (!underline && createNew) {
      this.text.setSpan(
        IrcUnderlineSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
    selectedFormattingChanged()
  }

  fun isStrikethrough(range: IntRange = selection) = this.text.hasSpans<StrikethroughSpan>(range)

  fun toggleStrikethrough(range: IntRange = selection, createNew: Boolean = true) {
    val strikethrough = isStrikethrough(range)

    this.text.removeSpans<StrikethroughSpan, IrcStrikethroughSpan>(range) { span ->
      when (span) {
        is IrcStrikethroughSpan -> span
        else                    -> IrcStrikethroughSpan()
      }
    }

    if (!strikethrough && createNew) {
      this.text.setSpan(
        IrcStrikethroughSpan(), range.start, range.endInclusive + 1,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
    selectedFormattingChanged()
  }

  fun isMonospace(range: IntRange = selection) = this.text.hasSpans<TypefaceSpan>(range) {
    it.family == "monospace"
  }

  fun toggleMonospace(range: IntRange = selection, createNew: Boolean = true) {
    val monospace = isMonospace(range)

    this.text.removeSpans<TypefaceSpan, IrcMonospaceSpan>(range) { span ->
      when {
        span is IrcMonospaceSpan   -> span
        span.family == "monospace" -> IrcMonospaceSpan()
        else                       -> null
      }
    }

    if (!monospace && createNew) {
      this.text.setSpan(
        IrcMonospaceSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
    selectedFormattingChanged()
  }

  fun foregroundColors(range: IntRange = selection) = this.text.spans<ForegroundColorSpan>(range)
  fun foregroundColor(range: IntRange = selection) =
    foregroundColors(range).singleOrNull()?.foregroundColor

  fun toggleForeground(range: IntRange = selection, @ColorInt color: Int? = null,
                       mircColor: Int? = null) {
    this.text.removeSpans<ForegroundColorSpan, IrcForegroundColorSpan<*>>(range) { span ->
      val mirc = mircColorMap[span.foregroundColor]
      when {
        span is IrcForegroundColorSpan<*> -> span
        mirc != null                      -> IrcForegroundColorSpan.MIRC(mirc, span.foregroundColor)
        else                              -> IrcForegroundColorSpan.HEX(span.foregroundColor)
      }
    }

    if (color != null) {
      if (mircColor != null) {
        this.text.setSpan(
          IrcForegroundColorSpan.MIRC(mircColor, color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      } else {
        this.text.setSpan(
          IrcForegroundColorSpan.HEX(color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      }
    }
    selectedFormattingChanged()
  }

  fun backgroundColors(range: IntRange = selection) = this.text.spans<BackgroundColorSpan>(range)
  fun backgroundColor(range: IntRange = selection) =
    backgroundColors(range).singleOrNull()?.backgroundColor

  fun toggleBackground(range: IntRange = selection, @ColorInt color: Int? = null,
                       mircColor: Int? = null) {
    this.text.removeSpans<BackgroundColorSpan, IrcBackgroundColorSpan<*>>(range) { span ->
      val mirc = mircColorMap[span.backgroundColor]
      when {
        span is IrcBackgroundColorSpan<*> -> span
        mirc != null                      -> IrcBackgroundColorSpan.MIRC(mirc, span.backgroundColor)
        else                              -> IrcBackgroundColorSpan.HEX(span.backgroundColor)
      }
    }

    if (color != null) {
      if (mircColor != null) {
        this.text.setSpan(
          IrcBackgroundColorSpan.MIRC(mircColor, color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      } else {
        this.text.setSpan(
          IrcBackgroundColorSpan.HEX(color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      }
    }
    selectedFormattingChanged()
  }

  fun clearFormatting(range: IntRange = selection) {
    toggleBold(range, false)
    toggleItalic(range, false)
    toggleUnderline(range, false)
    toggleStrikethrough(range, false)
    toggleMonospace(range, false)
    toggleForeground(range, null, null)
    toggleBackground(range, null, null)
  }

  fun setMultiLine(enabled: Boolean) {
    val selectionStart = selectionStart
    val selectionEnd = selectionEnd

    inputType = if (enabled) {
      inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE.inv()
    } else {
      inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    }

    setSelection(selectionStart, selectionEnd)
  }

  fun autoComplete(text: CharSequence) {
    val range = this.text.lastWordIndices(this.selection.start, true)
    val replacement = if (range?.start == 0) {
      "$text: "
    } else {
      "$text "
    }

    if (range != null) {
      this.text.replace(range.start, range.endInclusive + 1, replacement)
      this.setSelection(range.start + replacement.length)
    } else {
      this.text.append(replacement)
      this.setSelection(this.text.length)
    }
  }

  fun autoComplete(item: AutoCompletionState) {
    val suffix = if (item.range.start == 0) ": " else " "
    val replacement = "${item.completion.name}$suffix"
    val previousReplacement = item.lastCompletion?.let { "${item.lastCompletion.name}$suffix" }

    if (previousReplacement != null &&
        this.text.length >= item.range.start + previousReplacement.length &&
        this.text.substring(
          item.range.start, item.range.start + previousReplacement.length
        ) == previousReplacement) {
      this.text.replace(
        item.range.start, item.range.start + previousReplacement.length, replacement
      )
      this.setSelection(item.range.start + replacement.length)
    } else {
      this.text.replace(item.range.start, item.range.endInclusive + 1, replacement)
      this.setSelection(item.range.start + replacement.length)
    }
  }

  fun replace(text: CharSequence?) {
    this.setText(text)
    this.setSelection(this.text.length)
  }

  private fun selectedFormattingChanged(range: IntRange = selection) {
    formattingListener?.invoke(
      isBold(range),
      isItalic(range),
      isUnderline(range),
      isStrikethrough(range),
      isMonospace(range),
      foregroundColor(range),
      backgroundColor(range)
    )
  }
}
