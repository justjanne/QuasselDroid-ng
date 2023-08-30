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

package de.kuschku.libquassel.integration

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.QuasselType
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.util.TestSession
import de.kuschku.libquassel.util.setupTestSession
import de.kuschku.libquassel.util.with
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BufferViewConfigTest {
  lateinit var session: TestSession

  @BeforeEach
  fun setUp() {
    session = setupTestSession()
  }

  // Test positioning of added channel
  @Test
  fun addChannelAutomatically() = session.with {
    val bufferViewConfig = bufferViewManager.bufferViewConfig(0)!!

    ensure {
      bufferViewConfig.insertBufferSorted(bufferSyncer.bufferInfo(BufferId(4))!!, bufferSyncer)
    }.does {
      callSync(bufferViewConfig, "requestAddBuffer", listOf(
        QVariant_.of(BufferId(4), QuasselType.BufferId),
        QVariant_.of(2, QtType.Int)
      ))
    }
  }
}
