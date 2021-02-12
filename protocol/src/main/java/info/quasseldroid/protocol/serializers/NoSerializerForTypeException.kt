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

package info.quasseldroid.protocol.serializers

import info.quasseldroid.protocol.variant.QtType
import info.quasseldroid.protocol.variant.QuasselType

sealed class NoSerializerForTypeException : Exception() {
  data class Qt(
    private val type: Int,
    private val javaType: Class<*>? = null
  ) : NoSerializerForTypeException() {
    constructor(
      type: QtType,
      javaType: Class<*>? = null
    ) : this(type.id, javaType)

    override fun toString(): String {
      return "NoSerializerForTypeException.Qt(type=$type, javaType=$javaType)"
    }


  }

  data class Quassel(
    private val type: Int,
    private val typename: String?,
    private val javaType: Class<*>? = null
  ) : NoSerializerForTypeException() {
    constructor(
      type: QtType,
      typename: String?,
      javaType: Class<*>? = null
    ) : this(type.id, typename, javaType)

    constructor(
      type: QuasselType,
      javaType: Class<*>? = null
    ) : this(type.qtType, type.typeName, javaType)

    override fun toString(): String {
      return "NoSerializerForTypeException.Quassel(type=$type, typename=$typename, javaType=$javaType)"
    }
  }

  data class Handshake(
    private val type: String,
    private val javaType: Class<*>? = null
  ) : NoSerializerForTypeException() {
    override fun toString(): String {
      return "NoSerializerForTypeException.Handshake(type='$type', javaType=$javaType)"
    }
  }
}
