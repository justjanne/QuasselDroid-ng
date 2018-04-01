package de.kuschku.libquassel.quassel

import de.kuschku.libquassel.protocol.Buffer_Types
import de.kuschku.libquassel.util.flag.Flag
import de.kuschku.libquassel.util.flag.Flags
import de.kuschku.libquassel.util.flag.ShortFlag
import de.kuschku.libquassel.util.flag.ShortFlags

data class BufferInfo(
  var bufferId: Int,
  var networkId: Int,
  var type: Buffer_Types,
  var groupId: Int,
  var bufferName: String?
) {
  enum class Type(override val bit: Short) : ShortFlag<Type> {
    InvalidBuffer(0x00),
    StatusBuffer(0x01),
    ChannelBuffer(0x02),
    QueryBuffer(0x04),
    GroupBuffer(0x08);

    companion object : ShortFlags.Factory<Type> {
      val validValues = values().filter { it.bit != 0.toShort() }.toTypedArray()
      override fun of(bit: Short) = ShortFlags.of<Type>(bit)
      override fun of(vararg flags: Type) = ShortFlags.of(*flags)
      override fun of(flags: Iterable<Type>) = ShortFlags.of(flags)
    }
  }

  enum class Activity(override val bit: Int) : Flag<Activity> {
    NoActivity(0x00),
    OtherActivity(0x01),
    NewMessage(0x02),
    Highlight(0x04);

    companion object : Flags.Factory<Activity> {
      override val NONE = Activity.of()
      override fun of(bit: Int) = Flags.of<Activity>(bit)
      override fun of(vararg flags: Activity) = Flags.of(*flags)
      override fun of(flags: Iterable<Activity>) = Flags.of(flags)
    }
  }
}
