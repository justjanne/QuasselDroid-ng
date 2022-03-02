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

package de.justjanne.quasseldroid.util.format

import android.text.Spanned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.buildSpannedString
import de.justjanne.quasseldroid.util.AnnotatedStringAppender
import de.justjanne.quasseldroid.util.extensions.component6
import de.justjanne.quasseldroid.util.extensions.component7
import java.util.*
import java.util.regex.Pattern

/**
 * Provides [String.format] style functions that work with [Spanned] strings and preserve formatting.
 *
 * @author George T. Steel
 */
object TextFormatter {
  fun format(
    template: AnnotatedString,
    vararg args: Any?,
    locale: Locale = Locale.getDefault()
  ): AnnotatedString = buildAnnotatedString {
    formatBlocks(AnnotatedStringAppender(this), parseBlocks(template), args, locale)
  }

  fun format(
    template: Spanned,
    vararg args: Any?,
    locale: Locale = Locale.getDefault()
  ): Spanned = buildSpannedString {
    formatBlocks(this, parseBlocks(template), args, locale)
  }

  fun format(
    template: String,
    vararg args: Any?,
    locale: Locale = Locale.getDefault()
  ): String = buildString {
    formatBlocks(this, parseBlocks(template), args, locale)
  }

  internal fun formatBlocks(
    target: Appendable,
    blocks: Sequence<FormatString>,
    args: Array<out Any?>,
    locale: Locale
  ) {
    var argIndex = 0
    for (block in blocks) {
      when (block) {
        is FormatString.FixedValue -> target.append(block.content)
        is FormatString.FormatSpecifier -> {
          val arg = when {
              block.argumentIndex != null -> args[block.argumentIndex - 1]
              block.flags.orEmpty().contains(FLAG_REUSE_ARGUMENT) -> args[argIndex]
              else -> args[argIndex++]
          }

          if (block.conversion.lowercaseChar() == TYPE_STRING) {
            if (arg == null) {
              target.append(null)
            }

            fun justify(data: CharSequence): CharSequence = when {
              block.width == null -> data
              block.flags.orEmpty().contains(FLAG_JUSTIFY_LEFT) -> data.padEnd(block.width)
              else -> data.padStart(block.width)
            }

            fun uppercase(data: String): String = when {
              block.flags.orEmpty().contains(FLAG_UPPERCASE) -> data.uppercase(locale)
              else -> data
            }

            when (arg) {
              null -> target.append(justify(uppercase("null")))
              is String -> target.append(justify(uppercase(arg)))
              is CharSequence -> target.append(justify(arg))
              else -> target.append(justify(uppercase(arg.toString())))
            }
          } else {
            target.append(
              String.format(
                locale,
                block.toFormatSpecifier(ignoreFlags = setOf(FLAG_REUSE_ARGUMENT)),
                arg
              )
            )
          }
        }
      }
    }
  }

  internal fun parseBlocks(template: CharSequence) = sequence {
    var index = 0
    while (index < template.length) {
      val match = FORMAT_SEQUENCE.toRegex().find(template, index)
      if (match == null) {
        yield(FormatString.FixedValue(template.subSequence(index, template.length)))
        break
      }
      if (match.range.first != index) {
        yield(FormatString.FixedValue(template.subSequence(index, match.range.first)))
      }
      index = match.range.last + 1

      val groupValues = match.groupValues
      require(groupValues.size == 7) {
        "Invalid match '$match', should return 6 groups, returned ${groupValues.size}"
      }
      val (_, argumentIndex, flags, width, precision, time, conversion) = groupValues
      require(conversion.length == 1) {
        "Invalid format string '$match', conversion too long"
      }

      yield(
        FormatString.FormatSpecifier(
          argumentIndex = argumentIndex.takeIf(String::isNotEmpty)?.toIntOrNull(),
          flags = flags.takeIf(String::isNotEmpty),
          width = width.takeIf(String::isNotEmpty)?.toIntOrNull(),
          precision = precision.takeIf(String::isNotEmpty)?.toIntOrNull(),
          time = time.takeIf(String::isNotEmpty) != null,
          conversion = conversion.first()
        )
      )
    }
  }

  private const val TYPE_STRING = 's'
  private const val FLAG_JUSTIFY_LEFT = '-'
  private const val FLAG_UPPERCASE = '^'
  private const val FLAG_REUSE_ARGUMENT = '<'

  private val FORMAT_SEQUENCE =
    Pattern.compile("%(?:([0-9]+)\\\$)?([,\\-(+# 0<]*)([0-9]*)(?:\\.([0-9]*))?([tT]?)([a-zA-Z])")
}
