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
@file:Suppress("NOTHING_TO_INLINE")

package de.kuschku.libquassel.quassel.syncables.interfaces

import de.justjanne.libquassel.annotations.ProtocolSide
import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.session.SignalProxy
import io.reactivex.Observable

interface ISyncableObject {
  val objectName: String
  var identifier: Pair<String, String>
  val className: String
  var initialized: Boolean
  val proxy: SignalProxy
  val liveInitialized: Observable<Boolean>

  fun sync(target: ProtocolSide, function: String, vararg arg: QVariant_) {
    if (initialized && proxy.shouldSync(target)) {
      proxy.callSync(className, objectName, function, arg.toList())
    }
  }

  fun rpc(target: ProtocolSide, function: String, vararg arg: QVariant_) {
    if (initialized && proxy.shouldRpc(target)) {
      proxy.callRpc(function, arg.toList())
    }
  }
  fun update(properties: QVariantMap) {
    fromVariantMap(properties)
    sync(
      target = ProtocolSide.CLIENT,
      "update",
      QVariant.of(properties, QtType.QVariantMap)
    )
  }

  fun requestUpdate(properties: QVariantMap = toVariantMap()) {
    sync(
      target = ProtocolSide.CORE,
      "requestUpdate",
      QVariant.of(properties, QtType.QVariantMap)
    )
  }

  fun fromVariantMap(properties: QVariantMap) = Unit
  fun toVariantMap(): QVariantMap = emptyMap()

  fun deinit()
  fun init() {}
}
