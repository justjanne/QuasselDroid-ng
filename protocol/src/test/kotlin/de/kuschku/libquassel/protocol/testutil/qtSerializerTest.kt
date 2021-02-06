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
import de.kuschku.libquassel.protocol.serializers.primitive.QtSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.QuasselSerializer
import org.hamcrest.Matcher
import java.nio.ByteBuffer
fun <T> qtSerializerTest(
  serializer: QtSerializer<T>,
  value: T,
  encoded: ByteBuffer? = null,
  matcher: ((T) -> Matcher<T>)? = null,
  featureSets: List<FeatureSet> = listOf(FeatureSet.none(), FeatureSet.all()),
  deserializeFeatureSet: FeatureSet = FeatureSet.all(),
) {
  for (featureSet in featureSets) {
    testQtSerializerDirect(serializer, value, featureSet, matcher?.invoke(value))
    testQtSerializerVariant(serializer, value, featureSet, matcher?.invoke(value))
  }
  if (encoded != null) {
    if (matcher != null) {
      testDeserialize(serializer, matcher(value), encoded.rewind(), deserializeFeatureSet)
    } else {
      testDeserialize(serializer, value, encoded.rewind(), deserializeFeatureSet)
    }
  }
}
