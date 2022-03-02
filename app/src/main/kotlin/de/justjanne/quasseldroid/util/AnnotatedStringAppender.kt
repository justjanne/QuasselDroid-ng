package de.justjanne.quasseldroid.util

import android.text.SpannableStringBuilder
import androidx.compose.ui.text.AnnotatedString
import java.lang.IllegalArgumentException

class AnnotatedStringAppender(
  private val builder: AnnotatedString.Builder
) : Appendable {
  override fun append(text: CharSequence): Appendable = this.apply {
    when (text) {
      is String -> builder.append(text)
      is AnnotatedString -> builder.append(text)
      else -> throw IllegalArgumentException(
          "Unsupported type of text for annotated string: ${text.javaClass.canonicalName}"
      )
    }
  }

  override fun append(text: CharSequence, start: Int, end: Int): Appendable = this.apply {
    when (text) {
      is String -> builder.append(text.substring(start, end))
      is AnnotatedString -> builder.append(text.subSequence(start, end))
      else -> throw IllegalArgumentException(
          "Unsupported type of text for annotated string: ${text.javaClass.canonicalName}"
      )
    }
  }

  override fun append(text: Char): Appendable = this.apply {
    builder.append(text)
  }
}

class SpannableStringAppender(
  private val builder: SpannableStringBuilder
) : Appendable {
  override fun append(text: CharSequence): Appendable = this.apply {
    when (text) {
      is String -> builder.append(text)
      is AnnotatedString -> builder.append(text)
      else -> throw IllegalArgumentException(
          "Unsupported type of text for annotated string: ${text.javaClass.canonicalName}"
      )
    }
  }

  override fun append(text: CharSequence, start: Int, end: Int): Appendable = this.apply {
    when (text) {
      is String -> builder.append(text.substring(start, end))
      is AnnotatedString -> builder.append(text.subSequence(start, end))
      else -> throw IllegalArgumentException(
          "Unsupported type of text for annotated string: ${text.javaClass.canonicalName}"
      )
    }
  }

  override fun append(text: Char): Appendable = this.apply {
    builder.append(text)
  }
}
