/*
 * Copyright © 2014 George T. Steel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kuschku.quasseldroid.util.ui

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import java.util.*
import java.util.regex.Pattern

/**
 * Provides [String.format] style functions that work with [Spanned] strings and preserve formatting.
 *
 * @author George T. Steel
 */
object SpanFormatter {
  private val FORMAT_SEQUENCE = Pattern.compile("%([0-9]+\\$|<?)([^a-zA-z%]*)([[a-zA-Z%]&&[^tT]]|[tT][a-zA-Z])")

  /**
   * Version of [String.format] that works on [Spanned] strings to preserve rich text formatting.
   * Both the `format` as well as any `%s args` can be Spanned and will have their formatting preserved.
   * Due to the way [Spannable]s work, any argument's spans will can only be included **once** in the result.
   * Any duplicates will appear as text only.
   *
   * @param locale the locale to apply; `null` value means no localization.
   * @param format the format string (see [java.util.Formatter.format])
   * @param args   the list of arguments passed to the formatter.
   * @return the formatted string (with spans).
   * @see String.format
   */
  fun format(format: CharSequence, vararg args: Any?,
             locale: Locale = Locale.getDefault()): SpannedString {
    val out = SpannableStringBuilder(format)

    var i = 0
    var argAt = -1

    while (i < out.length) {
      val m = FORMAT_SEQUENCE.matcher(out)
      if (!m.find(i)) break
      i = m.start()
      val exprEnd = m.end()

      val argTerm = m.group(1)
      val modTerm = m.group(2)
      val typeTerm = m.group(3)

      val cookedArg: CharSequence

      if (typeTerm == "%") {
        cookedArg = "%"
      } else {
        val argIdx = when (argTerm) {
          ""   -> ++argAt
          "<"  -> argAt
          else -> Integer.parseInt(argTerm.substring(0, argTerm.length - 1)) - 1
        }

        val argItem = args[argIdx]

        cookedArg = if (typeTerm == "s" && argItem is Spanned) {
          argItem
        } else {
          String.format(locale, "%$modTerm$typeTerm", argItem)
        }
      }

      out.replace(i, exprEnd, cookedArg)
      i += cookedArg.length
    }

    return SpannedString(out)
  }
}
