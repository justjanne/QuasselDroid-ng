package de.kuschku.quasseldroid.util.irc.format

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.URLSpan
import de.kuschku.quasseldroid.settings.MessageSettings
import org.intellij.lang.annotations.Language
import javax.inject.Inject

class ContentFormatter @Inject constructor(
  private val ircFormatDeserializer: IrcFormatDeserializer,
  private val messageSettings: MessageSettings
) {
  @Language("RegExp")
  private val scheme = "(?:(?:mailto:|magnet:|(?:[+.-]?\\w)+://)|www(?=\\.\\S+\\.))"
  @Language("RegExp")
  private val authority = "(?:(?:[,.;@:]?[-\\w]+)+\\.?|\\[[0-9a-f:.]+])?(?::\\d+)?"
  @Language("RegExp")
  private val urlChars = "(?:[,.;:]*[\\w~@/?&=+$()!%#*-])"
  @Language("RegExp")
  private val urlEnd = "((?:>|[,.;:\"]*\\s|\\b|$))"

  private val urlPattern = Regex(
    "\\b($scheme$authority(?:$urlChars*)?)$urlEnd",
    RegexOption.IGNORE_CASE
  )

  private val channelPattern = Regex(
    "((?:#|![A-Z0-9]{5})[^,:\\s]+(?::[^,:\\s]+)?)\\b",
    RegexOption.IGNORE_CASE
  )

  class QuasselURLSpan(text: String, private val highlight: Boolean) : URLSpan(text) {
    override fun updateDrawState(ds: TextPaint?) {
      if (ds != null) {
        if (!highlight)
          ds.color = ds.linkColor
        ds.isUnderlineText = true
      }
    }
  }

  fun format(context: Context, content: String, highlight: Boolean = false): CharSequence {
    val formattedText = ircFormatDeserializer.formatString(
      context, content, messageSettings.colorizeMirc
    )
    val text = SpannableString(formattedText)

    for (result in urlPattern.findAll(formattedText)) {
      val group = result.groups[1]
      if (group != null) {
        text.setSpan(
          QuasselURLSpan(group.value, highlight), group.range.start,
          group.range.start + group.value.length,
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
      }
    }
    /*
    for (result in channelPattern.findAll(content)) {
      text.setSpan(URLSpan(result.value), result.range.start, result.range.endInclusive, Spanned.SPAN_INCLUSIVE_INCLUSIVE)}
    */

    return text
  }
}
