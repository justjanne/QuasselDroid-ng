package de.kuschku.quasseldroid_ng.quassel.syncables

import de.kuschku.quasseldroid_ng.protocol.*
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.IBufferViewManager
import de.kuschku.quasseldroid_ng.session.SignalProxy

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

  override fun initSetBufferViewIds(bufferViewIds: QVariantList) {
    bufferViewIds
      .mapNotNull { it.value<Int>() }
      .forEach { addBufferViewConfig(it) }
  }

  override fun addBufferViewConfig(config: BufferViewConfig) {
    if (_bufferViewConfigs.contains(config.bufferViewId()))
      return

    proxy.synchronize(config)
    _bufferViewConfigs[config.bufferViewId()] = config
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
  }

  private val _bufferViewConfigs: MutableMap<BufferId, BufferViewConfig> = mutableMapOf()
}
