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

import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.randomBoolean
import de.kuschku.libquassel.util.randomOf
import de.kuschku.libquassel.util.randomString
import de.kuschku.libquassel.util.roundTrip
import org.junit.Test

class IgnoreListManagerTest {
  @Test
  fun testSerialization() {
    val original = IgnoreListManager(ISession.NULL, SignalProxy.NULL)
    original.setIgnoreList(listOf(
      IgnoreListManager.IgnoreListItem(
        type = randomOf(*IgnoreListManager.IgnoreType.values()),
        ignoreRule = randomString(),
        isRegEx = randomBoolean(),
        strictness = randomOf(IgnoreListManager.StrictnessType.SoftStrictness,
                              IgnoreListManager.StrictnessType.HardStrictness),
        scope = randomOf(*IgnoreListManager.ScopeType.values()),
        scopeRule = randomString(),
        isActive = randomBoolean()
      ),
      IgnoreListManager.IgnoreListItem(
        type = randomOf(*IgnoreListManager.IgnoreType.values()),
        ignoreRule = randomString(),
        isRegEx = randomBoolean(),
        strictness = randomOf(IgnoreListManager.StrictnessType.SoftStrictness,
                              IgnoreListManager.StrictnessType.HardStrictness),
        scope = randomOf(*IgnoreListManager.ScopeType.values()),
        scopeRule = randomString(),
        isActive = randomBoolean()
      )
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
    val original = IgnoreListManager(ISession.NULL, SignalProxy.NULL)
    original.setIgnoreList(listOf(
      IgnoreListManager.IgnoreListItem(
        type = randomOf(*IgnoreListManager.IgnoreType.values()),
        ignoreRule = randomString(),
        isRegEx = randomBoolean(),
        strictness = randomOf(IgnoreListManager.StrictnessType.SoftStrictness,
                              IgnoreListManager.StrictnessType.HardStrictness),
        scope = randomOf(*IgnoreListManager.ScopeType.values()),
        scopeRule = randomString(),
        isActive = randomBoolean()
      ),
      IgnoreListManager.IgnoreListItem(
        type = randomOf(*IgnoreListManager.IgnoreType.values()),
        ignoreRule = randomString(),
        isRegEx = randomBoolean(),
        strictness = randomOf(IgnoreListManager.StrictnessType.SoftStrictness,
                              IgnoreListManager.StrictnessType.HardStrictness),
        scope = randomOf(*IgnoreListManager.ScopeType.values()),
        scopeRule = randomString(),
        isActive = randomBoolean()
      )
    ))

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    assert(original.isEqual(copy)) {
      System.err.println("Original:\n$original")
      System.err.println("Copy:\n$copy")
    }
  }
}
