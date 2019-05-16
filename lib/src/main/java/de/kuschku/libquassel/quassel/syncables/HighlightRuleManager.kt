/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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
import de.kuschku.libquassel.quassel.syncables.interfaces.IHighlightRuleManager
import de.kuschku.libquassel.session.SignalProxy
import java.io.Serializable

class HighlightRuleManager(
  proxy: SignalProxy
) : SyncableObject(proxy, "HighlightRuleManager"), IHighlightRuleManager {
  data class HighlightRule(
    val id: Int,
    val name: String,
    val isRegEx: Boolean = false,
    val isCaseSensitive: Boolean = false,
    val isEnabled: Boolean = true,
    val isInverse: Boolean = false,
    val sender: String,
    val channel: String
  ) : Serializable

  override fun toVariantMap(): QVariantMap = mapOf(
    "HighlightRuleList" to QVariant.of(initHighlightRuleList(), Type.QVariantMap),
    "highlightNick" to QVariant.of(_highlightNick.value, Type.Int),
    "nicksCaseSensitive" to QVariant.of(_nicksCaseSensitive, Type.Bool)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetHighlightRuleList(properties["HighlightRuleList"].valueOr(::emptyMap))

    _highlightNick = properties["highlightNick"].value<Int>()?.let {
      IHighlightRuleManager.HighlightNickType.of(it)
    } ?: _highlightNick
    _nicksCaseSensitive = properties["nicksCaseSensitive"].value(_nicksCaseSensitive)
  }

  override fun initHighlightRuleList(): QVariantMap = mapOf(
    "id" to QVariant.of(_highlightRuleList.map {
      QVariant.of(it.id, Type.Int)
    }, Type.QVariantList),
    "name" to QVariant.of(_highlightRuleList.map {
      it.name
    }, Type.QStringList),
    "isRegEx" to QVariant.of(_highlightRuleList.map {
      QVariant.of(it.isRegEx, Type.Bool)
    }, Type.QVariantList),
    "isCaseSensitive" to QVariant.of(_highlightRuleList.map {
      QVariant.of(it.isCaseSensitive, Type.Bool)
    }, Type.QVariantList),
    "isEnabled" to QVariant.of(_highlightRuleList.map {
      QVariant.of(it.isEnabled, Type.Bool)
    }, Type.QVariantList),
    "isInverse" to QVariant.of(_highlightRuleList.map {
      QVariant.of(it.isInverse, Type.Bool)
    }, Type.QVariantList),
    "sender" to QVariant.of(_highlightRuleList.map {
      it.sender
    }, Type.QStringList),
    "channel" to QVariant.of(_highlightRuleList.map {
      it.channel
    }, Type.QStringList)
  )

  override fun initSetHighlightRuleList(highlightRuleList: QVariantMap) {
    val idList = highlightRuleList["id"].valueOr<QVariantList>(::emptyList)
    val nameList = highlightRuleList["name"].valueOr<QStringList>(::emptyList)
    val isRegExList = highlightRuleList["isRegEx"].valueOr<QVariantList>(::emptyList)
    val isCaseSensitiveList = highlightRuleList["isCaseSensitive"].valueOr<QVariantList>(::emptyList)
    val isEnabledList = highlightRuleList["isEnabled"].valueOr<QVariantList>(::emptyList)
    val isInverseList = highlightRuleList["isInverse"].valueOr<QVariantList>(::emptyList)
    val senderList = highlightRuleList["sender"].valueOr<QStringList>(::emptyList)
    val channelList = highlightRuleList["channel"].valueOr<QStringList>(::emptyList)
    val size = idList.size
    if (nameList.size != size || isRegExList.size != size || isCaseSensitiveList.size != size ||
        isEnabledList.size != size || isInverseList.size != size || senderList.size != size ||
        channelList.size != size)
      return

    _highlightRuleList = List(size) {
      HighlightRule(
        id = idList[it].value(0),
        name = nameList[it] ?: "",
        isRegEx = isRegExList[it].value(false),
        isCaseSensitive = isCaseSensitiveList[it].value(false),
        isEnabled = isEnabledList[it].value(false),
        isInverse = isInverseList[it].value(false),
        sender = senderList[it] ?: "",
        channel = channelList[it] ?: ""
      )
    }
  }

  override fun removeHighlightRule(highlightRule: Int) = removeAt(indexOf(highlightRule))

  override fun toggleHighlightRule(highlightRule: Int) {
    _highlightRuleList = _highlightRuleList.map {
      if (it.id == highlightRule) it.copy(isEnabled = !it.isEnabled) else it
    }
  }

  override fun addHighlightRule(id: Int, name: String?, isRegEx: Boolean, isCaseSensitive: Boolean,
                                isEnabled: Boolean, isInverse: Boolean, sender: String?,
                                chanName: String?) {
    if (contains(id)) return

    _highlightRuleList += HighlightRule(
      id, name ?: "", isRegEx, isCaseSensitive, isEnabled, isInverse, sender ?: "", chanName ?: ""
    )
  }


  fun highlightNick() = _highlightNick
  override fun setHighlightNick(highlightNick: Int) {
    _highlightNick = IHighlightRuleManager.HighlightNickType.of(highlightNick) ?: _highlightNick
  }

  fun nicksCaseSensitive() = _nicksCaseSensitive
  override fun setNicksCaseSensitive(nicksCaseSensitive: Boolean) {
    _nicksCaseSensitive = nicksCaseSensitive
  }

  fun indexOf(id: Int): Int = _highlightRuleList.indexOfFirst { it.id == id }
  fun contains(id: Int) = _highlightRuleList.any { it.id == id }

  fun isEmpty() = _highlightRuleList.isEmpty()
  fun count() = _highlightRuleList.count()
  fun removeAt(index: Int) {
    _highlightRuleList = _highlightRuleList.drop(index)
  }

  operator fun get(index: Int) = _highlightRuleList[index]
  fun highlightRuleList() = _highlightRuleList
  fun setHighlightRuleList(list: List<HighlightRule>) {
    _highlightRuleList = list
  }

  fun copy() = HighlightRuleManager(proxy).also {
    it.fromVariantMap(toVariantMap())
  }

  private var _highlightRuleList = emptyList<HighlightRule>()
  private var _highlightNick = IHighlightRuleManager.HighlightNickType.CurrentNick
  private var _nicksCaseSensitive = false

  fun isEqual(other: HighlightRuleManager): Boolean =
    this.highlightNick() == other.highlightNick() &&
    this.nicksCaseSensitive() == other.nicksCaseSensitive() &&
    this.highlightRuleList() == other.highlightRuleList()

  override fun toString(): String {
    return "HighlightRuleManager(_highlightRuleList=$_highlightRuleList, _highlightNick=$_highlightNick, _nicksCaseSensitive=$_nicksCaseSensitive)"
  }
}
