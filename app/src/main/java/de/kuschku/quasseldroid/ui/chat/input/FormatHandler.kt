package de.kuschku.quasseldroid.ui.chat.input

import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.*
import android.widget.EditText
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.IrcFormatSerializer
import de.kuschku.quasseldroid.util.irc.format.spans.*

class FormatHandler(
  private val editText: EditText
) {
  val mircColors = editText.context.theme.styledAttributes(
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
    (0..98).map { getColor(it, 0) }
  }
  val mircColorMap = mircColors.withIndex().map { (key, value) -> key to value }.toMap()

  val defaultForegroundColor = editText.context.theme.styledAttributes(R.attr.colorForeground) {
    getColor(0, 0)
  }

  val defaultBackgroundColor = editText.context.theme.styledAttributes(R.attr.colorBackground) {
    getColor(0, 0)
  }

  private val serializer = IrcFormatSerializer(editText.context)
  val formattedText: Sequence<String>
    get() = editText.text.lineSequence().map { serializer.toEscapeCodes(SpannableString(it)) }
  val rawText: CharSequence
    get() = editText.text
  val strippedText: CharSequence
    get() = editText.text.let {
      val text = SpannableString(it)
      val toRemove = mutableListOf<Any>()
      for (span in text.getSpans(0, text.length, Any::class.java)) {
        if ((text.getSpanFlags(span) and Spanned.SPAN_COMPOSING) != 0) {
          toRemove.add(span)
        }
      }
      for (span in toRemove) {
        text.removeSpan(span)
      }
      text
    }

  fun isBold(range: IntRange) = editText.text.hasSpans<StyleSpan>(range) {
    it.style == Typeface.BOLD || it.style == Typeface.BOLD_ITALIC
  }

  fun toggleBold(range: IntRange, createNew: Boolean = true) {
    val bold = isBold(range)
    editText.text.removeSpans<StyleSpan, IrcBoldSpan>(range) { span ->
      when {
        span is IrcBoldSpan         -> span
        span.style == Typeface.BOLD -> IrcBoldSpan()
        else                        -> null
      }
    }

    if (!bold && createNew) {
      editText.text.setSpan(
        IrcBoldSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
  }

  fun isItalic(range: IntRange) = editText.text.hasSpans<StyleSpan>(range) {
    it.style == Typeface.ITALIC || it.style == Typeface.BOLD_ITALIC
  }

  fun toggleItalic(range: IntRange, createNew: Boolean = true) {
    val italic = isItalic(range)

    editText.text.removeSpans<StyleSpan, IrcItalicSpan>(range) { span ->
      when {
        span is IrcItalicSpan         -> span
        span.style == Typeface.ITALIC -> IrcItalicSpan()
        else                          -> null
      }
    }

    if (!italic && createNew) {
      editText.text.setSpan(
        IrcItalicSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
  }

  fun isUnderline(range: IntRange) = editText.text.hasSpans<UnderlineSpan>(range)

  fun toggleUnderline(range: IntRange, createNew: Boolean = true) {
    val underline = isUnderline(range)

    editText.text.removeSpans<UnderlineSpan, IrcUnderlineSpan>(range) { span ->
      when (span) {
        is IrcUnderlineSpan -> span
        else                -> IrcUnderlineSpan()
      }
    }

    if (!underline && createNew) {
      editText.text.setSpan(
        IrcUnderlineSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
  }

  fun isStrikethrough(range: IntRange) = editText.text.hasSpans<StrikethroughSpan>(range)

  fun toggleStrikethrough(range: IntRange, createNew: Boolean = true) {
    val strikethrough = isStrikethrough(range)

    editText.text.removeSpans<StrikethroughSpan, IrcStrikethroughSpan>(range) { span ->
      when (span) {
        is IrcStrikethroughSpan -> span
        else                    -> IrcStrikethroughSpan()
      }
    }

    if (!strikethrough && createNew) {
      editText.text.setSpan(
        IrcStrikethroughSpan(), range.start, range.endInclusive + 1,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
  }

  fun isMonospace(range: IntRange) = editText.text.hasSpans<TypefaceSpan>(range) {
    it.family == "monospace"
  }

  fun toggleMonospace(range: IntRange, createNew: Boolean = true) {
    val monospace = isMonospace(range)

    editText.text.removeSpans<TypefaceSpan, IrcMonospaceSpan>(range) { span ->
      when {
        span is IrcMonospaceSpan   -> span
        span.family == "monospace" -> IrcMonospaceSpan()
        else                       -> null
      }
    }

    if (!monospace && createNew) {
      editText.text.setSpan(
        IrcMonospaceSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
      )
    }
  }

  fun foregroundColors(range: IntRange) = editText.text.spans<ForegroundColorSpan>(range)
  fun foregroundColor(range: IntRange) = foregroundColors(range).singleOrNull()?.foregroundColor
  fun toggleForeground(range: IntRange, @ColorInt color: Int? = null, mircColor: Int? = null) {
    editText.text.removeSpans<ForegroundColorSpan, IrcForegroundColorSpan<*>>(range) { span ->
      val mirc = mircColorMap[span.foregroundColor]
      when {
        span is IrcForegroundColorSpan<*> -> span
        mirc != null                      -> IrcForegroundColorSpan.MIRC(mirc, span.foregroundColor)
        else                              -> IrcForegroundColorSpan.HEX(span.foregroundColor)
      }
    }

    if (color != null) {
      if (mircColor != null) {
        editText.text.setSpan(
          IrcForegroundColorSpan.MIRC(mircColor, color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      } else {
        editText.text.setSpan(
          IrcForegroundColorSpan.HEX(color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      }
    }
  }

  fun backgroundColors(range: IntRange) = editText.text.spans<BackgroundColorSpan>(range)
  fun backgroundColor(range: IntRange) = backgroundColors(range).singleOrNull()?.backgroundColor
  fun toggleBackground(range: IntRange, @ColorInt color: Int? = null, mircColor: Int? = null) {
    editText.text.removeSpans<BackgroundColorSpan, IrcBackgroundColorSpan<*>>(range) { span ->
      val mirc = mircColorMap[span.backgroundColor]
      when {
        span is IrcBackgroundColorSpan<*> -> span
        mirc != null                      -> IrcBackgroundColorSpan.MIRC(mirc, span.backgroundColor)
        else                              -> IrcBackgroundColorSpan.HEX(span.backgroundColor)
      }
    }

    if (color != null) {
      if (mircColor != null) {
        editText.text.setSpan(
          IrcBackgroundColorSpan.MIRC(mircColor, color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      } else {
        editText.text.setSpan(
          IrcBackgroundColorSpan.HEX(color),
          range.start,
          range.last + 1,
          Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
      }
    }
  }

  fun clearFormatting(range: IntRange) {
    toggleBold(range, false)
    toggleItalic(range, false)
    toggleUnderline(range, false)
    toggleStrikethrough(range, false)
    toggleMonospace(range, false)
    toggleForeground(range, null, null)
    toggleBackground(range, null, null)
  }

  private inline fun <reified U> Spanned.spans(range: IntRange) =
    getSpans(range.start, range.endInclusive + 1, U::class.java).filter {
      getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
      (getSpanEnd(it) != range.start ||
       getSpanFlags(it) and 0x02 != 0)
    }

  private inline fun <reified U> Spanned.spans(range: IntRange, f: (U) -> Boolean) =
    getSpans(range.start, range.last + 1, U::class.java).filter {
      f(it) &&
      getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
      (getSpanEnd(it) != range.start ||
       getSpanFlags(it) and 0x02 != 0)
    }

  private inline fun <reified U> Spanned.hasSpans(range: IntRange) =
    getSpans(range.start, range.endInclusive + 1, U::class.java).any {
      getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
      (getSpanEnd(it) != range.start ||
       getSpanFlags(it) and 0x02 != 0)
    }

  private inline fun <reified U> Spanned.hasSpans(range: IntRange, f: (U) -> Boolean) =
    getSpans(range.start, range.last + 1, U::class.java).any {
      f(it) &&
      getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
      (getSpanEnd(it) != range.start ||
       getSpanFlags(it) and 0x02 != 0)
    }

  private inline fun <reified U, T> Editable.removeSpans(
    range: IntRange, removeInvalid: Boolean = false, f: (U) -> T?
  ) where T : Copyable<T> {

    for (raw in getSpans<U>(range.start, range.endInclusive + 1, U::class.java)) {
      val spanFlags = getSpanFlags(raw)
      if (spanFlags and Spanned.SPAN_COMPOSING != 0) continue

      val spanEnd = getSpanEnd(raw)
      val spanStart = getSpanStart(raw)

      val span = f(raw)
      if (span == null) {
        if (removeInvalid)
          removeSpan(raw)
      } else {
        removeSpan(raw)

        for (spanRange in spanStart until spanEnd without range) {
          setSpan(
            span.copy(),
            spanRange.start,
            spanRange.endInclusive + 1,
            (spanFlags and 0x03.inv()) or 0x01
          )
        }
      }
    }
  }

  fun autoComplete(text: CharSequence) {
    val range = editText.text.lastWordIndices(editText.selection.start, true)
    val replacement = if (range?.start == 0) {
      "$text: "
    } else {
      "$text "
    }

    if (range != null) {
      editText.text.replace(range.start, range.endInclusive + 1, replacement)
      editText.setSelection(range.start + replacement.length)
    } else {
      editText.text.append(replacement)
      editText.setSelection(editText.text.length)
    }
  }

  fun autoComplete(item: ChatActivity.AutoCompletionState) {
    val suffix = if (item.range.start == 0) ": " else " "
    val replacement = "${item.completion.name}$suffix"
    val previousReplacement = item.lastCompletion?.let { "${item.lastCompletion.name}$suffix" }

    if (previousReplacement != null &&
        editText.text.length >= item.range.start + previousReplacement.length &&
        editText.text.substring(
          item.range.start, item.range.start + previousReplacement.length
        ) == previousReplacement) {
      editText.text.replace(
        item.range.start, item.range.start + previousReplacement.length, replacement
      )
      editText.setSelection(item.range.start + replacement.length)
    } else {
      editText.text.replace(item.range.start, item.range.endInclusive + 1, replacement)
      editText.setSelection(item.range.start + replacement.length)
    }
  }

  fun replace(text: CharSequence?) {
    editText.setText(text)
    editText.setSelection(editText.text.length)
  }
}
