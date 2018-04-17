package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.syncables.interfaces.IHighlightRuleManager
import de.kuschku.libquassel.session.SignalProxy

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
  )

  override fun toVariantMap(): QVariantMap = mapOf(
    "HighlightRuleList" to QVariant.of(initHighlightRuleList(), Type.QVariantMap),
    "HighlightNick" to QVariant.of(_highlightNick.value, Type.Int),
    "NicksCaseSensitive" to QVariant.of(_nicksCaseSensitive, Type.Bool)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetHighlightRuleList(properties["HighlightRuleList"].valueOr(::emptyMap))
    _highlightNick = properties["HighlightNick"].value<Int>()?.let {
      IHighlightRuleManager.HighlightNickType.of(it)
    } ?: _highlightNick
    _nicksCaseSensitive = properties["NicksCaseSensitive"].value(_nicksCaseSensitive)
  }

  override fun initHighlightRuleList(): QVariantMap = mapOf(
    "name" to QVariant.of(_highlightRuleList.map {
      QVariant.of(it.name, Type.QString)
    }, Type.QVariantList),
    "isRegEx" to QVariant.of(_highlightRuleList.map {
      QVariant.of(it.isRegEx, Type.Bool)
    }, Type.QStringList),
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
      QVariant.of(it.sender, Type.QString)
    }, Type.QStringList),
    "channel" to QVariant.of(_highlightRuleList.map {
      QVariant.of(it.channel, Type.QString)
    }, Type.QVariantList)
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

  override fun setHighlightNick(highlightNick: Int) {
    _highlightNick = IHighlightRuleManager.HighlightNickType.of(highlightNick) ?: _highlightNick
  }

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

  private var _highlightRuleList = emptyList<HighlightRule>()
  private var _highlightNick = IHighlightRuleManager.HighlightNickType.CurrentNick
  private var _nicksCaseSensitive = false
}
