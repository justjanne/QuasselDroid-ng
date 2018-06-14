/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.roundTrip
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test

class StringSerializerTest {
  @Test
  fun testVigListOfNaughtyStrings() {
    this::class.java.getResourceAsStream("/blns.txt").bufferedReader(Charsets.UTF_8).forEachLine {
      // Ignore comments
      if (!it.startsWith('#')) {
        assertThat<String>(roundTrip(StringSerializer.UTF16, it), BomMatcher(it))
        assertThat<String>(roundTrip(StringSerializer.UTF8, it), BomMatcher(it))
      }
    }
  }

  @Test
  fun testAscii() {
    // The simple solution: Just test all
    val it = String(CharArray(256, Int::toChar).toList().shuffled().toCharArray())
    assertEquals(it, roundTrip(StringSerializer.C, it))
  }

  private class BomMatcher(private val expected: String) : BaseMatcher<String>() {
    override fun describeTo(description: Description?) {
      description?.appendText(expected)
    }

    override fun matches(item: Any?) =
      (item as? String)?.endsWith(expected.trimStart('￾', '﻿')) == true
  }
}
