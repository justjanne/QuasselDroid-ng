package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type

@Syncable(name = "BufferViewConfig")
interface IBufferViewConfig : ISyncableObject {
  fun initBufferList(): QVariantList
  fun initRemovedBuffers(): QVariantList
  fun initTemporarilyRemovedBuffers(): QVariantList
  fun initSetBufferList(buffers: QVariantList)
  fun initSetRemovedBuffers(buffers: QVariantList)
  fun initSetTemporarilyRemovedBuffers(buffers: QVariantList)

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun addBuffer(bufferId: BufferId, pos: Int)

  @Slot
  fun moveBuffer(bufferId: BufferId, pos: Int)

  @Slot
  fun removeBuffer(bufferId: BufferId)

  @Slot
  fun removeBufferPermanently(bufferId: BufferId)

  @Slot
  fun requestAddBuffer(bufferId: BufferId, pos: Int) {
    REQUEST("requestAddBuffer", ARG(bufferId, QType.BufferId), ARG(pos, Type.Int))
  }

  @Slot
  fun requestMoveBuffer(bufferId: BufferId, pos: Int) {
    REQUEST("requestMoveBuffer", ARG(bufferId, QType.BufferId), ARG(pos, Type.Int))
  }

  @Slot
  fun requestRemoveBuffer(bufferId: BufferId) {
    REQUEST("requestRemoveBuffer", ARG(bufferId, QType.BufferId))
  }

  @Slot
  fun requestRemoveBufferPermanently(bufferId: BufferId) {
    REQUEST("requestRemoveBufferPermanently", ARG(bufferId, QType.BufferId))
  }

  @Slot
  fun requestSetBufferViewName(bufferViewName: String) {
    REQUEST("requestSetBufferViewName", ARG(bufferViewName, Type.QString))
  }

  @Slot
  fun setAddNewBuffersAutomatically(addNewBuffersAutomatically: Boolean) {
    SYNC("setAddNewBuffersAutomatically", ARG(addNewBuffersAutomatically, Type.Bool))
  }

  @Slot
  fun setAllowedBufferTypes(bufferTypes: Int) {
    SYNC("setAllowedBufferTypes", ARG(bufferTypes, Type.Int))
  }

  @Slot
  fun setBufferViewName(bufferViewName: String) {
    SYNC("setBufferViewName", ARG(bufferViewName, Type.QString))
  }

  @Slot
  fun setDisableDecoration(disableDecoration: Boolean) {
    SYNC("setDisableDecoration", ARG(disableDecoration, Type.Bool))
  }

  @Slot
  fun setHideInactiveBuffers(hideInactiveBuffers: Boolean) {
    SYNC("setHideInactiveBuffers", ARG(hideInactiveBuffers, Type.Bool))
  }

  @Slot
  fun setHideInactiveNetworks(hideInactiveNetworks: Boolean) {
    SYNC("setHideInactiveNetworks", ARG(hideInactiveNetworks, Type.Bool))
  }

  @Slot
  fun setMinimumActivity(activity: Int) {
    SYNC("setMinimumActivity", ARG(activity, Type.Int))
  }

  @Slot
  fun setNetworkId(networkId: NetworkId) {
    SYNC("setNetworkId", ARG(networkId, QType.NetworkId))
  }

  @Slot
  fun setShowSearch(showSearch: Boolean) {
    SYNC("setShowSearch", ARG(showSearch, Type.Bool))
  }

  @Slot
  fun setSortAlphabetically(sortAlphabetically: Boolean) {
    SYNC("setSortAlphabetically", ARG(sortAlphabetically, Type.Bool))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
