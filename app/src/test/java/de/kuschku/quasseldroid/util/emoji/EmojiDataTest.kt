/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.quasseldroid.util.emoji

import android.text.SpannableStringBuilder
import de.kuschku.quasseldroid.QuasseldroidTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(application = QuasseldroidTest::class)
@RunWith(RobolectricTestRunner::class)
class EmojiDataTest {
  @Test
  fun replaceShortCodes() {
    assertEquals("\ud83d\udc4d", replaceShortCodes(":like:"))
    assertEquals("this\ud83d\udc4disa\ud83d\udc1e\ud83d\udc4dtest",
                 replaceShortCodes("this:like:isa:beetle::+1:test"))
  }

  companion object {
    private fun replaceShortCodes(text: String): String =
      EmojiData.replaceShortCodes(SpannableStringBuilder(text)).toString()
  }
}
