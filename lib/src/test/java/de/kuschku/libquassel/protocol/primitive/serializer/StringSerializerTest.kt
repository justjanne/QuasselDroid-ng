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

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.deserialize
import de.kuschku.libquassel.util.roundTrip
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringSerializerTest {
  @Test
  fun testBigListOfNaughtyStrings() {
    this::class.java.getResourceAsStream("/blns.txt").bufferedReader(Charsets.UTF_8).forEachLine {
      // Ignore comments
      if (!it.startsWith('#')) {
        assertThat<String>(roundTrip(StringSerializer.UTF8, it), BomMatcher(it))
        assertThat<String>(roundTrip(StringSerializer.UTF16, it), BomMatcher(it))
      }
    }
  }

  @Test
  fun testNaughtyStrings() {
    this::class.java.getResourceAsStream("/naughty_strings.txt").bufferedReader(Charsets.UTF_8).forEachLine {
      // Ignore comments
      if (!it.startsWith('#')) {
        assertEquals(it, roundTrip(StringSerializer.UTF8, it))
        assertEquals(it, roundTrip(StringSerializer.UTF16, it))
      }
    }
  }

  @Test
  fun testSample() {
    val value = """
      : ACHTUNG!
      ALLES TURISTEN UND NONTEKNISCHEN LOOKENPEEPERS!
      DAS KOMPUTERMASCHINE IST NICHT FÜR DER GEFINGERPOKEN UND MITTENGRABEN! ODERWISE IST EASY TO SCHNAPPEN DER SPRINGENWERK, BLOWENFUSEN UND POPPENCORKEN MIT SPITZENSPARKEN.
      IST NICHT FÜR GEWERKEN BEI DUMMKOPFEN. DER RUBBERNECKEN SIGHTSEEREN KEEPEN DAS COTTONPICKEN HÄNDER IN DAS POCKETS MUSS.
      ZO RELAXEN UND WATSCHEN DER BLINKENLICHTEN.
    """.trimIndent()

    assertEquals(value, roundTrip(StringSerializer.UTF8, value))
    // @formatter:off
    assertEquals(value, deserialize(StringSerializer.UTF8, byteArrayOf(0, 0, 1, -118, 58, 32, 65, 67, 72, 84, 85, 78, 71, 33, 10, 65, 76, 76, 69, 83, 32, 84, 85, 82, 73, 83, 84, 69, 78, 32, 85, 78, 68, 32, 78, 79, 78, 84, 69, 75, 78, 73, 83, 67, 72, 69, 78, 32, 76, 79, 79, 75, 69, 78, 80, 69, 69, 80, 69, 82, 83, 33, 10, 68, 65, 83, 32, 75, 79, 77, 80, 85, 84, 69, 82, 77, 65, 83, 67, 72, 73, 78, 69, 32, 73, 83, 84, 32, 78, 73, 67, 72, 84, 32, 70, -61, -100, 82, 32, 68, 69, 82, 32, 71, 69, 70, 73, 78, 71, 69, 82, 80, 79, 75, 69, 78, 32, 85, 78, 68, 32, 77, 73, 84, 84, 69, 78, 71, 82, 65, 66, 69, 78, 33, 32, 79, 68, 69, 82, 87, 73, 83, 69, 32, 73, 83, 84, 32, 69, 65, 83, 89, 32, 84, 79, 32, 83, 67, 72, 78, 65, 80, 80, 69, 78, 32, 68, 69, 82, 32, 83, 80, 82, 73, 78, 71, 69, 78, 87, 69, 82, 75, 44, 32, 66, 76, 79, 87, 69, 78, 70, 85, 83, 69, 78, 32, 85, 78, 68, 32, 80, 79, 80, 80, 69, 78, 67, 79, 82, 75, 69, 78, 32, 77, 73, 84, 32, 83, 80, 73, 84, 90, 69, 78, 83, 80, 65, 82, 75, 69, 78, 46, 10, 73, 83, 84, 32, 78, 73, 67, 72, 84, 32, 70, -61, -100, 82, 32, 71, 69, 87, 69, 82, 75, 69, 78, 32, 66, 69, 73, 32, 68, 85, 77, 77, 75, 79, 80, 70, 69, 78, 46, 32, 68, 69, 82, 32, 82, 85, 66, 66, 69, 82, 78, 69, 67, 75, 69, 78, 32, 83, 73, 71, 72, 84, 83, 69, 69, 82, 69, 78, 32, 75, 69, 69, 80, 69, 78, 32, 68, 65, 83, 32, 67, 79, 84, 84, 79, 78, 80, 73, 67, 75, 69, 78, 32, 72, -61, -124, 78, 68, 69, 82, 32, 73, 78, 32, 68, 65, 83, 32, 80, 79, 67, 75, 69, 84, 83, 32, 77, 85, 83, 83, 46, 10, 90, 79, 32, 82, 69, 76, 65, 88, 69, 78, 32, 85, 78, 68, 32, 87, 65, 84, 83, 67, 72, 69, 78, 32, 68, 69, 82, 32, 66, 76, 73, 78, 75, 69, 78, 76, 73, 67, 72, 84, 69, 78, 46)))
    // @formatter:on

    assertEquals(value, roundTrip(StringSerializer.UTF16, value))
    // @formatter:off
    assertEquals(value, deserialize(StringSerializer.UTF16, byteArrayOf(0, 0, 3, 14, 0, 58, 0, 32, 0, 65, 0, 67, 0, 72, 0, 84, 0, 85, 0, 78, 0, 71, 0, 33, 0, 10, 0, 65, 0, 76, 0, 76, 0, 69, 0, 83, 0, 32, 0, 84, 0, 85, 0, 82, 0, 73, 0, 83, 0, 84, 0, 69, 0, 78, 0, 32, 0, 85, 0, 78, 0, 68, 0, 32, 0, 78, 0, 79, 0, 78, 0, 84, 0, 69, 0, 75, 0, 78, 0, 73, 0, 83, 0, 67, 0, 72, 0, 69, 0, 78, 0, 32, 0, 76, 0, 79, 0, 79, 0, 75, 0, 69, 0, 78, 0, 80, 0, 69, 0, 69, 0, 80, 0, 69, 0, 82, 0, 83, 0, 33, 0, 10, 0, 68, 0, 65, 0, 83, 0, 32, 0, 75, 0, 79, 0, 77, 0, 80, 0, 85, 0, 84, 0, 69, 0, 82, 0, 77, 0, 65, 0, 83, 0, 67, 0, 72, 0, 73, 0, 78, 0, 69, 0, 32, 0, 73, 0, 83, 0, 84, 0, 32, 0, 78, 0, 73, 0, 67, 0, 72, 0, 84, 0, 32, 0, 70, 0, -36, 0, 82, 0, 32, 0, 68, 0, 69, 0, 82, 0, 32, 0, 71, 0, 69, 0, 70, 0, 73, 0, 78, 0, 71, 0, 69, 0, 82, 0, 80, 0, 79, 0, 75, 0, 69, 0, 78, 0, 32, 0, 85, 0, 78, 0, 68, 0, 32, 0, 77, 0, 73, 0, 84, 0, 84, 0, 69, 0, 78, 0, 71, 0, 82, 0, 65, 0, 66, 0, 69, 0, 78, 0, 33, 0, 32, 0, 79, 0, 68, 0, 69, 0, 82, 0, 87, 0, 73, 0, 83, 0, 69, 0, 32, 0, 73, 0, 83, 0, 84, 0, 32, 0, 69, 0, 65, 0, 83, 0, 89, 0, 32, 0, 84, 0, 79, 0, 32, 0, 83, 0, 67, 0, 72, 0, 78, 0, 65, 0, 80, 0, 80, 0, 69, 0, 78, 0, 32, 0, 68, 0, 69, 0, 82, 0, 32, 0, 83, 0, 80, 0, 82, 0, 73, 0, 78, 0, 71, 0, 69, 0, 78, 0, 87, 0, 69, 0, 82, 0, 75, 0, 44, 0, 32, 0, 66, 0, 76, 0, 79, 0, 87, 0, 69, 0, 78, 0, 70, 0, 85, 0, 83, 0, 69, 0, 78, 0, 32, 0, 85, 0, 78, 0, 68, 0, 32, 0, 80, 0, 79, 0, 80, 0, 80, 0, 69, 0, 78, 0, 67, 0, 79, 0, 82, 0, 75, 0, 69, 0, 78, 0, 32, 0, 77, 0, 73, 0, 84, 0, 32, 0, 83, 0, 80, 0, 73, 0, 84, 0, 90, 0, 69, 0, 78, 0, 83, 0, 80, 0, 65, 0, 82, 0, 75, 0, 69, 0, 78, 0, 46, 0, 10, 0, 73, 0, 83, 0, 84, 0, 32, 0, 78, 0, 73, 0, 67, 0, 72, 0, 84, 0, 32, 0, 70, 0, -36, 0, 82, 0, 32, 0, 71, 0, 69, 0, 87, 0, 69, 0, 82, 0, 75, 0, 69, 0, 78, 0, 32, 0, 66, 0, 69, 0, 73, 0, 32, 0, 68, 0, 85, 0, 77, 0, 77, 0, 75, 0, 79, 0, 80, 0, 70, 0, 69, 0, 78, 0, 46, 0, 32, 0, 68, 0, 69, 0, 82, 0, 32, 0, 82, 0, 85, 0, 66, 0, 66, 0, 69, 0, 82, 0, 78, 0, 69, 0, 67, 0, 75, 0, 69, 0, 78, 0, 32, 0, 83, 0, 73, 0, 71, 0, 72, 0, 84, 0, 83, 0, 69, 0, 69, 0, 82, 0, 69, 0, 78, 0, 32, 0, 75, 0, 69, 0, 69, 0, 80, 0, 69, 0, 78, 0, 32, 0, 68, 0, 65, 0, 83, 0, 32, 0, 67, 0, 79, 0, 84, 0, 84, 0, 79, 0, 78, 0, 80, 0, 73, 0, 67, 0, 75, 0, 69, 0, 78, 0, 32, 0, 72, 0, -60, 0, 78, 0, 68, 0, 69, 0, 82, 0, 32, 0, 73, 0, 78, 0, 32, 0, 68, 0, 65, 0, 83, 0, 32, 0, 80, 0, 79, 0, 67, 0, 75, 0, 69, 0, 84, 0, 83, 0, 32, 0, 77, 0, 85, 0, 83, 0, 83, 0, 46, 0, 10, 0, 90, 0, 79, 0, 32, 0, 82, 0, 69, 0, 76, 0, 65, 0, 88, 0, 69, 0, 78, 0, 32, 0, 85, 0, 78, 0, 68, 0, 32, 0, 87, 0, 65, 0, 84, 0, 83, 0, 67, 0, 72, 0, 69, 0, 78, 0, 32, 0, 68, 0, 69, 0, 82, 0, 32, 0, 66, 0, 76, 0, 73, 0, 78, 0, 75, 0, 69, 0, 78, 0, 76, 0, 73, 0, 67, 0, 72, 0, 84, 0, 69, 0, 78, 0, 46)))
    // @formatter:on
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
