package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.quassel.syncables.interfaces.IDccConfig
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object DccConfig_PortSelectionModeSerializer : Serializer<IDccConfig.PortSelectionMode> {
  override fun serialize(buffer: ChainedByteBuffer, data: IDccConfig.PortSelectionMode,
                         features: QuasselFeatures) {
    buffer.put(data.value)
  }

  override fun deserialize(buffer: ByteBuffer,
                           features: QuasselFeatures): IDccConfig.PortSelectionMode {
    return IDccConfig.PortSelectionMode.of(buffer.get())
  }
}
