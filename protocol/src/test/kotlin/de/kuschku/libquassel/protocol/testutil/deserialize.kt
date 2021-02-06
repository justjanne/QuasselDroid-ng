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

package de.kuschku.libquassel.protocol.testutil

import de.kuschku.libquassel.protocol.features.FeatureSet
import de.kuschku.libquassel.protocol.serializers.handshake.HandshakeMapSerializer
import de.kuschku.libquassel.protocol.serializers.handshake.HandshakeSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.QtSerializer
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.junit.Assert
import java.nio.ByteBuffer

fun <T> deserialize(serializer: QtSerializer<T>, buffer: ByteBuffer): T {
  val connectionFeatureSet = FeatureSet.build()
  val result = serializer.deserialize(
    buffer,
    connectionFeatureSet
  )
  Assert.assertEquals(0, buffer.remaining())
  return result
}

fun <T> testDeserialize(serializer: QtSerializer<T>, matcher: Matcher<in T>, buffer: ByteBuffer) {
  val after = deserialize(serializer, buffer)
  MatcherAssert.assertThat(after, matcher)
}

fun <T> testDeserialize(serializer: QtSerializer<T>, data: T, buffer: ByteBuffer) {
  val after = deserialize(serializer, buffer)
  Assert.assertEquals(data, after)
}

fun <T> testDeserialize(serializer: HandshakeSerializer<T>, matcher: Matcher<in T>, buffer: ByteBuffer) {
  val map = deserialize(HandshakeMapSerializer, buffer)
  val after = serializer.deserialize(map)
  MatcherAssert.assertThat(after, matcher)
}

fun <T> testDeserialize(serializer: HandshakeSerializer<T>, data: T, buffer: ByteBuffer) {
  val map = deserialize(HandshakeMapSerializer, buffer)
  val after = serializer.deserialize(map)
  Assert.assertEquals(data, after)
}
