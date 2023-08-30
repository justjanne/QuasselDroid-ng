/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

import android.os.Build
import android.text.SpannableStringBuilder
import de.kuschku.quasseldroid.QuasseldroidTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.DecodeSequenceMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeToSequence
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

fun EmojiHandler.replaceShortcodes(source: String): String =
  this.replaceShortcodes(SpannableStringBuilder(source)).toString()

@Config(application = QuasseldroidTest::class, sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class EmojiDataTest {
  @OptIn(ExperimentalSerializationApi::class)
  object TestEmojiProvider : EmojiProvider {
    override val emoji: List<EmojiHandler.Emoji> = Json.decodeToSequence<EmojiHandler.Emoji>(
      TestEmojiProvider::class.java.getResourceAsStream("/emoji.json"),
      DecodeSequenceMode.ARRAY_WRAPPED
    ).toList()
    override val shortcodes: Map<String, EmojiHandler.Emoji> = emoji.flatMap { entry ->
      entry.shortcodes.map { Pair(it, entry) }
    }.toMap()
  }

  private val handler = EmojiHandler(TestEmojiProvider)

  @Test
  fun findShortCodes() {
    assertTrue(EmojiHandler.shortcodeRegex.matches(":like:"))
    assertTrue(EmojiHandler.shortcodeRegex.matches(":+1:"))
    assertTrue(EmojiHandler.shortcodeRegex.matches(":beetle:"))
  }

  @Test
  fun replaceShortCodes() {
    assertEquals("\uD83D\uDC4D️",
      handler.replaceShortcodes(":like:")
    )
    assertEquals("\uD83D\uDC4D️",
      handler.replaceShortcodes(":+1:")
    )
    assertEquals("\uD83D\uDC1E\uD83D\uDC4D️",
      handler.replaceShortcodes(":ladybug::+1:")
    )
    assertEquals(
      "this\uD83D\uDC4D️isa\uD83D\uDC1E\uD83D\uDC4D️test",
      handler.replaceShortcodes("this:like:isa:ladybug::+1:test")
    )
  }
}
