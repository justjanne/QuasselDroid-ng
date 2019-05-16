/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.libquassel.protocol.coresetup

import de.kuschku.libquassel.protocol.*
import java.io.Serializable

class CoreSetupBackendConfigElement(
  val key: String,
  val displayName: String,
  defaultValue: QVariant_
) : Serializable {
  private val typeId = defaultValue.type.id
  private val customType = defaultValue.type.serializableName
  private val rawDefaultValue = defaultValue.data as? Serializable

  val defaultValue: QVariant_
    get() {
      val type = Type.of(typeId)
      return if (type == Type.UserType) {
        val name = customType
        val qType = QType.of(name) ?: throw IllegalArgumentException("No such type: $name")
        QVariant.of<All_>(rawDefaultValue, qType)
      } else {
        QVariant.of<All_>(rawDefaultValue,
                          type ?: throw IllegalArgumentException("No such type: $type"))
      }
    }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CoreSetupBackendConfigElement

    if (key != other.key) return false
    if (displayName != other.displayName) return false
    if (typeId != other.typeId) return false
    if (customType != other.customType) return false
    if (rawDefaultValue != other.rawDefaultValue) return false

    return true
  }

  override fun hashCode(): Int {
    var result = key.hashCode()
    result = 31 * result + displayName.hashCode()
    result = 31 * result + typeId
    result = 31 * result + customType.hashCode()
    result = 31 * result + (rawDefaultValue?.hashCode() ?: 0)
    return result
  }

  override fun toString(): String {
    return "CoreBackendSetupDataEntry(key='$key', displayName='$displayName', defaultValue='$defaultValue')"
  }
}
