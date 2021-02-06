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
import de.kuschku.libquassel.protocol.serializers.primitive.QVariantSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.QuasselSerializer
import de.kuschku.libquassel.protocol.variant.QVariant
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals

fun <T> testQuasselSerializerVariant(
  serializer: QuasselSerializer<T>,
  data: T,
  featureSet: FeatureSet = FeatureSet.all(),
  matcher: Matcher<in T>? = null
) {
  val buffer = ChainedByteBuffer()
  QVariantSerializer.serialize(buffer, QVariant.of(data, serializer), featureSet)
  val result = buffer.toBuffer()
  result.print()
  val after = QVariantSerializer.deserialize(result, featureSet)
  assertEquals(0, result.remaining())
  if (matcher != null) {
    @Suppress("UNCHECKED_CAST")
    assertThat(after.value() as T, matcher)
  } else {
    assertEquals(data, after.value())
  }
}
