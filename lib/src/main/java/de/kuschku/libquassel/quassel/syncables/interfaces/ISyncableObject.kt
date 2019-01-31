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
@file:Suppress("NOTHING_TO_INLINE")
package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.protocol.ARG
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.session.SignalProxy
import io.reactivex.Observable

interface ISyncableObject {
  val objectName: String
  var identifier: Pair<String, String>
  val className: String
  var initialized: Boolean
  val proxy: SignalProxy
  val liveInitialized: Observable<Boolean>

  fun requestUpdate(properties: QVariantMap = toVariantMap()) {
    REQUEST("requestUpdate", ARG(properties, Type.QVariantMap))
  }

  fun update(properties: QVariantMap) {
    fromVariantMap(properties)
    SYNC("update", ARG(properties, Type.QVariantMap))
  }

  fun deinit()
  fun init() {}

  fun fromVariantMap(properties: QVariantMap) = Unit
  fun toVariantMap(): QVariantMap = emptyMap()
}

inline fun ISyncableObject.SYNC(function: String, vararg arg: QVariant_) {
  // Don’t transmit calls back that we just got from the network
  if (initialized && proxy.shouldSync(className, objectName, function))
    proxy.callSync(className, objectName, function, arg.toList())
}

inline fun ISyncableObject.REQUEST(function: String, vararg arg: QVariant_) {
  // Don’t transmit calls back that we just got from the network
  if (initialized && proxy.shouldSync(className, objectName, function))
    proxy.callSync(className, objectName, function, arg.toList())
}
