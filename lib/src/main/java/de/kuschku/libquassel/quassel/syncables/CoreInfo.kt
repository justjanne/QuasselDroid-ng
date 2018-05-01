/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.quassel.syncables.interfaces.ICoreInfo
import de.kuschku.libquassel.session.SignalProxy

class CoreInfo constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "CoreInfo"), ICoreInfo {
  override fun toVariantMap() = initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetProperties(properties)
  }

  override fun initProperties(): QVariantMap = mapOf(
    "coreData" to QVariant.of(coreData(), Type.QVariantMap)
  )

  override fun initSetProperties(properties: QVariantMap) {
    setCoreData(properties["coreData"].value(coreData()))
  }

  override fun setCoreData(data: QVariantMap) {
    _coreData = data
    super.setCoreData(data)
  }

  fun coreData() = _coreData

  private var _coreData: QVariantMap = emptyMap()
}
