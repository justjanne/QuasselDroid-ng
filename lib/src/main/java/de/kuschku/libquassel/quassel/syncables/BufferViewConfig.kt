package de.kuschku.libquassel.quassel.syncables

import clamp
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.quassel.syncables.interfaces.IBufferViewConfig
import de.kuschku.libquassel.session.SignalProxy

class BufferViewConfig constructor(
  bufferViewId: Int,
  proxy: SignalProxy
) : SyncableObject(proxy, "BufferViewConfig"), IBufferViewConfig {
  override fun init() {
    renameObject("$_bufferViewId")
  }

  override fun toVariantMap(): QVariantMap = mapOf(
    "BufferList" to QVariant_(initBufferList(), Type.QVariantList),
    "RemovedBuffers" to QVariant_(initBufferList(), Type.QVariantList),
    "TemporarilyRemovedBuffers" to QVariant_(initBufferList(), Type.QVariantList)
  ) + initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetBufferList(properties["BufferList"].valueOr(::emptyList))
    initSetRemovedBuffers(properties["RemovedBuffers"].valueOr(::emptyList))
    initSetTemporarilyRemovedBuffers(properties["TemporarilyRemovedBuffers"].valueOr(::emptyList))
    initSetProperties(properties)
  }

  override fun initBufferList(): QVariantList = _buffers.map {
    QVariant_(it, QType.BufferId)
  }

  override fun initRemovedBuffers(): QVariantList = _removedBuffers.map {
    QVariant_(it, QType.BufferId)
  }

  override fun initTemporarilyRemovedBuffers(): QVariantList = _temporarilyRemovedBuffers.map {
    QVariant_(it, QType.BufferId)
  }

  override fun initProperties(): QVariantMap = mapOf(
    "bufferViewName" to QVariant_(bufferViewName(), Type.QString),
    "networkId" to QVariant_(networkId(), QType.NetworkId),
    "addNewBuffersAutomatically" to QVariant_(addNewBuffersAutomatically(), Type.Bool),
    "sortAlphabetically" to QVariant_(sortAlphabetically(), Type.Bool),
    "hideInactiveBuffers" to QVariant_(hideInactiveBuffers(), Type.Bool),
    "hideInactiveNetworks" to QVariant_(hideInactiveNetworks(), Type.Bool),
    "disableDecoration" to QVariant_(disableDecoration(), Type.Bool),
    "allowedBufferTypes" to QVariant_(allowedBufferTypes(), Type.Int),
    "minimumActivity" to QVariant_(minimumActivity(), Type.Int),
    "showSearch" to QVariant_(showSearch(), Type.Bool)
  )

  override fun initSetBufferList(buffers: QVariantList) {
    _buffers = buffers.mapNotNull { it.value<BufferId?>() }.toMutableList()
  }

  override fun initSetRemovedBuffers(buffers: QVariantList) {
    _removedBuffers = buffers.mapNotNull { it.value<BufferId?>() }.toMutableSet()
  }

  override fun initSetTemporarilyRemovedBuffers(buffers: QVariantList) {
    _temporarilyRemovedBuffers = buffers.mapNotNull { it.value<BufferId?>() }.toMutableSet()
  }

  override fun initSetProperties(properties: QVariantMap) {
    setBufferViewName(properties["bufferViewName"].value(bufferViewName()))
    setNetworkId(properties["networkId"].value(networkId()))
    setAddNewBuffersAutomatically(
      properties["addNewBuffersAutomatically"].value(addNewBuffersAutomatically()))
    setSortAlphabetically(properties["sortAlphabetically"].value(sortAlphabetically()))
    setHideInactiveBuffers(properties["hideInactiveBuffers"].value(hideInactiveBuffers()))
    setHideInactiveNetworks(properties["hideInactiveNetworks"].value(hideInactiveNetworks()))
    setDisableDecoration(properties["disableDecoration"].value(disableDecoration()))
    setAllowedBufferTypes(properties["allowedBufferTypes"].value(allowedBufferTypes().toInt()))
    setMinimumActivity(properties["minimumActivity"].value(minimumActivity().toInt()))
    setShowSearch(properties["showSearch"].value(showSearch()))
  }

  override fun addBuffer(bufferId: BufferId, pos: Int) {
    if (_buffers.contains(bufferId))
      return

    if (_removedBuffers.contains(bufferId))
      _removedBuffers.remove(bufferId)

    if (_temporarilyRemovedBuffers.contains(bufferId))
      _temporarilyRemovedBuffers.remove(bufferId)

    _buffers.add(minOf(maxOf(pos, 0), _buffers.size), bufferId)
  }

  override fun moveBuffer(bufferId: BufferId, pos: Int) {
    if (!_buffers.contains(bufferId))
      return

    val currentPos = _buffers.indexOf(bufferId)
    val targetPos = pos.clamp(0, _buffers.size - 1)

    if (currentPos > targetPos) {
      _buffers.removeAt(currentPos)
      _buffers.add(bufferId, targetPos)
    }

    if (currentPos < targetPos) {
      _buffers.removeAt(currentPos)
      _buffers.add(bufferId, targetPos - 1)
    }
  }

  override fun removeBuffer(bufferId: BufferId) {
    if (_buffers.contains(bufferId))
      _buffers.remove(bufferId)

    if (_removedBuffers.contains(bufferId))
      _removedBuffers.remove(bufferId)

    _temporarilyRemovedBuffers.add(bufferId)
  }

  override fun removeBufferPermanently(bufferId: BufferId) {
    if (_buffers.contains(bufferId))
      _buffers.remove(bufferId)

    if (_temporarilyRemovedBuffers.contains(bufferId))
      _temporarilyRemovedBuffers.remove(bufferId);

    _removedBuffers.add(bufferId)
  }

  fun bufferViewId() = _bufferViewId
  fun bufferViewName() = _bufferViewName
  fun networkId() = _networkId
  fun addNewBuffersAutomatically() = _addNewBuffersAutomatically
  fun sortAlphabetically() = _sortAlphabetically
  fun hideInactiveBuffers() = _hideInactiveBuffers
  fun hideInactiveNetworks() = _hideInactiveNetworks
  fun disableDecoration() = _disableDecoration
  fun allowedBufferTypes() = _allowedBufferTypes
  fun minimumActivity() = _minimumActivity
  fun showSearch() = _showSearch


  override fun setAddNewBuffersAutomatically(addNewBuffersAutomatically: Boolean) {
    _addNewBuffersAutomatically = addNewBuffersAutomatically
    super.setAddNewBuffersAutomatically(addNewBuffersAutomatically)
  }

  override fun setAllowedBufferTypes(bufferTypes: Int) {
    _allowedBufferTypes = Buffer_Type.of(bufferTypes.toShort())
    super.setAllowedBufferTypes(bufferTypes)
  }

  override fun setBufferViewName(bufferViewName: String) {
    _bufferViewName = bufferViewName
    super.setBufferViewName(bufferViewName)
  }

  override fun setDisableDecoration(disableDecoration: Boolean) {
    _disableDecoration = disableDecoration
    super.setDisableDecoration(disableDecoration)
  }

  override fun setHideInactiveBuffers(hideInactiveBuffers: Boolean) {
    _hideInactiveBuffers = hideInactiveBuffers
    super.setHideInactiveBuffers(hideInactiveBuffers)
  }

  override fun setHideInactiveNetworks(hideInactiveNetworks: Boolean) {
    _hideInactiveNetworks = hideInactiveNetworks
    super.setHideInactiveNetworks(hideInactiveNetworks)
  }

  override fun setMinimumActivity(activity: Int) {
    _minimumActivity = Buffer_Activity.of(activity)
    super.setMinimumActivity(activity)
  }

  override fun setNetworkId(networkId: NetworkId) {
    _networkId = networkId
    super.setNetworkId(networkId)
  }

  override fun setShowSearch(showSearch: Boolean) {
    _showSearch = showSearch
    super.setShowSearch(showSearch)
  }

  override fun setSortAlphabetically(sortAlphabetically: Boolean) {
    _sortAlphabetically = sortAlphabetically
    super.setSortAlphabetically(sortAlphabetically)
  }

  val _bufferViewId: Int = bufferViewId
  var _bufferViewName: String = ""
  var _networkId: NetworkId = 0
  var _addNewBuffersAutomatically: Boolean = true
  var _sortAlphabetically: Boolean = true
  var _hideInactiveBuffers: Boolean = false
  var _hideInactiveNetworks: Boolean = false
  var _disableDecoration: Boolean = false
  var _allowedBufferTypes: Buffer_Types = Buffer_Type.of(*Buffer_Type.validValues)
  var _minimumActivity: Buffer_Activities = Buffer_Activities.of(0)
  var _showSearch: Boolean = false
  var _buffers: MutableList<BufferId> = mutableListOf()
  var _removedBuffers: MutableSet<BufferId> = mutableSetOf()
  var _temporarilyRemovedBuffers: MutableSet<BufferId> = mutableSetOf()
}
