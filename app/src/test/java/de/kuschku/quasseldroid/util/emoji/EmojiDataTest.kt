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
import android.text.Editable
import android.text.SpannableStringBuilder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.kuschku.quasseldroid.QuasseldroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.Reader

inline fun <reified T> Gson.fromJson(reader: Reader): T =
  if (T::class.java.typeParameters.isEmpty()) {
    this.fromJson(reader, T::class.java)
  } else {
    val type = object : TypeToken<T>() {}.type
    this.fromJson(reader, type)
  }

fun EmojiHandler.replaceShortcodes(source: String): String =
  this.replaceShortcodes(SpannableStringBuilder(source)).toString()

@Config(application = QuasseldroidTest::class, sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class EmojiDataTest {
  object TestEmojiProvider : EmojiProvider {
    private val gson: Gson = GsonBuilder().create()
    override val emoji: List<EmojiHandler.Emoji> = gson.fromJson(
      TestEmojiProvider::class.java.getResourceAsStream("/emoji.json")!!.reader(Charsets.UTF_8)
    )
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
