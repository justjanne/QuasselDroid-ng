/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.syncables.interfaces.IDccConfig
import de.kuschku.libquassel.session.SignalProxy
import java.net.Inet4Address
import java.net.InetAddress

class DccConfig constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "DccConfig"), IDccConfig {
  override fun init() {
    renameObject("DccConfig")
  }

  override fun toVariantMap() = initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetProperties(properties)
  }

  override fun initProperties(): QVariantMap = mapOf(
    /// Whether DCC is enabled
    "dccEnabled" to QVariant.of(isDccEnabled(), Type.Bool),
    /// The IP to use for outgoing traffic
    "outgoingIp" to QVariant.of(outgoingIp(), QType.QHostAddress),
    /// The IP detection mode
    "ipDetectionMode" to QVariant.of(ipDetectionMode(), QType.DccConfig_IpDetectionMode),
    /// The port range selection mode
    "portSelectionMode" to QVariant.of(portSelectionMode(), QType.DccConfig_PortSelectionMode),
    /// Minimum port to use for incoming connections
    "minPort" to QVariant.of(minPort(), Type.UShort),
    /// Maximum port to use for incoming connections
    "maxPort" to QVariant.of(maxPort(), Type.UShort),
    /// The chunk size to be used
    "chunkSize" to QVariant.of(chunkSize(), Type.Int),
    /// The timeout for DCC transfers
    "sendTimeout" to QVariant.of(sendTimeout(), Type.Int),
    /// Whether passive (reverse) DCC should be used
    "usePassiveDcc" to QVariant.of(usePassiveDcc(), Type.Bool),
    /// Whether fast sending should be used
    "useFastSend" to QVariant.of(useFastSend(), Type.Bool)
  )

  override fun initSetProperties(properties: QVariantMap) {
    /// Whether DCC is enabled
    setDccEnabled(properties["dccEnabled"].valueOr(this::isDccEnabled))
    /// The IP to use for outgoing traffic
    setOutgoingIp(properties["outgoingIp"].valueOr(this::outgoingIp))
    /// The IP detection mode
    setIpDetectionMode(properties["ipDetectionMode"].valueOr(this::ipDetectionMode))
    /// The port range selection mode
    setPortSelectionMode(properties["portSelectionMode"].valueOr(this::portSelectionMode))
    /// Minimum port to use for incoming connections
    setMinPort(properties["minPort"].valueOr(this::minPort))
    /// Maximum port to use for incoming connections
    setMaxPort(properties["maxPort"].valueOr(this::maxPort))
    /// The chunk size to be used
    setChunkSize(properties["chunkSize"].valueOr(this::chunkSize))
    /// The timeout for DCC transfers
    setSendTimeout(properties["sendTimeout"].valueOr(this::sendTimeout))
    /// Whether passive (reverse) DCC should be used
    setUsePassiveDcc(properties["usePassiveDcc"].valueOr(this::usePassiveDcc))
    /// Whether fast sending should be used
    setUseFastSend(properties["useFastSend"].valueOr(this::useFastSend))
  }

  override fun setDccEnabled(enabled: Boolean) {
    _dccEnabled = enabled
  }

  override fun setOutgoingIp(outgoingIp: InetAddress) {
    _outgoingIp = outgoingIp
  }

  override fun setIpDetectionMode(ipDetectionMode: IDccConfig.IpDetectionMode) {
    _ipDetectionMode = ipDetectionMode
  }

  override fun setPortSelectionMode(portSelectionMode: IDccConfig.PortSelectionMode) {
    _portSelectionMode = portSelectionMode
  }

  override fun setMinPort(port: UShort) {
    _minPort = port
  }

  override fun setMaxPort(port: UShort) {
    _maxPort = port
  }

  override fun setChunkSize(chunkSize: Int) {
    _chunkSize = chunkSize
  }

  override fun setSendTimeout(timeout: Int) {
    _sendTimeout = timeout
  }

  override fun setUsePassiveDcc(use: Boolean) {
    _usePassiveDcc = use
  }

  override fun setUseFastSend(use: Boolean) {
    _useFastSend = use
  }

  fun isDccEnabled() = _dccEnabled
  fun outgoingIp() = _outgoingIp
  fun ipDetectionMode() = _ipDetectionMode
  fun portSelectionMode() = _portSelectionMode
  fun minPort() = _minPort
  fun maxPort() = _maxPort
  fun chunkSize() = _chunkSize
  fun sendTimeout() = _sendTimeout
  fun usePassiveDcc() = _usePassiveDcc
  fun useFastSend() = _useFastSend

  /**  Whether DCC is enabled */
  private var _dccEnabled: Boolean = false

  /**  The IP to use for outgoing traffic */
  private var _outgoingIp: InetAddress = Inet4Address.getByAddress(byteArrayOf(127, 0, 0, 1))

  /**  The IP detection mode */
  private var _ipDetectionMode: IDccConfig.IpDetectionMode = IDccConfig.IpDetectionMode.Automatic

  /**  The port range selection mode */
  private var _portSelectionMode: IDccConfig.PortSelectionMode = IDccConfig.PortSelectionMode.Automatic

  /**  Minimum port to use for incoming connections */
  private var _minPort: UShort = 1024

  /**  Maximum port to use for incoming connections */
  private var _maxPort: UShort = 32767

  /**  The chunk size to be used */
  private var _chunkSize: Int = 16

  /**  The timeout for DCC transfers */
  private var _sendTimeout: Int = 180

  /**  Whether passive (reverse) DCC should be used */
  private var _usePassiveDcc: Boolean = false

  /**  Whether fast sending should be used */
  private var _useFastSend: Boolean = false
}
