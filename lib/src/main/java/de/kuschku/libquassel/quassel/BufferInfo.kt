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

package de.kuschku.libquassel.quassel

import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Buffer_Types
import de.kuschku.libquassel.util.flag.Flag
import de.kuschku.libquassel.util.flag.Flags
import de.kuschku.libquassel.util.flag.ShortFlag
import de.kuschku.libquassel.util.flag.ShortFlags

data class BufferInfo(
  var bufferId: Int = -1,
  var networkId: Int = -1,
  var type: Buffer_Types = Buffer_Type.of(),
  var groupId: Int = -1,
  var bufferName: String? = null
) {
  enum class Type(override val bit: UShort) : ShortFlag<Type> {
    InvalidBuffer(0x00u),
    StatusBuffer(0x01u),
    ChannelBuffer(0x02u),
    QueryBuffer(0x04u),
    GroupBuffer(0x08u);

    companion object : ShortFlags.Factory<Type> {
      override val NONE = Buffer_Type.of()
      val validValues = values().filter { it.bit != 0u.toUShort() }.toTypedArray()
      override fun of(bit: Short) = ShortFlags.of<Type>(bit)
      override fun of(bit: UShort) = ShortFlags.of<Type>(bit)
      override fun of(vararg flags: Type) = ShortFlags.of(*flags)
      override fun of(flags: Iterable<Type>) = ShortFlags.of(flags)
    }
  }

  enum class Activity(override val bit: UInt) : Flag<Activity> {
    NoActivity(0x00u),
    OtherActivity(0x01u),
    NewMessage(0x02u),
    Highlight(0x04u);

    companion object : Flags.Factory<Activity> {
      override val NONE = Activity.of()
      override fun of(bit: Int) = Flags.of<Activity>(bit)
      override fun of(bit: UInt) = Flags.of<Activity>(bit)
      override fun of(vararg flags: Activity) = Flags.of(*flags)
      override fun of(flags: Iterable<Activity>) = Flags.of(flags)
    }
  }
}
