/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class BufferSyncer constructor(
  var session: ISession,
  private val notificationManager: NotificationManager?
) : SyncableObject(session.proxy, "BufferSyncer"), IBufferSyncer {
  override fun deinit() {
    super.deinit()
    session = ISession.NULL
  }

  fun lastSeenMsg(buffer: BufferId): MsgId = _lastSeenMsg[buffer] ?: 0
  fun liveLastSeenMsg(buffer: BufferId): Observable<MsgId> = live_lastSeenMsg.map {
    markerLine(buffer)
  }.distinctUntilChanged()

  fun liveLastSeenMsgs(): Observable<Map<BufferId, MsgId>> =
    live_lastSeenMsg.map { _lastSeenMsg.toMap() }

  fun markerLine(buffer: BufferId): MsgId = _markerLines[buffer] ?: 0
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

  override fun initActivities(): QVariantList {
    val list: MutableList<QVariant_> = mutableListOf()
    for ((key, value) in _bufferActivities) {
      list.add(QVariant.of(key, QType.BufferId))
      list.add(QVariant.of(value, Type.Int))
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
    for ((key, value) in _bufferActivities) {
      list.add(QVariant.of(key, QType.BufferId))
      list.add(QVariant.of(value, QType.MsgId))
    }
    return list
  }

  override fun initMarkerLines(): QVariantList {
    val list: MutableList<QVariant_> = mutableListOf()
    for ((key, value) in _bufferActivities) {
      list.add(QVariant.of(key, QType.BufferId))
      list.add(QVariant.of(value, QType.MsgId))
    }
    return list
  }

  override fun initSetActivities(data: QVariantList) {
    (0 until data.size step 2).map {
      data[it].value(0) to data[it + 1].value(0)
    }.forEach { (buffer, activity) ->
      setBufferActivity(buffer, activity)
    }
    live_bufferActivities.onNext(Unit)
  }

  override fun initSetHighlightCounts(data: QVariantList) {
    (0 until data.size step 2).map {
      data[it].value(0) to data[it + 1].value(0)
    }.forEach { (buffer, count) ->
      setHighlightCount(buffer, count)
    }
    live_highlightCounts.onNext(Unit)
  }

  override fun initSetLastSeenMsg(data: QVariantList) {
    (0 until data.size step 2).map {
      data[it].value(0) to data[it + 1].value(0L)
    }.forEach { (buffer, msgId) ->
      setLastSeenMsg(buffer, msgId)
    }
    live_lastSeenMsg.onNext(Unit)
  }

  override fun initSetMarkerLines(data: QVariantList) {
    (0 until data.size step 2).map {
      data[it].value(0) to data[it + 1].value(0L)
    }.forEach { (buffer, msgId) ->
      setMarkerLine(buffer, msgId)
    }
    live_markerLines.onNext(Unit)
  }

  fun initSetBufferInfos(infos: QVariantList?) {
    _bufferInfos.clear()
    infos?.mapNotNull { it.value<BufferInfo>() }?.forEach { _bufferInfos[it.bufferId] = it }
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
    session.backlogManager?.removeBuffer(buffer)
    notificationManager?.clear(buffer)
  }

  override fun renameBuffer(buffer: BufferId, newName: String) {
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
      live_lastSeenMsg.onNext(Unit)
      super.setLastSeenMsg(buffer, msgId)
      notificationManager?.clear(buffer, msgId)
    }
  }

  override fun setMarkerLine(buffer: BufferId, msgId: MsgId) {
    if (msgId < 0 || markerLine(buffer) == msgId)
      return

    _markerLines[buffer] = msgId
    live_markerLines.onNext(Unit)
    super.setMarkerLine(buffer, msgId)
  }

  override fun setBufferActivity(buffer: BufferId, activity: Int) {
    val flags = Message_Types.of<Message_Type>(activity)
    super.setBufferActivity(buffer, activity)
    if (flags hasFlag Message_Type.Plain ||
        flags hasFlag Message_Type.Notice ||
        flags hasFlag Message_Type.Action) {
      bufferInfo(buffer)?.let {
        session.bufferViewManager?.handleBuffer(it, this, true)
      }
    }
    _bufferActivities[buffer] = flags
    live_bufferActivities.onNext(Unit)
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
    val caseMapper = IrcCaseMappers[session.networks[it.bufferId]?.support("CASEMAPPING")]
    bufferName == null || caseMapper.equalsIgnoreCaseNullable(it.bufferName, bufferName)
  }

  fun find(
    bufferName: String? = null,
    bufferId: BufferId? = null,
    networkId: NetworkId? = null,
    type: Buffer_Types? = null,
    groupId: Int? = null
  ) = all(bufferName, bufferId, networkId, type, groupId).firstOrNull()

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
