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
import de.kuschku.libquassel.protocol.io.ChainedByteBuffer
import de.kuschku.libquassel.protocol.serializers.handshake.HandshakeSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.HandshakeMapSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.QtSerializer
import de.kuschku.libquassel.protocol.testutil.matchers.ByteBufferMatcher
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import java.nio.ByteBuffer

fun <T> serialize(
  serializer: QtSerializer<T>,
  data: T,
  featureSet: FeatureSet = FeatureSet.all()
): ByteBuffer {
  val buffer = ChainedByteBuffer()
  serializer.serialize(buffer, data, featureSet)
  return buffer.toBuffer()
}

fun <T> testSerialize(
  serializer: QtSerializer<T>,
  data: T,
  buffer: ByteBuffer,
  featureSet: FeatureSet = FeatureSet.all()
) {
  val after = serialize(serializer, data, featureSet)
  assertThat(after, ByteBufferMatcher(buffer))
}

fun <T> testSerialize(
  serializer: HandshakeSerializer<T>,
  data: T,
  buffer: ByteBuffer,
  featureSet: FeatureSet = FeatureSet.all()
) {
  val map = serializer.serialize(data)
  val after = serialize(HandshakeMapSerializer, map, featureSet)
  assertThat(after, ByteBufferMatcher(buffer))
}
