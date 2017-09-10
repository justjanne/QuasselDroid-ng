package de.kuschku.quasseldroid_ng.protocol.primitive.serializer

import de.kuschku.quasseldroid_ng.protocol.Quassel_Features
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.IDccConfig
import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
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
