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
package de.justjanne.libquassel.protocol.testutil.matchers

import de.justjanne.libquassel.protocol.io.contentToString
import de.justjanne.libquassel.protocol.io.isEmpty
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import java.nio.ByteBuffer

class ByteBufferMatcher(buffer: ByteBuffer?) : BaseMatcher<ByteBuffer>() {
  private val expected = buffer?.let { original ->
    val copy = ByteBuffer.allocateDirect(original.limit())
    original.rewind()
    copy.put(original)
    copy.rewind()
    original.rewind()
    copy
  }

  override fun describeTo(description: Description?) {
    description?.appendText(expected?.contentToString())
  }

  override fun describeMismatch(item: Any?, description: Description?) {
    description?.appendText("was ")
    description?.appendText((item as? ByteBuffer)?.rewind()?.contentToString())
  }

  override fun matches(item: Any?): Boolean {
    val actual = item as? ByteBuffer

    if (actual.isEmpty() && expected.isEmpty()) {
      return true
    }

    return actual?.rewind()?.contentToString() == expected?.rewind()?.contentToString()
  }
}
