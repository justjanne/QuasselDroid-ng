/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2023 Janne Mareike Koschinski
 * Copyright (c) 2023 The Quassel Project
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

package de.kuschku.quasseldroid.util.emoji

import android.text.Editable
import java.util.*
import javax.inject.Inject

class EmojiHandler @Inject constructor(private val emojiProvider: EmojiProvider) {
  data class Emoji(
    val label: String,
    val tags: List<String>,
    val shortcodes: List<String>,
    val replacement: String
  )

  fun replaceShortcodes(source: Editable): Editable {
    var result = source

    val matches = shortcodeRegex.findAll(source)
      .map(MatchResult::value)
      .distinct()
      .toList()

    for (match in matches) {
      val emoji = shortcodeLookup(normalizeShortcode(match))
      if (emoji != null) {
        println("replacing $match with ${emoji.replacement}")
        println("before: $result")
        result = result.replaceAll(match, emoji.replacement)
        println("after: $result")
      }
    }

    return result
  }

  fun normalizeShortcode(pattern: String): String =
    pattern.lowercase(Locale.ROOT).trim(':')

  fun fuzzyLookup(pattern: String): List<Emoji> = emojiProvider.emoji.filter { emoji ->
    emoji.shortcodes.any { it.contains(pattern) }
      || emoji.tags.any { it.contains(pattern) }
      || emoji.label.contains(pattern)
  }

  fun shortcodeLookup(pattern: String): Emoji? = emojiProvider.shortcodes[pattern]

  companion object {
    val shortcodeRegex = Regex(":[\\d+_a-zA-Z-]+:")

    private fun Editable.replaceAll(original: String, replacement: String): Editable {
      var result = this
      var index: Int
      while (true) {
        index = result.indexOf(original)
        if (index == -1) break

        result = result.replace(index, index + original.length, replacement)
      }
      return result
    }
  }
}
