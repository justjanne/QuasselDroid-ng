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

package de.kuschku.libquassel.protocol.connection

import de.kuschku.bitflags.Flag
import de.kuschku.bitflags.Flags
import de.kuschku.bitflags.toEnumSet

enum class ProtocolFeature(
  override val value: UByte,
) : Flag<UByte> {
  None(0x00u),
  TLS(0x01u),
  Compression(0x02u);

  companion object : Flags<UByte, ProtocolFeature> {
    private val values = values().associateBy(ProtocolFeature::value)
    override val all: ProtocolFeatures = values.values.toEnumSet()
  }
}

typealias ProtocolFeatures = Set<ProtocolFeature>
