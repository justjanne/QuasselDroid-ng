package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.syncables.interfaces.IIdentity
import de.kuschku.libquassel.session.SignalProxy

class Identity constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "Identity"), IIdentity {
  override fun toVariantMap() = initProperties()
  override fun fromVariantMap(properties: QVariantMap) {
    initSetProperties(properties)
  }

  override fun init() {
    renameObject("${id()}")
  }

  override fun initProperties(): QVariantMap = mapOf(
    "identityId" to QVariant.of(id(), QType.IdentityId),
    "identityName" to QVariant.of(identityName(), Type.QString),
    "realName" to QVariant.of(realName(), Type.QString),
    "nicks" to QVariant.of(nicks(), Type.QStringList),
    "awayNick" to QVariant.of(awayNick(), Type.QString),
    "awayNickEnabled" to QVariant.of(awayNickEnabled(), Type.Bool),
    "awayReason" to QVariant.of(awayReason(), Type.QString),
    "awayReasonEnabled" to QVariant.of(awayReasonEnabled(), Type.Bool),
    "autoAwayEnabled" to QVariant.of(autoAwayEnabled(), Type.Bool),
    "autoAwayTime" to QVariant.of(autoAwayTime(), Type.Int),
    "autoAwayReason" to QVariant.of(autoAwayReason(), Type.QString),
    "autoAwayReasonEnabled" to QVariant.of(autoAwayReasonEnabled(), Type.Bool),
    "detachAwayEnabled" to QVariant.of(detachAwayEnabled(), Type.Bool),
    "detachAwayReason" to QVariant.of(detachAwayReason(), Type.QString),
    "detachAwayReasonEnabled" to QVariant.of(detachAwayReasonEnabled(), Type.Bool),
    "ident" to QVariant.of(ident(), Type.QString),
    "kickReason" to QVariant.of(kickReason(), Type.QString),
    "partReason" to QVariant.of(partReason(), Type.QString),
    "quitReason" to QVariant.of(quitReason(), Type.QString)
  )

  override fun initSetProperties(properties: QVariantMap) {
    setId(properties["identityId"].value(id()))
    setIdentityName(properties["identityName"].value(identityName()))
    setRealName(properties["realName"].valueOr(this::realName))
    setNicks(properties["nicks"].valueOr(this::nicks))
    setAwayNick(properties["awayNick"].valueOr(this::awayNick))
    setAwayNickEnabled(properties["awayNickEnabled"].valueOr(this::awayNickEnabled))
    setAwayReason(properties["awayReason"].valueOr(this::awayReason))
    setAwayReasonEnabled(properties["awayReasonEnabled"].valueOr(this::awayReasonEnabled))
    setAutoAwayEnabled(properties["autoAwayEnabled"].valueOr(this::autoAwayEnabled))
    setAutoAwayTime(properties["autoAwayTime"].valueOr(this::autoAwayTime))
    setAutoAwayReason(properties["autoAwayReason"].valueOr(this::autoAwayReason))
    setAutoAwayReasonEnabled(
      properties["autoAwayReasonEnabled"].valueOr(this::autoAwayReasonEnabled)
    )
    setDetachAwayEnabled(properties["detachAwayEnabled"].valueOr(this::detachAwayEnabled))
    setDetachAwayReason(properties["detachAwayReason"].valueOr(this::detachAwayReason))
    setDetachAwayReasonEnabled(
      properties["detachAwayReasonEnabled"].valueOr(this::detachAwayReasonEnabled)
    )
    setIdent(properties["ident"].valueOr(this::ident))
    setKickReason(properties["kickReason"].valueOr(this::kickReason))
    setPartReason(properties["partReason"].valueOr(this::partReason))
    setQuitReason(properties["quitReason"].valueOr(this::quitReason))
  }

