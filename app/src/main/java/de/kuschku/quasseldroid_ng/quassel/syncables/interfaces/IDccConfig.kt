package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.UByte
import de.kuschku.quasseldroid_ng.protocol.UShort
import java.net.InetAddress

@Syncable(name = "DccConfig")
interface IDccConfig : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun setDccEnabled(enabled: Boolean)

  @Slot
  fun setOutgoingIp(outgoingIp: InetAddress)

  @Slot
  fun setIpDetectionMode(ipDetectionMode: IpDetectionMode)

  @Slot
  fun setPortSelectionMode(portSelectionMode: PortSelectionMode)

  @Slot
  fun setMinPort(port: UShort)

  @Slot
  fun setMaxPort(port: UShort)

  @Slot
  fun setChunkSize(chunkSize: Int)

  @Slot
  fun setSendTimeout(timeout: Int)

  @Slot
  fun setUsePassiveDcc(use: Boolean)

  @Slot
  fun setUseFastSend(use: Boolean)

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }

  /**
   * Mode for detecting the outgoing IP
   */
  enum class IpDetectionMode(val value: UByte) {
    /** Automatic detection (network socket or USERHOST) */
    Automatic(0x00),
    /** Manually specified IP */
    Manual(0x01);

    companion object {
      private val byId = IpDetectionMode.values().associateBy(IpDetectionMode::value)
      fun of(value: UByte) = byId[value] ?: Automatic
    }
  }

  /**
   * Mode for selecting the port range for DCC
   */
  enum class PortSelectionMode(val value: UByte) {
    /** Automatic port selection */
    Automatic(0x00),
    /** Manually specified port range */
    Manual(0x01);

    companion object {
      private val byId = PortSelectionMode.values().associateBy(PortSelectionMode::value)
      fun of(value: UByte) = byId[value] ?: Automatic
    }
  }
}
