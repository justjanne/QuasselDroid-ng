/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.libquassel.quassel.syncables.interfaces

import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.annotations.SyncedCall
import de.justjanne.libquassel.annotations.SyncedObject
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.QuasselType
import de.kuschku.libquassel.protocol.qVariant
import java.net.InetAddress

@SyncedObject(name = "DccConfig")
interface IDccConfig : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)
  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setDccEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setDccEnabled",
      qVariant(enabled, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setOutgoingIp(outgoingIp: InetAddress) {
    sync(
      target = ProtocolSide.CLIENT,
      "setOutgoingIp",
      qVariant(outgoingIp, QuasselType.QHostAddress),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setIpDetectionMode(ipDetectionMode: IpDetectionMode) {
    sync(
      target = ProtocolSide.CLIENT,
      "setIpDetectionMode",
      qVariant(ipDetectionMode, QuasselType.DccConfig_IpDetectionMode),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setPortSelectionMode(portSelectionMode: PortSelectionMode) {
    sync(
      target = ProtocolSide.CLIENT,
      "setPortSelectionMode",
      qVariant(portSelectionMode, QuasselType.DccConfig_PortSelectionMode),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setMinPort(port: UShort) {
    sync(
      target = ProtocolSide.CLIENT,
      "setMinPort",
      qVariant(port, QtType.UShort),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setMaxPort(port: UShort) {
    sync(
      target = ProtocolSide.CLIENT,
      "setMaxPort",
      qVariant(port, QtType.UShort),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setChunkSize(chunkSize: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setChunkSize",
      qVariant(chunkSize, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setSendTimeout(timeout: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setSendTimeout",
      qVariant(timeout, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUsePassiveDcc(use: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUsePassiveDcc",
      qVariant(use, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setUseFastSend(use: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setUseFastSend",
      qVariant(use, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)

  /**
   * Mode for detecting the outgoing IP
   */
  enum class IpDetectionMode(val value: UByte) {
    /** Automatic detection (network socket or USERHOST) */
    Automatic(0x00u),
    /** Manually specified IP */
    Manual(0x01u);

    companion object {
      private val byId = values().associateBy(IpDetectionMode::value)
      fun of(value: UByte) = byId[value] ?: Automatic
    }
  }

  /**
   * Mode for selecting the port range for DCC
   */
  enum class PortSelectionMode(val value: UByte) {
    /** Automatic port selection */
    Automatic(0x00u),
    /** Manually specified port range */
    Manual(0x01u);

    companion object {
      private val byId = values().associateBy(PortSelectionMode::value)
      fun of(value: UByte) = byId[value] ?: Automatic
    }
  }
}
