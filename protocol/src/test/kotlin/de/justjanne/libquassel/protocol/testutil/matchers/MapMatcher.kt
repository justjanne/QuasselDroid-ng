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

class MapMatcher<K, V>(
  private val expected: Map<K, V>
) : BaseMatcher<Map<K, V>>() {
  override fun describeTo(description: Description?) {
    description?.appendText(expected.toString())
  }

  override fun describeMismatch(item: Any?, description: Description?) {
    if (item is Map<*, *>) {
      for (key in expected.keys) {
        if (!item.containsKey(key)) {
          description?.appendText(" did not have key $key")
        }
        if (expected[key] != item[key]) {
          description?.appendText(" key $key was: ${item[key]} instead of ${expected[key]}")
        }
      }
    } else {
      description?.appendText("was: $item")
    }
  }

  override fun matches(item: Any?): Boolean {
    if (item is Map<*, *>) {
      for (key in expected.keys) {
        if (!item.containsKey(key)) {
          return false
        }
        if (expected[key] != item[key]) {
          return false
        }
      }
      return true
    } else {
      return false
    }
  }
}
