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

package de.kuschku.quasseldroid.viewmodel

import de.kuschku.quasseldroid.viewmodel.ChatViewModel.Companion.recentMessagesChangeInternal
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatViewModelTest {
  @Test
  fun testRecentMessagesChange() {
    assertEquals(recentMessagesChangeInternal(0, 0, -1), -1)
    assertEquals(recentMessagesChangeInternal(0, 0, +1), -1)
    assertEquals(recentMessagesChangeInternal(-1, 0, -1), -1)
    assertEquals(recentMessagesChangeInternal(-1, 0, +1), -1)
    assertEquals(recentMessagesChangeInternal(1, 0, -1), -1)
    assertEquals(recentMessagesChangeInternal(1, 0, +1), -1)


    assertEquals(recentMessagesChangeInternal(0, 5, -1), -1)
    assertEquals(recentMessagesChangeInternal(0, 5, +1), 1)

    assertEquals(recentMessagesChangeInternal(1, 5, -1), 0)
    assertEquals(recentMessagesChangeInternal(1, 5, +1), 2)

    assertEquals(recentMessagesChangeInternal(4, 5, -1), 3)
    assertEquals(recentMessagesChangeInternal(4, 5, +1), 0)
  }
}
