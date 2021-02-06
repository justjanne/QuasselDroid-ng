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
import de.kuschku.libquassel.protocol.io.print
import de.kuschku.libquassel.protocol.serializers.handshake.HandshakeMapSerializer
import de.kuschku.libquassel.protocol.serializers.handshake.HandshakeSerializer
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals

fun <T> testHandshakeSerializerEncoded(
  serializer: HandshakeSerializer<T>,
  data: T,
  featureSet: FeatureSet = FeatureSet.all(),
  matcher: Matcher<T>? = null
) {
  val buffer = ChainedByteBuffer()
  HandshakeMapSerializer.serialize(buffer, serializer.serialize(data), featureSet)
  val result = buffer.toBuffer()
  result.print()
  val after = serializer.deserialize(HandshakeMapSerializer.deserialize(result, featureSet))
  assertEquals(0, result.remaining())
  if (matcher != null) {
    assertThat(after, matcher)
  } else {
    assertEquals(data, after)
  }
}