  fun id() = _identityId
  fun identityName() = _identityName
  fun realName() = _realName
  fun nicks() = _nicks
  fun awayNick() = _awayNick
  fun awayNickEnabled() = _awayNickEnabled
  fun awayReason() = _awayReason
  fun awayReasonEnabled() = _awayReasonEnabled
  fun autoAwayEnabled() = _autoAwayEnabled
  fun autoAwayTime() = _autoAwayTime
  fun autoAwayReason() = _autoAwayReason
  fun autoAwayReasonEnabled() = _autoAwayReasonEnabled
  fun detachAwayEnabled() = _detachAwayEnabled
  fun detachAwayReason() = _detachAwayReason
  fun detachAwayReasonEnabled() = _detachAwayReasonEnabled
  fun ident() = _ident
  fun kickReason() = _kickReason
  fun partReason() = _partReason
  fun quitReason() = _quitReason

  override fun setAutoAwayEnabled(enabled: Boolean) {
    _autoAwayEnabled = enabled
    super.setAutoAwayEnabled(enabled)
  }

  override fun setAutoAwayReason(reason: String) {
    _autoAwayReason = reason
    super.setAutoAwayReason(reason)
  }

  override fun setAutoAwayReasonEnabled(enabled: Boolean) {
    _autoAwayReasonEnabled = enabled
    super.setAutoAwayReasonEnabled(enabled)
  }

  override fun setAutoAwayTime(time: Int) {
    _autoAwayTime = time
    super.setAutoAwayTime(time)
  }

  override fun setAwayNick(awayNick: String) {
    _awayNick = awayNick
    super.setAwayNick(awayNick)
  }

  override fun setAwayNickEnabled(enabled: Boolean) {
    _awayNickEnabled = enabled
    super.setAwayNickEnabled(enabled)
  }

  override fun setAwayReason(awayReason: String) {
    _awayReason = awayReason
    super.setAwayReason(awayReason)
  }

  override fun setAwayReasonEnabled(enabled: Boolean) {
    _awayReasonEnabled = enabled
    super.setAwayReasonEnabled(enabled)
  }

  override fun setDetachAwayEnabled(enabled: Boolean) {
    _detachAwayEnabled = enabled
    super.setDetachAwayEnabled(enabled)
  }

  override fun setDetachAwayReason(reason: String) {
    _detachAwayReason = reason
    super.setDetachAwayReason(reason)
  }

  override fun setDetachAwayReasonEnabled(enabled: Boolean) {
    _detachAwayReasonEnabled = enabled
    super.setDetachAwayReasonEnabled(enabled)
  }

  override fun setId(id: IdentityId) {
    _identityId = id
    super.setId(id)
  }

  override fun setIdent(ident: String) {
    _ident = ident
    super.setIdent(ident)
  }

  override fun setIdentityName(name: String) {
    _identityName = name
    super.setIdentityName(name)
  }

  override fun setKickReason(reason: String) {
    _kickReason = reason
    super.setKickReason(reason)
  }

  override fun setNicks(nicks: QStringList) {
    _nicks = nicks.filterNotNull().toMutableList()
    super.setNicks(nicks)
  }

  override fun setPartReason(reason: String) {
    _partReason = reason
    super.setPartReason(reason)
  }

  override fun setQuitReason(reason: String) {
    _quitReason = reason
    super.setQuitReason(reason)
  }

  override fun setRealName(realName: String) {
    _realName = realName
    super.setRealName(realName)
  }

  private var _identityId: IdentityId = -1
  private var _identityName: String = "<isEmpty>"
  private var _realName: String = ""
  private var _nicks: MutableList<String> = mutableListOf("quassel")
  private var _awayNick: String = ""
  private var _awayNickEnabled: Boolean = false
  private var _awayReason: String = "Gone fishing."
  private var _awayReasonEnabled: Boolean = true
  private var _autoAwayEnabled: Boolean = false
  private var _autoAwayTime: Int = 10
  private var _autoAwayReason: String = "Not here. No, really. not here!"
  private var _autoAwayReasonEnabled: Boolean = false
  private var _detachAwayEnabled: Boolean = false
  private var _detachAwayReason: String = "All Quassel clients vanished from the face of the earth..."
  private var _detachAwayReasonEnabled: Boolean = false
  private var _ident: String = "quassel"
  private var _kickReason: String = "Kindergarten is elsewhere!"
  private var _partReason: String = "http://quassel-irc.org - Chat comfortably. Anywhere."
  private var _quitReason: String = "http://quassel-irc.org - Chat comfortably. Anywhere."
}
