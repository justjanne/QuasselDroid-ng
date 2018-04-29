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

package de.kuschku.quasseldroid.util.irc.format

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

  fun format(content: String, highlight: Boolean = false): CharSequence {
    val formattedText = ircFormatDeserializer.formatString(content, messageSettings.colorizeMirc)
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
