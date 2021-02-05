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

package de.kuschku.libquassel.protocol.serializers

import de.kuschku.libquassel.protocol.variant.QtType
import de.kuschku.libquassel.protocol.variant.QuasselType

class NoSerializerForTypeException(
  private val javaType: Class<*>?,
  private val qtType: Int,
  private val quasselType: String?,
) : Exception() {
  constructor(quasselType: QuasselType, javaType: Class<*>? = null) :
    this(javaType, quasselType.qtType.id, quasselType.typeName)

  constructor(qtType: QtType, javaType: Class<*>? = null) :
    this(javaType, qtType.id, null)

  constructor(qtType: Int, quasselType: String?) :
    this(null, qtType, quasselType)

  override fun toString(): String {
    return "NoSerializerForTypeException(javaType=$javaType, qtType=${QtType.of(qtType) ?: qtType}, quasselType=${QuasselType.of(quasselType) ?: quasselType})"
  }
}
