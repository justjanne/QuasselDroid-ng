package de.kuschku.quasseldroid.ui.chat.input

import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.widget.EditText
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.helper.lastWordIndices
import de.kuschku.quasseldroid.util.helper.lineSequence
import de.kuschku.quasseldroid.util.helper.selection
import de.kuschku.quasseldroid.util.helper.without
import de.kuschku.quasseldroid.util.irc.format.IrcFormatSerializer
import de.kuschku.quasseldroid.util.irc.format.spans.*

class FormatHandler(
  private val editText: EditText
) {
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

  fun clearFormatting(range: IntRange) {
    toggleBold(range, false)
    toggleItalic(range, false)
    toggleUnderline(range, false)
    toggleStrikethrough(range, false)
    toggleMonospace(range, false)
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
