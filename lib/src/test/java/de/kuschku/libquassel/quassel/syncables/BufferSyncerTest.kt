/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.roundTrip
import org.junit.Test

class BufferSyncerTest {
  @Test
  fun testSerialization() {
    val original = BufferSyncer(ISession.NULL)
    original.setLastSeenMsg(listOf(
      Pair(BufferId(1), MsgId(16)),
      Pair(BufferId(2), MsgId(17)),
      Pair(BufferId(3), MsgId(18)),
      Pair(BufferId(4), MsgId(19))
    ))
    original.setMarkerLines(listOf(
      Pair(BufferId(1), MsgId(26)),
      Pair(BufferId(2), MsgId(27)),
      Pair(BufferId(3), MsgId(28)),
      Pair(BufferId(4), MsgId(29))
    ))
    original.setActivities(listOf(
      Pair(BufferId(1), Message_Type.of(Message_Type.Plain)),
      Pair(BufferId(2), Message_Type.of(Message_Type.Notice)),
      Pair(BufferId(3), Message_Type.of(Message_Type.Action)),
      Pair(BufferId(4), Message_Type.of(Message_Type.Error))
    ))
    original.setHighlightCounts(listOf(
      Pair(BufferId(1), 36),
      Pair(BufferId(2), 37),
      Pair(BufferId(3), 38),
      Pair(BufferId(4), 39)
    ))

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    assert(original.isEqual(copy)) {
      System.err.println("Original:\n$original")
      System.err.println("Copy:\n$copy")
    }
  }

  @Test
  fun testCopy() {
    val original = BufferSyncer(ISession.NULL)
    original.setLastSeenMsg(listOf(
      Pair(BufferId(1), MsgId(16)),
      Pair(BufferId(2), MsgId(17)),
      Pair(BufferId(3), MsgId(18)),
      Pair(BufferId(4), MsgId(19))
    ))
    original.setMarkerLines(listOf(
      Pair(BufferId(1), MsgId(26)),
      Pair(BufferId(2), MsgId(27)),
      Pair(BufferId(3), MsgId(28)),
      Pair(BufferId(4), MsgId(29))
    ))
    original.setActivities(listOf(
      Pair(BufferId(1), Message_Type.of(Message_Type.Plain)),
      Pair(BufferId(2), Message_Type.of(Message_Type.Notice)),
      Pair(BufferId(3), Message_Type.of(Message_Type.Action)),
      Pair(BufferId(4), Message_Type.of(Message_Type.Error))
    ))
    original.setHighlightCounts(listOf(
      Pair(BufferId(1), 36),
      Pair(BufferId(2), 37),
      Pair(BufferId(3), 38),
      Pair(BufferId(4), 39)
    ))

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    assert(original.isEqual(copy)) {
      System.err.println("Original:\n$original")
      System.err.println("Copy:\n$copy")
    }
  }
}
