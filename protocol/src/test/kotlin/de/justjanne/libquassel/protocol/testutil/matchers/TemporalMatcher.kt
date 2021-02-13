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

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.threeten.bp.*
import org.threeten.bp.temporal.Temporal

class TemporalMatcher<T: Temporal>(
  private val expected: T
) : BaseMatcher<T>() {
  override fun describeTo(description: Description?) {
    description?.appendText(expected.toString())
  }

  override fun matches(item: Any?): Boolean {
    return when {
      expected is ZonedDateTime && item is ZonedDateTime ->
        expected == item
      expected is ZonedDateTime && item is OffsetDateTime ->
        expected.toOffsetDateTime() == item
      expected is OffsetDateTime && item is OffsetDateTime ->
        expected == item
      expected is LocalDateTime && item is LocalDateTime ->
        expected == item
      expected is LocalTime && item is LocalTime ->
        expected == item
      expected is LocalDate && item is LocalDate ->
        expected == item
      expected is Instant && item is Instant ->
        expected == item
      else ->
        false
    }
  }
}
