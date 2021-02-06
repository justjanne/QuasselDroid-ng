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
import de.kuschku.libquassel.protocol.serializers.primitive.QtSerializer
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.junit.Assert

fun <T> testQtSerializerDirect(serializer: QtSerializer<T>, data: T, matcher: Matcher<T>? = null) {
  val connectionFeatureSet = FeatureSet.build()
  val buffer = ChainedByteBuffer()

  serializer.serialize(buffer, data, connectionFeatureSet)
  val result = buffer.toBuffer()
  result.print()
  val after = serializer.deserialize(result, connectionFeatureSet)

  Assert.assertEquals(0, result.remaining())
  if (matcher != null) {
    MatcherAssert.assertThat(data, matcher)
  } else {
    Assert.assertEquals(data, after)
  }
}

