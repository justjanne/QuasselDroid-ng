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

package de.kuschku.quasseldroid.ui.chat.input

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import android.util.AttributeSet
import androidx.annotation.ColorInt
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.spans.*
import de.kuschku.quasseldroid.util.ui.DoubleClickHelper
import de.kuschku.quasseldroid.util.ui.EditTextSelectionChange

class RichEditText : EditTextSelectionChange {
  val safeText: Editable
    get() = this.text ?: SpannableStringBuilder("").also {
      this.text = it
    }

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

  fun isBold(range: IntRange = selection) = this.safeText.hasSpans<StyleSpan>(range) {
    it.style == Typeface.BOLD || it.style == Typeface.BOLD_ITALIC
  }

  fun toggleBold(range: IntRange = selection, createNew: Boolean = true) {
    val bold = isBold(range)
    this.safeText.removeSpans<StyleSpan, IrcBoldSpan>(range) { span ->
      when {
        span is IrcBoldSpan         -> span
        span.style == Typeface.BOLD -> IrcBoldSpan()
        else                        -> null
      }
    }

    if (!bold && createNew) {
      this.safeText.setSpan(
        IrcBoldSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
    selectedFormattingChanged()
  }

  fun isItalic(range: IntRange = selection) = this.safeText.hasSpans<StyleSpan>(range) {
    it.style == Typeface.ITALIC || it.style == Typeface.BOLD_ITALIC
  }

  fun toggleItalic(range: IntRange = selection, createNew: Boolean = true) {
    val italic = isItalic(range)

    this.safeText.removeSpans<StyleSpan, IrcItalicSpan>(range) { span ->
      when {
        span is IrcItalicSpan         -> span
        span.style == Typeface.ITALIC -> IrcItalicSpan()
        else                          -> null
      }
    }

    if (!italic && createNew) {
      this.safeText.setSpan(
        IrcItalicSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
    selectedFormattingChanged()
  }

  fun isUnderline(range: IntRange = selection) = this.safeText.hasSpans<UnderlineSpan>(range)

  fun toggleUnderline(range: IntRange = selection, createNew: Boolean = true) {
    val underline = isUnderline(range)

    this.safeText.removeSpans<UnderlineSpan, IrcUnderlineSpan>(range) { span ->
      when (span) {
        is IrcUnderlineSpan -> span
        else                -> IrcUnderlineSpan()
      }
    }

    if (!underline && createNew) {
      this.safeText.setSpan(
        IrcUnderlineSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
    selectedFormattingChanged()
  }

  fun isStrikethrough(
    range: IntRange = selection) = this.safeText.hasSpans<StrikethroughSpan>(range)

  fun toggleStrikethrough(range: IntRange = selection, createNew: Boolean = true) {
    val strikethrough = isStrikethrough(range)

    this.safeText.removeSpans<StrikethroughSpan, IrcStrikethroughSpan>(range) { span ->
      when (span) {
        is IrcStrikethroughSpan -> span
        else                    -> IrcStrikethroughSpan()
      }
    }

    if (!strikethrough && createNew) {
      this.safeText.setSpan(
        IrcStrikethroughSpan(), range.start, range.endInclusive + 1,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
    selectedFormattingChanged()
  }

  fun isMonospace(range: IntRange = selection) = this.safeText.hasSpans<TypefaceSpan>(range) {
    it.family == "monospace"
  }

  fun toggleMonospace(range: IntRange = selection, createNew: Boolean = true) {
    val monospace = isMonospace(range)

    this.safeText.removeSpans<TypefaceSpan, IrcMonospaceSpan>(range) { span ->
      when {
        span is IrcMonospaceSpan   -> span
        span.family == "monospace" -> IrcMonospaceSpan()
        else                       -> null
      }
    }

    if (!monospace && createNew) {
      this.safeText.setSpan(
        IrcMonospaceSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
    selectedFormattingChanged()
  }

  fun foregroundColors(
    range: IntRange = selection) = this.safeText.spans<ForegroundColorSpan>(range)

  fun foregroundColor(range: IntRange = selection) =
    foregroundColors(range).singleOrNull()?.foregroundColor

  fun toggleForeground(range: IntRange = selection, @ColorInt color: Int? = null,
                       mircColor: Int? = null) {
    this.safeText.removeSpans<ForegroundColorSpan, IrcForegroundColorSpan<*>>(range) { span ->
      val mirc = mircColorMap[span.foregroundColor]
      when {
        span is IrcForegroundColorSpan<*> -> span
        mirc != null                      -> IrcForegroundColorSpan.MIRC(mirc, span.foregroundColor)
        else                              -> IrcForegroundColorSpan.HEX(span.foregroundColor)
      }
    }

    if (color != null) {
      if (mircColor != null) {
        this.safeText.setSpan(
          IrcForegroundColorSpan.MIRC(mircColor, color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      } else {
        this.safeText.setSpan(
          IrcForegroundColorSpan.HEX(color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      }
    }
    selectedFormattingChanged()
  }

  fun backgroundColors(
    range: IntRange = selection) = this.safeText.spans<BackgroundColorSpan>(range)

  fun backgroundColor(range: IntRange = selection) =
    backgroundColors(range).singleOrNull()?.backgroundColor

  fun toggleBackground(range: IntRange = selection, @ColorInt color: Int? = null,
                       mircColor: Int? = null) {
    this.safeText.removeSpans<BackgroundColorSpan, IrcBackgroundColorSpan<*>>(range) { span ->
      val mirc = mircColorMap[span.backgroundColor]
      when {
        span is IrcBackgroundColorSpan<*> -> span
        mirc != null                      -> IrcBackgroundColorSpan.MIRC(mirc, span.backgroundColor)
        else                              -> IrcBackgroundColorSpan.HEX(span.backgroundColor)
      }
    }

    if (color != null) {
      if (mircColor != null) {
        this.safeText.setSpan(
          IrcBackgroundColorSpan.MIRC(mircColor, color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      } else {
        this.safeText.setSpan(
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
    val range = this.safeText.lastWordIndices(this.selection.start, true)
    val replacement = if (range?.start == 0) {
      "$text$suffix"
    } else {
      "$text "
    }

    if (range != null) {
      this.safeText.replace(range.start, range.endInclusive + 1, replacement)
      this.setSelection(range.start + replacement.length)
    } else {
      this.safeText.append(replacement)
      this.setSelection(this.safeText.length)
    }
  }

  fun autoComplete(item: AutoCompletionState) {
    val suffix = if (item.range.start == 0) item.completion.suffix else " "
    val replacement = "${item.completion.name}$suffix"
    val previousReplacement = item.lastCompletion?.let { "${item.lastCompletion.name}$suffix" }

    if (previousReplacement != null &&
        this.safeText.length >= item.range.start + previousReplacement.length &&
        this.safeText.substring(
          item.range.start, item.range.start + previousReplacement.length
        ) == previousReplacement) {
      this.safeText.replace(
        item.range.start, item.range.start + previousReplacement.length, replacement
      )
      this.setSelection(item.range.start + replacement.length)
    } else {
      this.safeText.replace(item.range.start, item.range.endInclusive + 1, replacement)
      this.setSelection(item.range.start + replacement.length)
    }
  }

  fun replaceText(text: CharSequence?) {
    this.setText(text)
    this.setSelection(this.safeText.length)
  }

  fun appendText(text: CharSequence?, suffix: String?) {
    val shouldAddSuffix = this.safeText.isEmpty()
    if (this.safeText.isNotEmpty() && !this.safeText.endsWith(" "))
      this.safeText.append(" ")
    this.safeText.append(text)
    if (shouldAddSuffix)
      this.safeText.append(suffix)
    this.setSelection(this.safeText.length)
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
