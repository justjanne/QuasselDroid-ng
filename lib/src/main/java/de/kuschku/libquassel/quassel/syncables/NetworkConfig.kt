package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.valueOr
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
    "pingTimeoutEnabled" to QVariant.of(pingTimeoutEnabled(), Type.Bool),
    "pingInterval" to QVariant.of(pingInterval(), Type.Int),
    "maxPingCount" to QVariant.of(maxPingCount(), Type.Int),
    "autoWhoEnabled" to QVariant.of(autoWhoEnabled(), Type.Bool),
    "autoWhoInterval" to QVariant.of(autoWhoInterval(), Type.Int),
    "autoWhoNickLimit" to QVariant.of(autoWhoNickLimit(), Type.Int),
    "autoWhoDelay" to QVariant.of(autoWhoDelay(), Type.Int),
    "standardCtcp" to QVariant.of(standardCtcp(), Type.Bool)
  )

  override fun initSetProperties(properties: QVariantMap) {
    setPingTimeoutEnabled(properties["pingTimeoutEnabled"].valueOr(this::pingTimeoutEnabled))
    setPingInterval(properties["pingInterval"].valueOr(this::pingInterval))
    setMaxPingCount(properties["maxPingCount"].valueOr(this::maxPingCount))
    setAutoWhoEnabled(properties["autoWhoEnabled"].valueOr(this::autoWhoEnabled))
    setAutoWhoInterval(properties["autoWhoInterval"].valueOr(this::autoWhoInterval))
    setAutoWhoNickLimit(properties["autoWhoNickLimit"].valueOr(this::autoWhoNickLimit))
    setAutoWhoDelay(properties["autoWhoDelay"].valueOr(this::autoWhoDelay))
    setStandardCtcp(properties["standardCtcp"].valueOr(this::standardCtcp))
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

  fun copy(): NetworkConfig {
    val config = NetworkConfig(SignalProxy.NULL)
    config.fromVariantMap(this.toVariantMap())
    return config
  }

  private var _pingTimeoutEnabled: Boolean = true
  private var _pingInterval: Int = 30
  private var _maxPingCount: Int = 6
  private var _autoWhoEnabled: Boolean = true
  private var _autoWhoInterval: Int = 90
  private var _autoWhoNickLimit: Int = 200
  private var _autoWhoDelay: Int = 5
  private var _standardCtcp: Boolean = false


  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as NetworkConfig

    if (_pingTimeoutEnabled != other._pingTimeoutEnabled) return false
    if (_pingInterval != other._pingInterval) return false
    if (_maxPingCount != other._maxPingCount) return false
    if (_autoWhoEnabled != other._autoWhoEnabled) return false
    if (_autoWhoInterval != other._autoWhoInterval) return false
    if (_autoWhoNickLimit != other._autoWhoNickLimit) return false
    if (_autoWhoDelay != other._autoWhoDelay) return false
    if (_standardCtcp != other._standardCtcp) return false

    return true
  }

  override fun hashCode(): Int {
    var result = _pingTimeoutEnabled.hashCode()
    result = 31 * result + _pingInterval
    result = 31 * result + _maxPingCount
    result = 31 * result + _autoWhoEnabled.hashCode()
    result = 31 * result + _autoWhoInterval
    result = 31 * result + _autoWhoNickLimit
    result = 31 * result + _autoWhoDelay
    result = 31 * result + _standardCtcp.hashCode()
    return result
  }

  override fun toString(): String {
    return "NetworkConfig(_pingTimeoutEnabled=$_pingTimeoutEnabled, _pingInterval=$_pingInterval, _maxPingCount=$_maxPingCount, _autoWhoEnabled=$_autoWhoEnabled, _autoWhoInterval=$_autoWhoInterval, _autoWhoNickLimit=$_autoWhoNickLimit, _autoWhoDelay=$_autoWhoDelay, _standardCtcp=$_standardCtcp)"
  }
}
