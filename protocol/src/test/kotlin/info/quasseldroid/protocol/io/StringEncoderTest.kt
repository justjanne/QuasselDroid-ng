/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package info.quasseldroid.protocol.io

import info.quasseldroid.protocol.testutil.byteBufferOf
import info.quasseldroid.protocol.testutil.matchers.ByteBufferMatcher
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

class StringEncoderTest {
  private val ascii = StringEncoder(Charsets.ISO_8859_1)
  private val utf8 = StringEncoder(Charsets.UTF_8)
  private val utf16 = StringEncoder(Charsets.UTF_16BE)

  @Test
  fun testNullString() {
    assertThat(
      ascii.encode(null),
      ByteBufferMatcher(ByteBuffer.allocate(0))
    )
    assertThat(
      utf8.encode(null),
      ByteBufferMatcher(ByteBuffer.allocate(0))
    )
    assertThat(
      utf16.encode(null),
      ByteBufferMatcher(ByteBuffer.allocate(0))
    )
  }

  @Test
  fun testUnencodableString() {
    assertEquals(
      0,
      ascii.encode("\uFFFF").remaining()
    )
    assertThat(
      ascii.encode("\uFFFF"),
      ByteBufferMatcher(byteBufferOf())
    )
  }

  @Test
  fun testNullChar() {
    assertEquals(
      1,
      ascii.encodeChar(null).remaining()
    )
    assertThat(
      ascii.encodeChar(null),
      ByteBufferMatcher(byteBufferOf(0))
    )

    assertThat(
      utf8.encodeChar(null),
      ByteBufferMatcher(byteBufferOf(0xEFu, 0xBFu, 0xBDu))
    )

    assertEquals(
      2,
      utf16.encodeChar(null).remaining()
    )
    assertThat(
      utf16.encodeChar(null),
      ByteBufferMatcher(byteBufferOf(0xFFu, 0xFDu)),
    )
  }

  @Test
  fun testUnencodableChar() {
    assertEquals(
      1,
      ascii.encodeChar('\uFFFF').remaining()
    )
    assertThat(
      ascii.encodeChar('\uFFFF'),
      ByteBufferMatcher(byteBufferOf(0))
    )
  }
}
