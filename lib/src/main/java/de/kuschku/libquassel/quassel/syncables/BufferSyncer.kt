package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.IBufferSyncer
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SignalProxy
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class BufferSyncer constructor(
  proxy: SignalProxy,
  private val session: ISession
) : SyncableObject(proxy, "BufferSyncer"), IBufferSyncer {
  fun lastSeenMsg(buffer: BufferId): MsgId = _lastSeenMsg[buffer] ?: 0
  fun liveLastSeenMsg(buffer: BufferId): Observable<MsgId> = live_lastSeenMsg.map {
    markerLine(buffer)
  }.distinctUntilChanged()

  fun liveLastSeenMsgs(): Observable<Map<BufferId, MsgId>> = live_lastSeenMsg

  fun markerLine(buffer: BufferId): MsgId = _markerLines[buffer] ?: 0
  fun liveMarkerLine(
    buffer: BufferId): Observable<MsgId> = live_markerLines.map { markerLine(buffer) }.distinctUntilChanged()

  fun liveMarkerLines(): Observable<Map<BufferId, MsgId>> = live_markerLines

  fun activity(buffer: BufferId): Message_Types = _bufferActivities[buffer] ?: Message_Types.of()
  fun liveActivity(
    buffer: BufferId): Observable<Message_Types> = live_bufferActivities.map { activity(buffer) }.distinctUntilChanged()

  fun liveActivities(): Observable<Map<BufferId, Message_Types>> = live_bufferActivities

  fun highlightCount(buffer: BufferId): Int = _highlightCounts[buffer] ?: 0
  fun liveHighlightCount(
    buffer: BufferId): Observable<Int> = live_highlightCounts.map { highlightCount(buffer) }.distinctUntilChanged()

  fun liveHighlightCounts(): Observable<Map<BufferId, Int>> = live_highlightCounts

  fun bufferInfo(bufferId: BufferId) = _bufferInfos[bufferId]
  fun liveBufferInfo(
    bufferId: BufferId) = live_bufferInfos.map { bufferInfo(bufferId) }.distinctUntilChanged()

  fun bufferInfos(): Collection<BufferInfo> = _bufferInfos.values
  fun liveBufferInfos(): Observable<Map<BufferId, BufferInfo>> = live_bufferInfos

  override fun toVariantMap(): QVariantMap = mapOf(
    "Activities" to QVariant_(initActivities(), Type.QVariantList),
    "HighlightCounts" to QVariant_(initHighlightCounts(), Type.QVariantList),
    "LastSeenMsg" to QVariant_(initLastSeenMsg(), Type.QVariantList),
    "MarkerLines" to QVariant_(initMarkerLines(), Type.QVariantList)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetActivities(properties["Activities"].valueOr(::emptyList))
    initSetHighlightCounts(properties["HighlightCounts"].valueOr(::emptyList))
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

  override fun initHighlightCounts(): QVariantList {
    val list: MutableList<QVariant_> = mutableListOf()
    for ((key, value) in _highlightCounts) {
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
    live_bufferActivities.onNext(_bufferActivities)
  }

  override fun initSetHighlightCounts(data: QVariantList) {
    (0 until data.size step 2).map {
      data[it].value(0) to data[it + 1].value(0)
    }.forEach { (buffer, count) ->
      setHighlightCount(buffer, count)
    }
    live_highlightCounts.onNext(_highlightCounts)
  }

  override fun initSetLastSeenMsg(data: QVariantList) {
    (0 until data.size step 2).map {
      data[it].value(0) to data[it + 1].value(0)
    }.forEach { (buffer, msgId) ->
      setLastSeenMsg(buffer, msgId)
    }
    live_lastSeenMsg.onNext(_lastSeenMsg)
  }

  override fun initSetMarkerLines(data: QVariantList) {
    (0 until data.size step 2).map {
      data[it].value(0) to data[it + 1].value(0)
    }.forEach { (buffer, msgId) ->
      setMarkerLine(buffer, msgId)
    }
    live_markerLines.onNext(_markerLines)
  }

  fun initSetBufferInfos(infos: QVariantList?) {
    _bufferInfos.clear()
    infos?.mapNotNull { it.value<BufferInfo>() }?.forEach { _bufferInfos[it.bufferId] = it }
    live_bufferInfos.onNext(_bufferInfos)
  }

  override fun mergeBuffersPermanently(buffer1: BufferId, buffer2: BufferId) {
    _lastSeenMsg.remove(buffer2);live_lastSeenMsg.onNext(_lastSeenMsg)
    _markerLines.remove(buffer2);live_markerLines.onNext(_markerLines)
    _bufferActivities.remove(buffer2);live_bufferActivities.onNext(_bufferActivities)
    _highlightCounts.remove(buffer2);live_highlightCounts.onNext(_highlightCounts)
    _bufferInfos.remove(buffer2);live_bufferInfos.onNext(_bufferInfos)
  }

  override fun removeBuffer(buffer: BufferId) {
    _lastSeenMsg.remove(buffer);live_lastSeenMsg.onNext(_lastSeenMsg)
    _markerLines.remove(buffer);live_markerLines.onNext(_markerLines)
    _bufferActivities.remove(buffer);live_bufferActivities.onNext(_bufferActivities)
    _highlightCounts.remove(buffer);live_highlightCounts.onNext(_highlightCounts)
    _bufferInfos.remove(buffer);live_bufferInfos.onNext(_bufferInfos)
  }

  override fun renameBuffer(buffer: BufferId, newName: String) {
    val bufferInfo = _bufferInfos[buffer]
    if (bufferInfo != null) {
      _bufferInfos[buffer] = bufferInfo.copy(bufferName = newName)
      live_bufferInfos.onNext(_bufferInfos)
    }
  }

  fun bufferInfoUpdated(info: BufferInfo) {
    val oldInfo = _bufferInfos[info.bufferId]
    if (info != oldInfo) {
      _bufferInfos[info.bufferId] = info
      live_bufferInfos.onNext(_bufferInfos)

      if (oldInfo == null) {
        session.bufferViewManager?.handleBuffer(info, this)
      }
    }
  }

  override fun setLastSeenMsg(buffer: BufferId, msgId: MsgId) {
    if (msgId < 0)
      return

    val oldLastSeenMsg = lastSeenMsg(buffer)
    if (oldLastSeenMsg < msgId) {
      _lastSeenMsg[buffer] = msgId
      live_lastSeenMsg.onNext(_lastSeenMsg)
      super.setLastSeenMsg(buffer, msgId)
    }
  }

  override fun setMarkerLine(buffer: BufferId, msgId: MsgId) {
    if (msgId < 0 || markerLine(buffer) == msgId)
      return

    _markerLines[buffer] = msgId
    live_markerLines.onNext(_markerLines)
    super.setMarkerLine(buffer, msgId)
  }

  override fun setBufferActivity(buffer: BufferId, activity: Int) {
    val flags = Message_Types.of<Message_Type>(activity)
    super.setBufferActivity(buffer, activity)
    _bufferActivities[buffer] = flags
    live_bufferActivities.onNext(_bufferActivities)
  }

  override fun setHighlightCount(buffer: BufferId, count: Int) {
    super.setHighlightCount(buffer, count)
    _highlightCounts[buffer] = count
    live_highlightCounts.onNext(_highlightCounts)
  }

  private val _lastSeenMsg: MutableMap<BufferId, MsgId> = mutableMapOf()
  private val live_lastSeenMsg = BehaviorSubject.createDefault(mapOf<BufferId, MsgId>())

  private val _markerLines: MutableMap<BufferId, MsgId> = mutableMapOf()
  private val live_markerLines = BehaviorSubject.createDefault(mapOf<BufferId, MsgId>())

  private val _bufferActivities: MutableMap<BufferId, Message_Types> = mutableMapOf()
  private val live_bufferActivities = BehaviorSubject.createDefault(mapOf<BufferId, Message_Types>())

  private val _highlightCounts: MutableMap<BufferId, Int> = mutableMapOf()
  private val live_highlightCounts = BehaviorSubject.createDefault(mapOf<BufferId, Int>())

  private val _bufferInfos = mutableMapOf<BufferId, BufferInfo>()
  private val live_bufferInfos = BehaviorSubject.createDefault(mapOf<BufferId, BufferInfo>())
}
