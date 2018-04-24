package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.syncables.interfaces.IHighlightRuleManager
import de.kuschku.libquassel.session.SignalProxy
import java.io.Serializable

class HighlightRuleManager(
  proxy: SignalProxy
) : SyncableObject(proxy, "HighlightRuleManager"), IHighlightRuleManager {
  data class HighlightRule(
    val name: String,
    val isRegEx: Boolean = false,
    val isCaseSensitive: Boolean = false,
    val isEnabled: Boolean = true,
    val isInverse: Boolean = false,
    val sender: String,
    val channel: String
  ) : Serializable

  override fun toVariantMap(): QVariantMap = mapOf(
    "HighlightRuleList" to QVariant.of(initHighlightRuleList(), Type.QVariantMap)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetHighlightRuleList(properties["HighlightRuleList"].valueOr(::emptyMap))
  }

  override fun initHighlightRuleList(): QVariantMap = mapOf(
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
    }, Type.QStringList),
    "highlightNick" to QVariant.of(_highlightNick.value, Type.Int),
    "nicksCaseSensitive" to QVariant.of(_nicksCaseSensitive, Type.Bool)
  )

  override fun initSetHighlightRuleList(highlightRuleList: QVariantMap) {
    val nameList = highlightRuleList["name"].valueOr<QStringList>(::emptyList)
    val isRegExList = highlightRuleList["isRegEx"].valueOr<QVariantList>(::emptyList)
    val isCaseSensitiveList = highlightRuleList["isCaseSensitive"].valueOr<QVariantList>(::emptyList)
    val isEnabledList = highlightRuleList["isEnabled"].valueOr<QVariantList>(::emptyList)
    val isInverseList = highlightRuleList["isInverse"].valueOr<QVariantList>(::emptyList)
    val senderList = highlightRuleList["sender"].valueOr<QStringList>(::emptyList)
    val channelList = highlightRuleList["channel"].valueOr<QStringList>(::emptyList)
    val size = nameList.size
    if (isRegExList.size != size || isCaseSensitiveList.size != size ||
        isEnabledList.size != size || isInverseList.size != size || senderList.size != size ||
        channelList.size != size)
      return

    _highlightRuleList = List(size, {
      HighlightRule(
        name = nameList[it] ?: "",
        isRegEx = isRegExList[it].value(false),
        isCaseSensitive = isCaseSensitiveList[it].value(false),
        isEnabled = isEnabledList[it].value(false),
        isInverse = isInverseList[it].value(false),
        sender = senderList[it] ?: "",
        channel = channelList[it] ?: ""
      )
    })
    _highlightNick = highlightRuleList["highlightNick"].value<Int>()?.let {
      IHighlightRuleManager.HighlightNickType.of(it)
    } ?: _highlightNick
    _nicksCaseSensitive = highlightRuleList["nicksCaseSensitive"].value(_nicksCaseSensitive)
  }

  override fun removeHighlightRule(highlightRule: String) = removeAt(indexOf(highlightRule))

  override fun toggleHighlightRule(highlightRule: String) {
    _highlightRuleList = _highlightRuleList.map {
      if (it.name == highlightRule) it.copy(isEnabled = !it.isEnabled) else it
    }
  }

  override fun addHighlightRule(name: String, isRegEx: Boolean, isCaseSensitive: Boolean,
                                isEnabled: Boolean, isInverse: Boolean, sender: String,
                                chanName: String) {
    if (contains(name)) return

    _highlightRuleList += HighlightRule(
      name, isRegEx, isCaseSensitive, isEnabled, isInverse, sender, chanName
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

  fun indexOf(name: String): Int = _highlightRuleList.indexOfFirst { it.name == name }
  fun contains(name: String) = _highlightRuleList.any { it.name == name }

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

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as HighlightRuleManager

    if (_highlightRuleList != other._highlightRuleList) return false
    if (_highlightNick != other._highlightNick) return false
    if (_nicksCaseSensitive != other._nicksCaseSensitive) return false

    return true
  }

  override fun hashCode(): Int {
    var result = _highlightRuleList.hashCode()
    result = 31 * result + _highlightNick.hashCode()
    result = 31 * result + _nicksCaseSensitive.hashCode()
    return result
  }

  override fun toString(): String {
    return "HighlightRuleManager(_highlightRuleList=$_highlightRuleList, _highlightNick=$_highlightNick, _nicksCaseSensitive=$_nicksCaseSensitive)"
  }
}
