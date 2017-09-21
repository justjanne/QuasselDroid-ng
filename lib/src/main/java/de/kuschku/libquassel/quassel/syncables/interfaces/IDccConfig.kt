package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.*
import java.net.InetAddress

@Syncable(name = "DccConfig")
interface IDccConfig : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun setDccEnabled(enabled: Boolean) {
    SYNC("setDccEnabled", ARG(enabled, Type.Bool))
  }

  @Slot
  fun setOutgoingIp(outgoingIp: InetAddress) {
    SYNC("setOutgoingIp", ARG(outgoingIp, QType.QHostAddress))
  }

  @Slot
  fun setIpDetectionMode(ipDetectionMode: IpDetectionMode) {
    SYNC("setIpDetectionMode", ARG(ipDetectionMode, QType.DccConfig_IpDetectionMode))
  }

  @Slot
  fun setPortSelectionMode(portSelectionMode: PortSelectionMode) {
    SYNC("setPortSelectionMode", ARG(portSelectionMode, QType.DccConfig_PortSelectionMode))
  }

  @Slot
  fun setMinPort(port: UShort) {
    SYNC("setMinPort", ARG(port, Type.UShort))
  }

  @Slot
  fun setMaxPort(port: UShort) {
    SYNC("setMaxPort", ARG(port, Type.UShort))
  }

  @Slot
  fun setChunkSize(chunkSize: Int) {
    SYNC("setChunkSize", ARG(chunkSize, Type.Int))
  }

  @Slot
  fun setSendTimeout(timeout: Int) {
    SYNC("setSendTimeout", ARG(timeout, Type.Int))
  }

  @Slot
  fun setUsePassiveDcc(use: Boolean) {
    SYNC("setUsePassiveDcc", ARG(use, Type.Bool))
  }

  @Slot
  fun setUseFastSend(use: Boolean) {
    SYNC("setUseFastSend", ARG(use, Type.Bool))
  }

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
