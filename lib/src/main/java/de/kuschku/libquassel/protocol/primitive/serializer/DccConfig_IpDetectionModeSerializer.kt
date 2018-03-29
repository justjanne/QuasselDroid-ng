package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.quassel.syncables.interfaces.IDccConfig
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object DccConfig_IpDetectionModeSerializer : Serializer<IDccConfig.IpDetectionMode> {
  override fun serialize(buffer: ChainedByteBuffer, data: IDccConfig.IpDetectionMode,
                         features: QuasselFeatures) {
    buffer.put(data.value)
  }

  override fun deserialize(buffer: ByteBuffer,
                           features: QuasselFeatures): IDccConfig.IpDetectionMode {
    return IDccConfig.IpDetectionMode.of(buffer.get())
  }
}
