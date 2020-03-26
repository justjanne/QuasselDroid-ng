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

package de.kuschku.quasseldroid.util.compatibility

import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test

class AndroidCrashFixerTest {
  companion object {
    lateinit var crashingText: String
    lateinit var validText: String

    @JvmStatic
    @BeforeClass
    fun setUp() {
      crashingText = AndroidCrashFixerTest::class.java.getResourceAsStream("/crashing_text.txt")?.bufferedReader()?.readText()
                     ?: ""
      validText = AndroidCrashFixerTest::class.java.getResourceAsStream("/valid_text.txt")?.bufferedReader()?.readText()
                  ?: ""
    }
  }


  @Test
  fun testRemovesCrashableCharacters() {
    val result = AndroidCrashFixer.removeCrashableCharacters(crashingText)
    assertTrue(Regex("\u200F\\s*\u200E").containsMatchIn(crashingText))
    assertTrue(Regex("\u200E\\s*\u200F").containsMatchIn(crashingText))
    assertFalse(Regex("\u200F\\s*\u200E").containsMatchIn(result))
    assertFalse(Regex("\u200E\\s*\u200F").containsMatchIn(result))
  }

  @Test
  fun testRetainsContent() {
    assertEquals(validText, AndroidCrashFixer.removeCrashableCharacters(validText))
  }
}
