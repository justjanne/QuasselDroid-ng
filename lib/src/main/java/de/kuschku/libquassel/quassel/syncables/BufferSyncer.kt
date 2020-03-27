/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.IBufferSyncer
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.NotificationManager
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.minus
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class BufferSyncer constructor(
  var session: ISession,
  private val notificationManager: NotificationManager? = null
) : SyncableObject(session.proxy, "BufferSyncer"), IBufferSyncer {
  override fun deinit() {
    super.deinit()
    session = ISession.NULL
  }

  fun lastSeenMsg(buffer: BufferId): MsgId = _lastSeenMsg[buffer] ?: MsgId(0)
  fun liveLastSeenMsg(buffer: BufferId): Observable<MsgId> = live_lastSeenMsg.map {
    markerLine(buffer)
  }.distinctUntilChanged()

  fun liveLastSeenMsgs(): Observable<Map<BufferId, MsgId>> =
    live_lastSeenMsg.map { _lastSeenMsg.toMap() }

  fun markerLine(buffer: BufferId): MsgId = _markerLines[buffer] ?: MsgId(0)
  fun liveMarkerLine(buffer: BufferId): Observable<MsgId> =
    live_markerLines.map { markerLine(buffer) }.distinctUntilChanged()

  fun liveMarkerLines(): Observable<Map<BufferId, MsgId>> =
    live_markerLines.map { _markerLines.toMap() }

  fun activity(buffer: BufferId): Message_Types =
    _bufferActivities[buffer] ?: Message_Types.of()

  fun liveActivity(buffer: BufferId): Observable<Message_Types> =
    live_bufferActivities.map { activity(buffer) }.distinctUntilChanged()

  fun liveActivities(): Observable<Map<BufferId, Message_Types>> =
    live_bufferActivities.map { _bufferActivities.toMap() }

  fun highlightCount(buffer: BufferId): Int = _highlightCounts[buffer] ?: 0
  fun liveHighlightCount(buffer: BufferId): Observable<Int> =
    live_highlightCounts.map { highlightCount(buffer) }.distinctUntilChanged()

  fun liveHighlightCounts(): Observable<Map<BufferId, Int>> =
    live_highlightCounts.map { _highlightCounts.toMap() }

  fun bufferInfo(bufferId: BufferId) = _bufferInfos[bufferId]
  fun liveBufferInfo(bufferId: BufferId) =
    live_bufferInfos.map { bufferInfo(bufferId) }.distinctUntilChanged()

  fun bufferInfos(): Collection<BufferInfo> = _bufferInfos.values.toList()
  fun liveBufferInfos(): Observable<Map<BufferId, BufferInfo>> = live_bufferInfos.map { _bufferInfos.toMap() }

  override fun toVariantMap(): QVariantMap = mapOf(
    "Activities" to QVariant.of(initActivities(), Type.QVariantList),
    "HighlightCounts" to QVariant.of(initHighlightCounts(), Type.QVariantList),
    "LastSeenMsg" to QVariant.of(initLastSeenMsg(), Type.QVariantList),
    "MarkerLines" to QVariant.of(initMarkerLines(), Type.QVariantList)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetActivities(properties["Activities"].valueOr(::emptyList))
    initSetHighlightCounts(properties["HighlightCounts"].valueOr(::emptyList))
    initSetLastSeenMsg(properties["LastSeenMsg"].valueOr(::emptyList))
    initSetMarkerLines(properties["MarkerLines"].valueOr(::emptyList))
  }

  fun copy() = BufferSyncer(session).also {
    it.fromVariantMap(toVariantMap())
  }

  fun isEqual(other: BufferSyncer) =
    _bufferInfos == other._bufferInfos &&
    _lastSeenMsg == other._lastSeenMsg &&
    _markerLines == other._markerLines &&
    _bufferActivities == other._bufferActivities &&
    _highlightCounts == other._highlightCounts


  override fun initActivities(): QVariantList {
    val list: MutableList<QVariant_> = mutableListOf()
    for ((key, value) in _bufferActivities) {
      list.add(QVariant.of(key, QType.BufferId))
      list.add(QVariant.of(value.toInt(), Type.Int))
    }
    return list
  }

  override fun initHighlightCounts(): QVariantList {
    val list: MutableList<QVariant_> = mutableListOf()
    for ((key, value) in _highlightCounts) {
      list.add(QVariant.of(key, QType.BufferId))
      list.add(QVariant.of(value, Type.Int))
    }
    return list
  }

  override fun initLastSeenMsg(): QVariantList {
    val list: MutableList<QVariant_> = mutableListOf()
    for ((key, value) in _lastSeenMsg) {
      list.add(QVariant.of(key, QType.BufferId))
      list.add(QVariant.of(value, QType.MsgId))
    }
    return list
  }

  override fun initMarkerLines(): QVariantList {
    val list: MutableList<QVariant_> = mutableListOf()
    for ((key, value) in _markerLines) {
      list.add(QVariant.of(key, QType.BufferId))
      list.add(QVariant.of(value, QType.MsgId))
    }
    return list
  }

  override fun initSetActivities(data: QVariantList) {
    setActivities((0 until data.size step 2).map {
      Pair(
        data[it].value(BufferId(0)),
        Message_Type.of(data[it + 1].value(0))
      )
    })
  }

  fun setActivities(data: List<Pair<BufferId, Message_Types>>) {
    for ((buffer, activity) in data) {
      setBufferActivityInternal(buffer, activity)
    }
    live_bufferActivities.onNext(Unit)
  }

  override fun initSetHighlightCounts(data: QVariantList) {
    setHighlightCounts((0 until data.size step 2).map {
      Pair(
        data[it].value(BufferId(0)),
        data[it + 1].value(0)
      )
    })
  }

  fun setHighlightCounts(data: List<Pair<BufferId, Int>>) {
    for ((buffer, count) in data) {
      setHighlightCount(buffer, count)
    }
    live_highlightCounts.onNext(Unit)
  }

  override fun initSetLastSeenMsg(data: QVariantList) {
    setLastSeenMsg((0 until data.size step 2).map {
      Pair(
        data[it].value(BufferId(0)),
        data[it + 1].value(MsgId(0L))
      )
    })
  }

  fun setLastSeenMsg(data: List<Pair<BufferId, MsgId>>) {
    for ((buffer, msgId) in data) {
      setLastSeenMsg(buffer, msgId)
    }
    live_lastSeenMsg.onNext(Unit)
  }

  override fun initSetMarkerLines(data: QVariantList) {
    setMarkerLines((0 until data.size step 2).map {
      Pair(
        data[it].value(BufferId(0)),
        data[it + 1].value(MsgId(0L))
      )
    })
  }

  fun setMarkerLines(data: List<Pair<BufferId, MsgId>>) {
    for ((buffer, msgId) in data) {
      setMarkerLine(buffer, msgId)
    }
    live_markerLines.onNext(Unit)
  }

  fun initSetBufferInfos(infos: QVariantList?) {
    setBufferInfos(infos?.mapNotNull { it.value<BufferInfo>() }.orEmpty())
  }

  fun setBufferInfos(infos: List<BufferInfo>) {
    _bufferInfos.clear()
    for (info in infos) {
      _bufferInfos[info.bufferId] = info
    }
    live_bufferInfos.onNext(Unit)
  }

  override fun mergeBuffersPermanently(buffer1: BufferId, buffer2: BufferId) {
    removeBuffer(buffer2)
  }

  override fun removeBuffer(buffer: BufferId) {
    _lastSeenMsg.remove(buffer);live_lastSeenMsg.onNext(Unit)
    _markerLines.remove(buffer);live_markerLines.onNext(Unit)
    _bufferActivities.remove(buffer);live_bufferActivities.onNext(Unit)
    _highlightCounts.remove(buffer);live_highlightCounts.onNext(Unit)
    _bufferInfos.remove(buffer);live_bufferInfos.onNext(Unit)
    session.backlogManager.removeBuffer(buffer)
    notificationManager?.clear(buffer)
  }

  override fun renameBuffer(buffer: BufferId, newName: String?) {
    val bufferInfo = _bufferInfos[buffer]
    if (bufferInfo != null) {
      _bufferInfos[buffer] = bufferInfo.copy(bufferName = newName)
      live_bufferInfos.onNext(Unit)
    }
  }

  fun bufferInfoUpdated(info: BufferInfo) {
    val oldInfo = _bufferInfos[info.bufferId]
    if (info != oldInfo) {
      _bufferInfos[info.bufferId] = info
      live_bufferInfos.onNext(Unit)

      if (oldInfo == null) {
        session.bufferViewManager.handleBuffer(info, this)
      }
    }
  }

  override fun setLastSeenMsg(buffer: BufferId, msgId: MsgId) {
    if (msgId < MsgId(0))
      return

    val oldLastSeenMsg = lastSeenMsg(buffer)
    if (oldLastSeenMsg < msgId) {
      _lastSeenMsg[buffer] = msgId
      live_lastSeenMsg.onNext(Unit)
      super.setLastSeenMsg(buffer, msgId)
      notificationManager?.clear(buffer, msgId)
    }
  }

  override fun setMarkerLine(buffer: BufferId, msgId: MsgId) {
    if (msgId < MsgId(0) || markerLine(buffer) == msgId)
      return

    _markerLines[buffer] = msgId
    live_markerLines.onNext(Unit)
    super.setMarkerLine(buffer, msgId)
  }

  override fun setBufferActivity(buffer: BufferId, activity: Int) {
    setBufferActivity(buffer, Message_Type.of(activity))
  }

  fun setBufferActivityInternal(buffer: BufferId, activity: Message_Types) {
    super.setBufferActivity(buffer, activity.toInt())
    _bufferActivities[buffer] = activity
    live_bufferActivities.onNext(Unit)
  }

  fun setBufferActivity(buffer: BufferId, activity: Message_Types) {
    val oldActivity = activity(buffer)
    setBufferActivityInternal(buffer, activity)
    if ((activity - oldActivity).isNotEmpty()) {
      bufferInfo(buffer)?.let {
        session.bufferViewManager.handleBuffer(it, this, true)
      }
    }
  }

  override fun setHighlightCount(buffer: BufferId, count: Int) {
    super.setHighlightCount(buffer, count)
    _highlightCounts[buffer] = count
    live_highlightCounts.onNext(Unit)
  }

  fun all(
    bufferName: String? = null,
    bufferId: BufferId? = null,
    networkId: NetworkId? = null,
    type: Buffer_Types? = null,
    groupId: Int? = null
  ) = _bufferInfos.values.filter {
    bufferId == null || it.bufferId == bufferId
  }.filter {
    networkId == null || it.networkId == networkId
  }.filter {
    type == null || it.type == type
  }.filter {
    groupId == null || it.groupId == groupId
  }.filter {
    val caseMapper = IrcCaseMappers[session.networks[it.networkId]?.support("CASEMAPPING")]
    bufferName == null || caseMapper.equalsIgnoreCaseNullable(it.bufferName, bufferName)
  }

  fun liveAll(
    bufferName: String? = null,
    bufferId: BufferId? = null,
    networkId: NetworkId? = null,
    type: Buffer_Types? = null,
    groupId: Int? = null
  ) = liveBufferInfos().map {
    it.values.filter {
      bufferId == null || it.bufferId == bufferId
    }.filter {
      networkId == null || it.networkId == networkId
    }.filter {
      type == null || it.type == type
    }.filter {
      groupId == null || it.groupId == groupId
    }.filter {
      val caseMapper = IrcCaseMappers[session.networks[it.networkId]?.support("CASEMAPPING")]
      bufferName == null || caseMapper.equalsIgnoreCaseNullable(it.bufferName, bufferName)
    }
  }

  fun find(
    bufferName: String? = null,
    bufferId: BufferId? = null,
    networkId: NetworkId? = null,
    type: Buffer_Types? = null,
    groupId: Int? = null
  ) = all(bufferName, bufferId, networkId, type, groupId).firstOrNull()

  fun liveFind(
    bufferName: String? = null,
    bufferId: BufferId? = null,
    networkId: NetworkId? = null,
    type: Buffer_Types? = null,
    groupId: Int? = null
  ) = liveAll(bufferName, bufferId, networkId, type, groupId).map {
    Optional.ofNullable(it.firstOrNull())
  }

  override fun toString(): String {
    return "BufferSyncer(_lastSeenMsg=$_lastSeenMsg, _markerLines=$_markerLines, _bufferActivities=$_bufferActivities, _highlightCounts=$_highlightCounts, _bufferInfos=$_bufferInfos)"
  }

  private val _lastSeenMsg: MutableMap<BufferId, MsgId> = mutableMapOf()
  private val live_lastSeenMsg = BehaviorSubject.createDefault(Unit)

  private val _markerLines: MutableMap<BufferId, MsgId> = mutableMapOf()
  private val live_markerLines = BehaviorSubject.createDefault(Unit)

  private val _bufferActivities: MutableMap<BufferId, Message_Types> = mutableMapOf()
  private val live_bufferActivities = BehaviorSubject.createDefault(Unit)

  private val _highlightCounts: MutableMap<BufferId, Int> = mutableMapOf()
  private val live_highlightCounts = BehaviorSubject.createDefault(Unit)

  private val _bufferInfos = mutableMapOf<BufferId, BufferInfo>()
  private val live_bufferInfos = BehaviorSubject.createDefault(Unit)
}
