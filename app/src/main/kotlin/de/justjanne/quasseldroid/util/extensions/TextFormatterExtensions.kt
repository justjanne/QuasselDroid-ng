package de.justjanne.quasseldroid.util.extensions

import android.text.Spanned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.buildSpannedString
import de.justjanne.quasseldroid.util.AnnotatedStringAppender
import de.justjanne.quasseldroid.util.format.TextFormatter
import java.util.*

fun TextFormatter.format(
  template: AnnotatedString,
  vararg args: Any?,
  locale: Locale = Locale.getDefault()
): AnnotatedString = buildAnnotatedString {
  formatBlocks(AnnotatedStringAppender(this), parseBlocks(template), args, locale)
}

fun TextFormatter.format(
  template: Spanned,
  vararg args: Any?,
  locale: Locale = Locale.getDefault()
): Spanned = buildSpannedString {
  formatBlocks(this, parseBlocks(template), args, locale)
}
