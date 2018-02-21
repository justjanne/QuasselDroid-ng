package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.syncables.interfaces.IBufferViewManager
import de.kuschku.libquassel.session.SignalProxy
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class BufferViewManager constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "BufferViewManager"), IBufferViewManager {
  override fun toVariantMap(): QVariantMap = mapOf(
    "BufferViewIds" to QVariant_(initBufferViewIds(), Type.QVariantList)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetBufferViewIds(properties["BufferViewIds"].valueOr(::emptyList))
  }

  override fun initBufferViewIds(): QVariantList = _bufferViewConfigs.keys.map {
    QVariant_(it, Type.Int)
  }

  fun bufferViewConfig(bufferViewId: Int) = _bufferViewConfigs[bufferViewId]

  fun bufferViewConfigs() = _bufferViewConfigs.values

  fun liveBufferViewConfigs(): Observable<Set<Int>> = live_bufferViewConfigs

  override fun initSetBufferViewIds(bufferViewIds: QVariantList) {
    bufferViewIds
      .mapNotNull { it.value<Int>() }
      .forEach { addBufferViewConfig(it) }
  }

  override fun addBufferViewConfig(config: BufferViewConfig) {
    if (_bufferViewConfigs.contains(config.bufferViewId()))
      return

    proxy.synchronize(config, !initialized)
    _bufferViewConfigs[config.bufferViewId()] = config
    live_bufferViewConfigs.onNext(_bufferViewConfigs.keys)
  }

  override fun addBufferViewConfig(bufferViewConfigId: Int) {
    if (_bufferViewConfigs.contains(bufferViewConfigId))
      return

    addBufferViewConfig(BufferViewConfig(bufferViewConfigId, proxy))
  }

  override fun deleteBufferViewConfig(bufferViewConfigId: Int) {
    if (!_bufferViewConfigs.contains(bufferViewConfigId))
      return

    _bufferViewConfigs.remove(bufferViewConfigId)
    live_bufferViewConfigs.onNext(_bufferViewConfigs.keys)
  }

  private val _bufferViewConfigs: MutableMap<Int, BufferViewConfig>
    = mutableMapOf()

  private val live_bufferViewConfigs: BehaviorSubject<Set<Int>>
    = BehaviorSubject.createDefault<Set<Int>>(emptySet())
}
