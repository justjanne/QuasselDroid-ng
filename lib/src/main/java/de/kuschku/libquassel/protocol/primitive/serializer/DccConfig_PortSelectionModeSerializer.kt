package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.quassel.syncables.interfaces.IDccConfig
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object DccConfig_PortSelectionModeSerializer : Serializer<IDccConfig.PortSelectionMode> {
  override fun serialize(buffer: ChainedByteBuffer, data: IDccConfig.PortSelectionMode,
                         features: Quassel_Features) {
    buffer.put(data.value)
  }

  override fun deserialize(buffer: ByteBuffer,
                           features: Quassel_Features): IDccConfig.PortSelectionMode {
    return IDccConfig.PortSelectionMode.of(buffer.get())
  }
}
