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

package de.kuschku.libquassel.quassel.syncables.interfaces

import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.annotations.SyncedCall
import de.justjanne.libquassel.annotations.SyncedObject
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.qVariant
import de.kuschku.libquassel.quassel.BufferInfo
import java.io.Serializable

@SyncedObject(name = "AliasManager")
interface IAliasManager : ISyncableObject {
  fun initAliases(): QVariantMap
  fun initSetAliases(aliases: QVariantMap)

  @SyncedCall(target = ProtocolSide.CORE)
  fun addAlias(name: String, expansion: String) {
    sync(
      target = ProtocolSide.CORE,
      "addAlias",
      qVariant(name, QtType.QString),
      qVariant(expansion, QtType.QString)
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)

  data class Alias(
    val name: String?,
    val expansion: String?
  ) : Serializable

  data class Command(
    val buffer: BufferInfo,
    val message: String
  )
}
