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
  val mircColors = listOf(
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
      inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    } else {
      inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE.inv()
    }

    setSelection(selectionStart, selectionEnd)
  }

  fun autoComplete(text: CharSequence, suffix: String) {
    val range = this.text.lastWordIndices(this.selection.start, true)
    val replacement = if (range?.start == 0) {
      "$text$suffix"
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
    val suffix = if (item.range.start == 0) item.completion.suffix else " "
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

  fun replaceText(text: CharSequence?) {
    this.setText(text)
    this.setSelection(this.text.length)
  }

  fun appendText(text: CharSequence?, suffix: String?) {
    val shouldAddSuffix = this.text.isEmpty()
    if (!this.text.endsWith(" "))
      this.text.append(" ")
    this.text.append(text)
    if (shouldAddSuffix)
      this.text.append(suffix)
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
