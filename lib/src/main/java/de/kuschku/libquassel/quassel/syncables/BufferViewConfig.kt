package de.kuschku.libquassel.quassel.syncables

import clamp
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.IBufferViewConfig
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.flag.hasFlag
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class BufferViewConfig constructor(
  bufferViewId: Int,
  proxy: SignalProxy
) : SyncableObject(proxy, "BufferViewConfig"), IBufferViewConfig {
  override fun init() {
    renameObject("$_bufferViewId")
  }

  override fun toVariantMap(): QVariantMap = mapOf(
    "BufferList" to QVariant.of(initBufferList(), Type.QVariantList),
    "RemovedBuffers" to QVariant.of(initRemovedBuffers(), Type.QVariantList),
    "TemporarilyRemovedBuffers" to QVariant.of(initTemporarilyRemovedBuffers(), Type.QVariantList)
  ) + initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetBufferList(properties["BufferList"].valueOr(::emptyList))
    initSetRemovedBuffers(properties["RemovedBuffers"].valueOr(::emptyList))
    initSetTemporarilyRemovedBuffers(properties["TemporarilyRemovedBuffers"].valueOr(::emptyList))
    initSetProperties(properties)
  }

  override fun initBufferList(): QVariantList = _buffers.map {
    QVariant.of(it, QType.BufferId)
  }

  override fun initRemovedBuffers(): QVariantList = _removedBuffers.map {
    QVariant.of(it, QType.BufferId)
  }

  override fun initTemporarilyRemovedBuffers(): QVariantList = _temporarilyRemovedBuffers.map {
    QVariant.of(it, QType.BufferId)
  }

  override fun initProperties(): QVariantMap = mapOf(
    "bufferViewName" to QVariant.of(bufferViewName(), Type.QString),
    "networkId" to QVariant.of(networkId(), QType.NetworkId),
    "addNewBuffersAutomatically" to QVariant.of(addNewBuffersAutomatically(), Type.Bool),
    "sortAlphabetically" to QVariant.of(sortAlphabetically(), Type.Bool),
    "hideInactiveBuffers" to QVariant.of(hideInactiveBuffers(), Type.Bool),
    "hideInactiveNetworks" to QVariant.of(hideInactiveNetworks(), Type.Bool),
    "disableDecoration" to QVariant.of(disableDecoration(), Type.Bool),
    "allowedBufferTypes" to QVariant.of(allowedBufferTypes(), Type.Int),
    "minimumActivity" to QVariant.of(minimumActivity(), Type.Int),
    "showSearch" to QVariant.of(showSearch(), Type.Bool)
  )

  override fun initSetBufferList(buffers: QVariantList) {
    _buffers = buffers.mapNotNull { it.value<BufferId?>() }.toMutableList()
    live_buffers.onNext(Unit)
  }

  override fun initSetRemovedBuffers(buffers: QVariantList) {
    _removedBuffers = buffers.mapNotNull { it.value<BufferId?>() }.toMutableSet()
    live_removedBuffers.onNext(Unit)
  }

  override fun initSetTemporarilyRemovedBuffers(buffers: QVariantList) {
    _temporarilyRemovedBuffers = buffers.mapNotNull { it.value<BufferId?>() }.toMutableSet()
    live_temporarilyRemovedBuffers.onNext(Unit)
  }

  override fun initSetProperties(properties: QVariantMap) {
    _bufferViewName = properties["bufferViewName"].valueOr(this::bufferViewName)
    _networkId = properties["networkId"].valueOr(this::networkId)
    _addNewBuffersAutomatically = properties["addNewBuffersAutomatically"].valueOr(this::addNewBuffersAutomatically)
    _sortAlphabetically = properties["sortAlphabetically"].valueOr(this::sortAlphabetically)
    _hideInactiveBuffers = properties["hideInactiveBuffers"].valueOr(this::hideInactiveBuffers)
    _hideInactiveNetworks = properties["hideInactiveNetworks"].valueOr(this::hideInactiveNetworks)
    _disableDecoration = properties["disableDecoration"].valueOr(this::disableDecoration)
    _allowedBufferTypes = properties["allowedBufferTypes"].valueOr(this::allowedBufferTypes)
    _minimumActivity = properties["minimumActivity"].valueOr(this::minimumActivity)
    _showSearch = properties["showSearch"].valueOr(this::showSearch)
  }

  override fun addBuffer(bufferId: BufferId, pos: Int) {
    if (_buffers.contains(bufferId))
      return

    if (_removedBuffers.contains(bufferId)) {
      _removedBuffers.remove(bufferId)
      live_removedBuffers.onNext(Unit)
    }

    if (_temporarilyRemovedBuffers.contains(bufferId)) {
      _temporarilyRemovedBuffers.remove(bufferId)
      live_temporarilyRemovedBuffers.onNext(Unit)
    }

    _buffers.add(minOf(maxOf(pos, 0), _buffers.size), bufferId)
    live_buffers.onNext(Unit)
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

    live_buffers.onNext(Unit)
  }

  override fun removeBuffer(bufferId: BufferId) {
    if (_buffers.contains(bufferId)) {
      _buffers.remove(bufferId)
      live_buffers.onNext(Unit)
    }

    if (_removedBuffers.contains(bufferId)) {
      _removedBuffers.remove(bufferId)
      live_removedBuffers.onNext(Unit)
    }

    _temporarilyRemovedBuffers.add(bufferId)
    live_temporarilyRemovedBuffers.onNext(Unit)
  }

  override fun removeBufferPermanently(bufferId: BufferId) {
    if (_buffers.contains(bufferId)) {
      _buffers.remove(bufferId)
      live_buffers.onNext(Unit)
    }

    if (_temporarilyRemovedBuffers.contains(bufferId)) {
      _temporarilyRemovedBuffers.remove(bufferId)
      live_temporarilyRemovedBuffers.onNext(Unit)
    }

    _removedBuffers.add(bufferId)
    live_removedBuffers.onNext(Unit)
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

  fun buffers(): List<BufferId> = _buffers.toList()
  fun removedBuffers(): Set<BufferId> = _removedBuffers.toSet()
  fun temporarilyRemovedBuffers(): Set<BufferId> = _temporarilyRemovedBuffers.toSet()

  fun liveUpdates(): Observable<BufferViewConfig> =
    live_config.map { this }

  fun liveBuffers(): Observable<List<BufferId>> =
    live_buffers.map { buffers() }

  fun liveRemovedBuffers(): Observable<Set<BufferId>> =
    live_removedBuffers.map { removedBuffers() }

  fun liveTemporarilyRemovedBuffers(): Observable<Set<BufferId>> =
    live_temporarilyRemovedBuffers.map { temporarilyRemovedBuffers() }

  fun copy(): BufferViewConfig {
    val config = BufferViewConfig(this.bufferViewId(), SignalProxy.NULL)
    config.fromVariantMap(toVariantMap())
    return config
  }

  override fun setAddNewBuffersAutomatically(addNewBuffersAutomatically: Boolean) {
    _addNewBuffersAutomatically = addNewBuffersAutomatically
    super.setAddNewBuffersAutomatically(addNewBuffersAutomatically)
  }

  fun setAllowedBufferTypes(bufferTypes: Buffer_Types) {
    _allowedBufferTypes = bufferTypes
    super.setAllowedBufferTypes(bufferTypes.toInt())
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

  private val _bufferViewId: Int = bufferViewId
  private var _bufferViewName: String = ""
    set(value) {
      field = value
      live_config.onNext(Unit)
    }
  private var _networkId: NetworkId = 0
    set(value) {
      field = value
      live_config.onNext(Unit)
    }
  private var _addNewBuffersAutomatically: Boolean = true
    set(value) {
      field = value
      live_config.onNext(Unit)
    }
  private var _sortAlphabetically: Boolean = true
    set(value) {
      field = value
      live_config.onNext(Unit)
    }
  private var _hideInactiveBuffers: Boolean = false
    set(value) {
      field = value
      live_config.onNext(Unit)
    }
  private var _hideInactiveNetworks: Boolean = false
    set(value) {
      field = value
      live_config.onNext(Unit)
    }
  private var _disableDecoration: Boolean = false
    set(value) {
      field = value
      live_config.onNext(Unit)
    }
  private var _allowedBufferTypes: Buffer_Types = Buffer_Type.of(*Buffer_Type.validValues)
    set(value) {
      field = value
      live_config.onNext(Unit)
    }
  private var _minimumActivity: Buffer_Activities = Buffer_Activities.of(0)
    set(value) {
      field = value
      live_config.onNext(Unit)
    }
  private var _showSearch: Boolean = false
    set(value) {
      field = value
      live_config.onNext(Unit)
    }
  private var _buffers: MutableList<BufferId> = mutableListOf()
  private var _removedBuffers: MutableSet<BufferId> = mutableSetOf()
  private var _temporarilyRemovedBuffers: MutableSet<BufferId> = mutableSetOf()
  private val live_config = BehaviorSubject.createDefault(Unit)
  private val live_buffers = BehaviorSubject.createDefault(Unit)
  private val live_removedBuffers = BehaviorSubject.createDefault(Unit)
  private val live_temporarilyRemovedBuffers = BehaviorSubject.createDefault(Unit)

  object NameComparator : Comparator<BufferViewConfig> {
    override fun compare(a: BufferViewConfig?, b: BufferViewConfig?) =
      (a?.bufferViewName() ?: "").compareTo((b?.bufferViewName() ?: ""), true)
  }

  fun insertBufferSorted(info: BufferInfo, bufferSyncer: BufferSyncer) {
    if (!_buffers.contains(info.bufferId)) {
      val position = if (_sortAlphabetically) {
        val sortedBuffers = _buffers.mapNotNull { bufferSyncer.bufferInfo(it)?.bufferName }
        -sortedBuffers.binarySearch(info.bufferName)
      } else {
        _buffers.size
      }
      requestAddBuffer(info.bufferId, position)
    }
  }

  fun handleBuffer(info: BufferInfo, bufferSyncer: BufferSyncer) {
    if (_addNewBuffersAutomatically &&
        !_buffers.contains(info.bufferId) &&
        !_temporarilyRemovedBuffers.contains(info.bufferId) &&
        !_removedBuffers.contains(info.bufferId) &&
        !info.type.hasFlag(Buffer_Type.StatusBuffer)) {
      insertBufferSorted(info, bufferSyncer)
    }
  }
}
