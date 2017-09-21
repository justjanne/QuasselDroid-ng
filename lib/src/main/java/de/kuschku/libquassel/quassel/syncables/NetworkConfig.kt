package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.quassel.syncables.interfaces.INetworkConfig
import de.kuschku.libquassel.session.SignalProxy

class NetworkConfig constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "NetworkConfig"), INetworkConfig {
  override fun init() {
    renameObject("GlobalNetworkConfig")
  }

  override fun toVariantMap() = initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetProperties(properties)
  }

  override fun initProperties(): QVariantMap = mapOf(
    "pingTimeoutEnabled" to QVariant_(pingTimeoutEnabled(), Type.Bool),
    "pingInterval" to QVariant_(pingInterval(), Type.Int),
    "maxPingCount" to QVariant_(maxPingCount(), Type.Int),
    "autoWhoEnabled" to QVariant_(autoWhoEnabled(), Type.Bool),
    "autoWhoInterval" to QVariant_(autoWhoInterval(), Type.Int),
    "autoWhoNickLimit" to QVariant_(autoWhoNickLimit(), Type.Int),
    "autoWhoDelay" to QVariant_(autoWhoDelay(), Type.Int),
    "standardCtcp" to QVariant_(standardCtcp(), Type.Bool)
  )

  override fun initSetProperties(properties: QVariantMap) {
    setPingTimeoutEnabled(properties["pingTimeoutEnabled"].value(pingTimeoutEnabled()))
    setPingInterval(properties["pingInterval"].value(pingInterval()))
    setMaxPingCount(properties["maxPingCount"].value(maxPingCount()))
    setAutoWhoEnabled(properties["autoWhoEnabled"].value(autoWhoEnabled()))
    setAutoWhoInterval(properties["autoWhoInterval"].value(autoWhoInterval()))
    setAutoWhoNickLimit(properties["autoWhoNickLimit"].value(autoWhoNickLimit()))
    setAutoWhoDelay(properties["autoWhoDelay"].value(autoWhoDelay()))
    setStandardCtcp(properties["standardCtcp"].value(standardCtcp()))
  }

  fun pingTimeoutEnabled() = _pingTimeoutEnabled
  fun pingInterval() = _pingInterval
  fun maxPingCount() = _maxPingCount
  fun autoWhoEnabled() = _autoWhoEnabled
  fun autoWhoInterval() = _autoWhoInterval
  fun autoWhoNickLimit() = _autoWhoNickLimit
  fun autoWhoDelay() = _autoWhoDelay
  fun standardCtcp() = _standardCtcp

  override fun setPingTimeoutEnabled(enabled: Boolean) {
    _pingTimeoutEnabled = enabled
    super.setPingTimeoutEnabled(enabled)
  }

  override fun setPingInterval(interval: Int) {
    _pingInterval = interval
    super.setPingInterval(interval)
  }

  override fun setMaxPingCount(count: Int) {
    _maxPingCount = count
    super.setMaxPingCount(count)
  }

  override fun setAutoWhoEnabled(enabled: Boolean) {
    _autoWhoEnabled = enabled
    super.setAutoWhoEnabled(enabled)
  }

  override fun setAutoWhoInterval(interval: Int) {
    _autoWhoInterval = interval
    super.setAutoWhoInterval(interval)
  }

  override fun setAutoWhoNickLimit(limit: Int) {
    _autoWhoNickLimit = limit
    super.setAutoWhoNickLimit(limit)
  }

  override fun setAutoWhoDelay(delay: Int) {
    _autoWhoDelay = delay
    super.setAutoWhoDelay(delay)
  }

  override fun setStandardCtcp(standardCtcp: Boolean) {
    _standardCtcp = standardCtcp
    super.setStandardCtcp(standardCtcp)
  }

  private var _pingTimeoutEnabled: Boolean = true
  private var _pingInterval: Int = 30
  private var _maxPingCount: Int = 6
  private var _autoWhoEnabled: Boolean = true
  private var _autoWhoInterval: Int = 90
  private var _autoWhoNickLimit: Int = 200
  private var _autoWhoDelay: Int = 5
  private var _standardCtcp: Boolean = false
}
