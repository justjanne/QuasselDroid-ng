package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type

@Syncable(name = "Identity")
interface IIdentity : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun copyFrom(other: IIdentity) {
    SYNC(SLOT, ARG(other, QType.Identity))
  }

  @Slot
  fun setAutoAwayEnabled(enabled: Boolean) {
    SYNC(SLOT, ARG(enabled, Type.Bool))
  }

  @Slot
  fun setAutoAwayReason(reason: String) {
    SYNC(SLOT, ARG(reason, Type.QString))
  }

  @Slot
  fun setAutoAwayReasonEnabled(enabled: Boolean) {
    SYNC(SLOT, ARG(enabled, Type.Bool))
  }

  @Slot
  fun setAutoAwayTime(time: Int) {
    SYNC(SLOT, ARG(time, Type.Int))
  }

  @Slot
  fun setAwayNick(awayNick: String) {
    SYNC(SLOT, ARG(awayNick, Type.QString))
  }

  @Slot
  fun setAwayNickEnabled(enabled: Boolean) {
    SYNC(SLOT, ARG(enabled, Type.Bool))
  }

  @Slot
  fun setAwayReason(awayReason: String) {
    SYNC(SLOT, ARG(awayReason, Type.QString))
  }

  @Slot
  fun setAwayReasonEnabled(enabled: Boolean) {
    SYNC(SLOT, ARG(enabled, Type.Bool))
  }

  @Slot
  fun setDetachAwayEnabled(enabled: Boolean) {
    SYNC(SLOT, ARG(enabled, Type.Bool))
  }

  @Slot
  fun setDetachAwayReason(reason: String) {
    SYNC(SLOT, ARG(reason, Type.QString))
  }

  @Slot
  fun setDetachAwayReasonEnabled(enabled: Boolean) {
    SYNC(SLOT, ARG(enabled, Type.Bool))
  }

  @Slot
  fun setId(id: IdentityId) {
    SYNC(SLOT, ARG(id, QType.IdentityId))
  }

  @Slot
  fun setIdent(ident: String) {
    SYNC(SLOT, ARG(ident, Type.QString))
  }

  @Slot
  fun setIdentityName(name: String) {
    SYNC(SLOT, ARG(name, Type.QString))
  }

  @Slot
  fun setKickReason(reason: String) {
    SYNC(SLOT, ARG(reason, Type.QString))
  }

  @Slot
  fun setNicks(nicks: QStringList) {
    SYNC(SLOT, ARG(nicks, Type.QStringList))
  }

  @Slot
  fun setPartReason(reason: String) {
    SYNC(SLOT, ARG(reason, Type.QString))
  }

  @Slot
  fun setQuitReason(reason: String) {
    SYNC(SLOT, ARG(reason, Type.QString))
  }

  @Slot
  fun setRealName(realName: String) {
    SYNC(SLOT, ARG(realName, Type.QString))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
