package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.IBufferSyncer
import de.kuschku.libquassel.session.SignalProxy
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class BufferSyncer constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "BufferSyncer"), IBufferSyncer {
  fun lastSeenMsg(buffer: BufferId): MsgId = _lastSeenMsg[buffer] ?: 0
  fun markerLine(buffer: BufferId): MsgId = _markerLines[buffer] ?: 0
  fun activity(buffer: BufferId): Message_Types = _bufferActivities[buffer] ?: Message_Types.of()
  fun liveActivity(buffer: BufferId): Observable<Message_Types>
    = live_bufferActivities.map { activity(buffer) }.distinctUntilChanged()

  override fun toVariantMap(): QVariantMap = mapOf(
    "Activities" to QVariant_(initActivities(), Type.QVariantList),
    "LastSeenMsg" to QVariant_(initLastSeenMsg(), Type.QVariantList),
    "MarkerLines" to QVariant_(initMarkerLines(), Type.QVariantList)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetActivities(properties["Activities"].valueOr(::emptyList))
    initSetLastSeenMsg(properties["LastSeenMsg"].valueOr(::emptyList))
    initSetMarkerLines(properties["MarkerLines"].valueOr(::emptyList))
  }

  override fun initActivities(): QVariantList {
    val list: MutableList<QVariant_> = mutableListOf()
    for ((key, value) in _bufferActivities) {
      list.add(QVariant_(key, QType.BufferId))
      list.add(QVariant_(value, Type.Int))
    }
    return list
  }

  override fun initLastSeenMsg(): QVariantList {
    val list: MutableList<QVariant_> = mutableListOf()
    for ((key, value) in _bufferActivities) {
      list.add(QVariant_(key, QType.BufferId))
      list.add(QVariant_(value, QType.MsgId))
    }
    return list
  }

  override fun initMarkerLines(): QVariantList {
    val list: MutableList<QVariant_> = mutableListOf()
    for ((key, value) in _bufferActivities) {
      list.add(QVariant_(key, QType.BufferId))
      list.add(QVariant_(value, QType.MsgId))
    }
    return list
  }

  override fun initSetActivities(data: QVariantList) {
    (0 until data.size step 2).map {
      data[it].value(0) to data[it + 1].value(0)
    }.forEach { (buffer, activity) ->
      setBufferActivity(buffer, activity)
    }
  }

  override fun initSetLastSeenMsg(data: QVariantList) {
    (0 until data.size step 2).map {
      data[it].value(0) to data[it + 1].value(0)
    }.forEach { (buffer, msgId) ->
      setLastSeenMsg(buffer, msgId)
    }
  }

  override fun initSetMarkerLines(data: QVariantList) {
    (0 until data.size step 2).map {
      data[it].value(0) to data[it + 1].value(0)
    }.forEach { (buffer, msgId) ->
      setMarkerLine(buffer, msgId)
    }
  }

  fun initSetBufferInfos(infos: QVariantList?) {
    _bufferInfos.clear()
    infos?.mapNotNull { it.value<BufferInfo>() }?.forEach { _bufferInfos[it.bufferId] = it }
    live_bufferInfos.onNext(_bufferInfos)
  }

  override fun mergeBuffersPermanently(buffer1: BufferId, buffer2: BufferId) {
    _lastSeenMsg.remove(buffer2)
    _markerLines.remove(buffer2)
    _bufferActivities.remove(buffer2)
    live_bufferInfos.onNext(_bufferInfos)
  }

  override fun removeBuffer(buffer: BufferId) {
    _lastSeenMsg.remove(buffer)
    _markerLines.remove(buffer)
    _bufferActivities.remove(buffer)
    _bufferInfos.remove(buffer)
    live_bufferInfos.onNext(_bufferInfos)
  }

  override fun renameBuffer(buffer: BufferId, newName: String) {
    val bufferInfo = _bufferInfos[buffer]
    if (bufferInfo != null) {
      _bufferInfos[buffer] = bufferInfo.copy(bufferName = newName)
      live_bufferInfos.onNext(_bufferInfos)
    }
  }

  fun bufferInfo(bufferId: BufferId) = _bufferInfos[bufferId]
  fun liveBufferInfo(bufferId: BufferId) = live_bufferInfos.distinctUntilChanged().map {
    bufferInfo(bufferId)
  }

  fun bufferInfoUpdated(info: BufferInfo) {
    if (info != _bufferInfos[info.bufferId]) {
      _bufferInfos[info.bufferId] = info
      live_bufferInfos.onNext(_bufferInfos)
    }
  }

  override fun setLastSeenMsg(buffer: BufferId, msgId: MsgId) {
    if (msgId < 0)
      return

    val oldLastSeenMsg = lastSeenMsg(buffer)
    if (oldLastSeenMsg < msgId) {
      _lastSeenMsg[buffer] = msgId
      super.setLastSeenMsg(buffer, msgId)
    }
  }

  override fun setMarkerLine(buffer: BufferId, msgId: MsgId) {
    if (msgId < 0 || markerLine(buffer) == msgId)
      return

    _markerLines[buffer] = msgId
    super.setMarkerLine(buffer, msgId)
  }

  override fun setBufferActivity(buffer: BufferId, activity: Int) {
    val flags = Message_Types.of<Message_Type>(activity)
    super.setBufferActivity(buffer, activity)
    _bufferActivities[buffer] = flags
    live_bufferActivities.onNext(_bufferActivities)
  }

  private val _lastSeenMsg: MutableMap<BufferId, MsgId> = mutableMapOf()
  private val _markerLines: MutableMap<BufferId, MsgId> = mutableMapOf()
  private val _bufferActivities: MutableMap<BufferId, Message_Types> = mutableMapOf()
  private val live_bufferActivities = BehaviorSubject.createDefault(
    mutableMapOf<BufferId, Message_Types>()
  )
  private val _bufferInfos = mutableMapOf<BufferId, BufferInfo>()
  val live_bufferInfos = BehaviorSubject.createDefault(mutableMapOf<BufferId, BufferInfo>())
}
