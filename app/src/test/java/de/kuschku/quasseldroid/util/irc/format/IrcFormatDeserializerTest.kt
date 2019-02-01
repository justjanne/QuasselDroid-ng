/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.quasseldroid.util.irc.format

import android.text.Spanned
import android.text.SpannedString
import de.kuschku.quasseldroid.QuasseldroidTest
import de.kuschku.quasseldroid.util.irc.format.spans.IrcBackgroundColorSpan
import de.kuschku.quasseldroid.util.irc.format.spans.IrcForegroundColorSpan
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = QuasseldroidTest::class)
@RunWith(RobolectricTestRunner::class)
class IrcFormatDeserializerTest {
  lateinit var deserializer: IrcFormatDeserializer

  @Before
  fun setUp() {
    deserializer = IrcFormatDeserializer(mircColors = colors)
  }

  @Test
  fun testMissingEndTag() {
    val text = SpannedString.valueOf(deserializer.formatString(
      "\u000301,01weeeeeeeeee",
      colorize = true
    ))
    assertEquals(
      listOf(
        SpanInfo(
          from = 0,
          to = 11,
          flags = Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
          span = IrcForegroundColorSpan.MIRC(mircColor = 1, color = colors[1])
        ),
        SpanInfo(
          from = 0,
          to = 11,
          flags = Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
          span = IrcBackgroundColorSpan.MIRC(mircColor = 1, color = colors[1])
        )
      ),
      text.allSpans<Any>()
    )
  }

  inline fun <reified T> Spanned.allSpans(): List<SpanInfo> =
    getSpans(0, length, T::class.java).map {
      SpanInfo(
        from = getSpanStart(it),
        to = getSpanEnd(it),
        flags = getSpanFlags(it),
        span = it
      )
    }

  data class SpanInfo(
    val from: Int,
    val to: Int,
    val flags: Int,
    val span: Any?
  )

  companion object {
    val colors = intArrayOf(
      0x00ffffff,
      0x00000000,
      0x00000080,
      0x00008000,
      0x00ff0000,
      0x00800000,
      0x00800080,
      0x00ffa500,
      0x00ffff00,
      0x0000ff00,
      0x00008080,
      0x0000ffff,
      0x004169e1,
      0x00ff00ff,
      0x00808080,
      0x00c0c0c0,

      0x00470000,
      0x00740000,
      0x00b50000,
      0x00ff0000,
      0x00ff5959,
      0x00ff9c9c,

      0x00472100,
      0x00743a00,
      0x00b56300,
      0x00ff8c00,
      0x00ffb459,
      0x00ffd39c,

      0x00474700,
      0x00747400,
      0x00b5b500,
      0x00ffff00,
      0x00ffff71,
      0x00ffff9c,

      0x00324700,
      0x00517400,
      0x007db500,
      0x00b2ff00,
      0x00cfff60,
      0x00e2ff9c,

      0x00004700,
      0x00007400,
      0x0000b500,
      0x0000ff00,
      0x006fff6f,
      0x009cff9c,

      0x0000472c,
      0x00007449,
      0x0000b571,
      0x0000ffa0,
      0x0065ffc9,
      0x009cffdb,

      0x00004747,
      0x00007474,
      0x0000b5b5,
      0x0000ffff,
      0x006dffff,
      0x009cffff,

      0x00002747,
      0x00004074,
      0x000063b5,
      0x00008cff,
      0x0059b4ff,
      0x009cd3ff,

      0x00000047,
      0x00000074,
      0x000000b5,
      0x000000ff,
      0x005959ff,
      0x009c9cff,

      0x002e0047,
      0x004b0074,
      0x007500b5,
      0x00a500ff,
      0x00c459ff,
      0x00dc9cff,

      0x00470047,
      0x00740074,
      0x00b500b5,
      0x00ff00ff,
      0x00ff66ff,
      0x00ff9cff,

      0x0047002a,
      0x00740045,
      0x00b5006b,
      0x00ff0098,
      0x00ff59bc,
      0x00ff94d3,

      0x00000000,
      0x00131313,
      0x00282828,
      0x00363636,
      0x004d4d4d,
      0x00656565,
      0x00818181,
      0x009f9f9f,
      0x00bcbcbc,
      0x00e2e2e2,
      0x00ffffff
    )
  }
}
