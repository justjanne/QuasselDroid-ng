package de.kuschku.libquassel.quassel

import de.kuschku.libquassel.util.Flag
import de.kuschku.libquassel.util.Flags

enum class ProtocolFeature(override val bit: Int) : Flag<ProtocolFeature> {
  None(0x00),
  TLS(0x01),
  Compression(0x02);

  companion object : Flags.Factory<ProtocolFeature> {
    override val NONE = ProtocolFeature.of()
    override fun of(bit: Int) = Flags.of<ProtocolFeature>(bit)
    override fun of(vararg flags: ProtocolFeature) = Flags.of(*flags)
    override fun of(flags: Iterable<ProtocolFeature>) = Flags.of(flags)
  }
}
