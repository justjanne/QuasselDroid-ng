package de.kuschku.quasseldroid_ng.ui.chat

import android.graphics.Typeface
import android.support.annotation.MenuRes
import android.text.Editable
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.widget.EditText
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.lastWordIndices
import de.kuschku.quasseldroid_ng.util.helper.selection
import de.kuschku.quasseldroid_ng.util.irc.format.IrcFormatSerializer
import de.kuschku.quasseldroid_ng.util.irc.format.spans.*

class InputEditor(private val editText: EditText) {
  private val serializer = IrcFormatSerializer(editText.context)
  val formattedString: String
    get() = serializer.toEscapeCodes(editText.text)

  @MenuRes
  val menu: Int = R.menu.editor

  fun toggleBold(range: IntRange, createNew: Boolean = true) {
    if (range.isEmpty())
      return

    val exists = editText.text.removeSpans<StyleSpan, IrcBoldSpan>(range) { span ->
      when {
        span is IrcBoldSpan         -> span
        span.style == Typeface.BOLD -> IrcBoldSpan()
        else                        -> null
      }
    }

    if (!exists && createNew) {
      editText.text.setSpan(
        IrcBoldSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
      )
    }
  }

  fun toggleItalic(range: IntRange, createNew: Boolean = true) {
    if (range.isEmpty())
      return

    val exists = editText.text.removeSpans<StyleSpan, IrcItalicSpan>(range) { span ->
      when {
        span is IrcItalicSpan         -> span
        span.style == Typeface.ITALIC -> IrcItalicSpan()
        else                          -> null
      }
    }

    if (!exists && createNew) {
      editText.text.setSpan(
        IrcItalicSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
      )
    }
  }

  fun toggleUnderline(range: IntRange, createNew: Boolean = true) {
    if (range.isEmpty())
      return

    val exists = editText.text.removeSpans<UnderlineSpan, IrcUnderlineSpan>(range) { span ->
      when {
        span is IrcUnderlineSpan -> span
        else                     -> IrcUnderlineSpan()
      }
    }

    if (!exists && createNew) {
      editText.text.setSpan(
        IrcUnderlineSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
      )
    }
  }

  fun toggleStrikethrough(range: IntRange, createNew: Boolean = true) {
    if (range.isEmpty())
      return

    val exists = editText.text.removeSpans<StrikethroughSpan, IrcStrikethroughSpan>(range) { span ->
      when {
        span is IrcStrikethroughSpan -> span
        else                         -> IrcStrikethroughSpan()
      }
    }

    if (!exists && createNew) {
      editText.text.setSpan(
        IrcStrikethroughSpan(), range.start, range.endInclusive + 1,
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
      )
    }
  }

  fun toggleMonospace(range: IntRange, createNew: Boolean = true) {
    if (range.isEmpty())
      return

    val exists = editText.text.removeSpans<TypefaceSpan, IrcMonospaceSpan>(range) { span ->
      when {
        span is IrcMonospaceSpan   -> span
        span.family == "monospace" -> IrcMonospaceSpan()
        else                       -> null
      }
    }

    if (!exists && createNew) {
      editText.text.setSpan(
        IrcMonospaceSpan(), range.start, range.endInclusive + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
      )
    }
  }

  fun clearFormatting(range: IntRange) {
    if (range.isEmpty())
      return

    toggleBold(range, false)
    toggleItalic(range, false)
    toggleUnderline(range, false)
    toggleStrikethrough(range, false)
    toggleMonospace(range, false)
  }

  fun onMenuItemClick(item: MenuItem?) = when (item?.itemId) {
    R.id.format_bold          -> {
      toggleBold(editText.selection)
      true
    }
    R.id.format_italic        -> {
      toggleItalic(editText.selection)
      true
    }
    R.id.format_underline     -> {
      toggleUnderline(editText.selection)
      true
    }
    R.id.format_strikethrough -> {
      toggleStrikethrough(editText.selection)
      true
    }
    R.id.format_monospace     -> {
      toggleMonospace(editText.selection)
      true
    }
    R.id.format_clear         -> {
      clearFormatting(editText.selection)
      true
    }
    else                      -> false
  }

  private inline fun <reified U, T> Editable.removeSpans(
    range: IntRange, removeInvalid: Boolean = false, f: (U) -> T?): Boolean where T : Copyable<T> {
    if (range.isEmpty())
      return false

    var removedAny = false

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

        val endIsIn = spanEnd in range
        val endIsAfter = spanEnd > range.endInclusive + 1

        val startIsIn = spanStart in range
        val startIsBefore = spanStart < range.start

        if (endIsIn && startIsIn) {
          removedAny = true
        } else if (endIsIn) {
          setSpan(span, spanStart, range.start, spanFlags)
          removedAny = true
        } else if (startIsIn) {
          setSpan(span, range.endInclusive + 1, spanEnd, spanFlags)
          removedAny = true
        } else if (startIsBefore && endIsAfter) {
          setSpan(span, spanStart, range.start, spanFlags)
          setSpan(span.copy(), range.endInclusive + 1, spanEnd, spanFlags)
          removedAny = true
        } else if (startIsBefore) {
          setSpan(span, spanStart, range.start, spanFlags)
          removedAny = true
        }
      }
    }

    return removedAny
  }

  fun autoComplete(text: CharSequence) {
    val range = editText.text.lastWordIndices(editText.selectionStart, true)
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

  fun share(text: CharSequence?) {
    editText.setText(text)
    editText.setSelection(editText.text.length)
  }
}