package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.ARG
import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.SLOT
import de.kuschku.quasseldroid_ng.protocol.Type

@Syncable(name = "NetworkConfig")
interface INetworkConfig : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun requestSetAutoWhoDelay(i: Int) {
    REQUEST(SLOT, ARG(i, Type.Int))
  }

  @Slot
  fun requestSetAutoWhoEnabled(b: Boolean) {
    REQUEST(SLOT, ARG(b, Type.Bool))
  }

  @Slot
  fun requestSetAutoWhoInterval(i: Int) {
    REQUEST(SLOT, ARG(i, Type.Int))
  }

  @Slot
  fun requestSetAutoWhoNickLimit(i: Int) {
    REQUEST(SLOT, ARG(i, Type.Int))
  }

  @Slot
  fun requestSetMaxPingCount(i: Int) {
    REQUEST(SLOT, ARG(i, Type.Int))
  }

  @Slot
  fun requestSetPingInterval(i: Int) {
    REQUEST(SLOT, ARG(i, Type.Int))
  }

  @Slot
  fun requestSetPingTimeoutEnabled(b: Boolean) {
    REQUEST(SLOT, ARG(b, Type.Bool))
  }

  @Slot
  fun requestSetStandardCtcp(b: Boolean) {
    REQUEST(SLOT, ARG(b, Type.Bool))
  }

  @Slot
  fun setAutoWhoDelay(delay: Int) {
    SYNC(SLOT, ARG(delay, Type.Int))
  }

  @Slot
  fun setAutoWhoEnabled(enabled: Boolean) {
    SYNC(SLOT, ARG(enabled, Type.Bool))
  }

  @Slot
  fun setAutoWhoInterval(interval: Int) {
    SYNC(SLOT, ARG(interval, Type.Int))
  }

  @Slot
  fun setAutoWhoNickLimit(limit: Int) {
    SYNC(SLOT, ARG(limit, Type.Int))
  }

  @Slot
  fun setMaxPingCount(count: Int) {
    SYNC(SLOT, ARG(count, Type.Int))
  }

  @Slot
  fun setPingInterval(interval: Int) {
    SYNC(SLOT, ARG(interval, Type.Int))
  }

  @Slot
  fun setPingTimeoutEnabled(enabled: Boolean) {
    SYNC(SLOT, ARG(enabled, Type.Bool))
  }

  @Slot
  fun setStandardCtcp(standardCtcp: Boolean) {
    SYNC(SLOT, ARG(standardCtcp, Type.Bool))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
