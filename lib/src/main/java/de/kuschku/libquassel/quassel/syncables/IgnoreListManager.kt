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
import de.kuschku.libquassel.quassel.syncables.interfaces.IIgnoreListManager
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.ExpressionMatch
import de.kuschku.libquassel.util.flag.and
import io.reactivex.subjects.BehaviorSubject
import java.io.Serializable

class IgnoreListManager constructor(
  var session: ISession
) : SyncableObject(session.proxy, "IgnoreListManager"), IIgnoreListManager {
  override fun deinit() {
    super.deinit()
    session = ISession.NULL
  }

  override fun toVariantMap(): QVariantMap = mapOf(
    "IgnoreList" to QVariant.of(initIgnoreList(), Type.QVariantMap)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetIgnoreList(properties["IgnoreList"].valueOr(::emptyMap))
  }

  override fun initIgnoreList() = mapOf(
    "ignoreType" to QVariant.of(_ignoreList.map {
      QVariant.of(it.type.value, Type.Int)
    }, Type.QVariantList),
    "ignoreRule" to QVariant.of(_ignoreList.map {
      it.ignoreRule
    }, Type.QStringList),
    "isRegEx" to QVariant.of(_ignoreList.map {
      QVariant.of(it.isRegEx, Type.Bool)
    }, Type.QVariantList),
    "strictness" to QVariant.of(_ignoreList.map {
      QVariant.of(it.strictness.value, Type.Int)
    }, Type.QVariantList),
    "scope" to QVariant.of(_ignoreList.map {
      QVariant.of(it.scope.value, Type.Int)
    }, Type.QVariantList),
    "scopeRule" to QVariant.of(_ignoreList.map {
      it.scopeRule
    }, Type.QStringList),
    "isActive" to QVariant.of(_ignoreList.map {
      QVariant.of(it.isActive, Type.Bool)
    }, Type.QVariantList)
  )

  override fun initSetIgnoreList(ignoreList: QVariantMap) {
    val ignoreTypeList = ignoreList["ignoreType"].valueOr<QVariantList>(::emptyList)
    val ignoreRuleList = ignoreList["ignoreRule"].valueOr<QStringList>(::emptyList)
    val isRegExList = ignoreList["isRegEx"].valueOr<QVariantList>(::emptyList)
    val strictnessList = ignoreList["strictness"].valueOr<QVariantList>(::emptyList)
    val scopeList = ignoreList["scope"].valueOr<QVariantList>(::emptyList)
    val scopeRuleList = ignoreList["scopeRule"].valueOr<QStringList>(::emptyList)
    val isActiveList = ignoreList["isActive"].valueOr<QVariantList>(::emptyList)
    val size = ignoreTypeList.size
    if (ignoreRuleList.size != size || isRegExList.size != size || strictnessList.size != size ||
        scopeList.size != size || scopeRuleList.size != size || isActiveList.size != size)
      return

    _ignoreList = List(size) {
      IgnoreListItem(
        type = ignoreTypeList[it].value(0),
        ignoreRule = ignoreRuleList[it] ?: "",
        isRegEx = isRegExList[it].value(false),
        strictness = strictnessList[it].value(0),
        scope = scopeList[it].value(0),
        scopeRule = scopeRuleList[it] ?: "",
        isActive = isActiveList[it].value(false)
      )
    }
  }

  override fun addIgnoreListItem(type: Int, ignoreRule: String?, isRegEx: Boolean, strictness: Int,
                                 scope: Int, scopeRule: String?, isActive: Boolean) {
    if (contains(ignoreRule)) return

    _ignoreList += IgnoreListItem(type, ignoreRule, isRegEx, strictness, scope, scopeRule, isActive)
  }

  override fun removeIgnoreListItem(ignoreRule: String?) = removeAt(indexOf(ignoreRule))

  override fun toggleIgnoreRule(ignoreRule: String?) {
    _ignoreList = _ignoreList.map {
      if (it.ignoreRule == ignoreRule) it.copy(isActive = !it.isActive) else it
    }
  }

  fun indexOf(ignore: String?): Int = _ignoreList.indexOfFirst { it.ignoreRule == ignore }
  fun contains(ignore: String?) = _ignoreList.any { it.ignoreRule == ignore }
  fun isEmpty() = _ignoreList.isEmpty()
  fun count() = _ignoreList.count()
  fun removeAt(index: Int) {
    _ignoreList = _ignoreList.drop(index)
  }

  operator fun get(index: Int) = _ignoreList[index]
  fun ignoreList() = _ignoreList
  fun setIgnoreList(list: List<IgnoreListItem>) {
    _ignoreList = list
  }

  fun matchingRules(sender: String) = _ignoreList.filter {
    it.type == IgnoreType.SenderIgnore &&
    it.regEx.match(sender)
  }

  fun liveMatchingRules(sender: String) = live_updates.map {
    matchingRules(sender)
  }

  fun updates() = live_updates.map { this }

  fun copy() = IgnoreListManager(session).also {
    it.fromVariantMap(toVariantMap())
  }

  enum class IgnoreType(val value: Int) {
    SenderIgnore(0),
    MessageIgnore(1),
    CtcpIgnore(2);

    companion object {
      private val byId = enumValues<IgnoreType>().associateBy(IgnoreType::value)
      fun of(value: Int) = byId[value] ?: IgnoreType.SenderIgnore
    }
  }

  enum class StrictnessType(val value: Int) {
    UnmatchedStrictness(0),
    SoftStrictness(1),
    HardStrictness(2);

    companion object {
      private val byId = enumValues<StrictnessType>().associateBy(StrictnessType::value)
      fun of(value: Int) = byId[value] ?: StrictnessType.UnmatchedStrictness
    }
  }

  enum class ScopeType(val value: Int) {
    GlobalScope(0),
    NetworkScope(1),
    ChannelScope(2);

    companion object {
      private val byId = enumValues<ScopeType>().associateBy(ScopeType::value)
      fun of(value: Int) = byId[value] ?: ScopeType.GlobalScope
    }
  }

  class IgnoreListItem private constructor(
    val type: IgnoreType,
    val ignoreRule: String,
    val isRegEx: Boolean,
    val strictness: StrictnessType,
    val scope: ScopeType,
    val scopeRule: String,
    val isActive: Boolean,
    val regEx: ExpressionMatch,
    val scopeRegEx: ExpressionMatch
  ) : Serializable {
    constructor(type: Int, ignoreRule: String?, isRegEx: Boolean, strictness: Int, scope: Int,
                scopeRule: String?, isActive: Boolean) : this(
      IgnoreType.of(type), ignoreRule, isRegEx, StrictnessType.of(strictness), ScopeType.of(scope),
      scopeRule, isActive
    )

    constructor(type: IgnoreType, ignoreRule: String?, isRegEx: Boolean, strictness: StrictnessType,
                scope: ScopeType, scopeRule: String?, isActive: Boolean) : this(
      type, ignoreRule ?: "", isRegEx, strictness, scope, scopeRule ?: "", isActive,
      ExpressionMatch(
        ignoreRule ?: "",
        if (isRegEx) ExpressionMatch.MatchMode.MatchRegEx
        else ExpressionMatch.MatchMode.MatchWildcard,
        caseSensitive = false
      ),
      ExpressionMatch(
        scopeRule ?: "",
        ExpressionMatch.MatchMode.MatchMultiWildcard,
        caseSensitive = false
      )
    )

    fun copy(
      type: IgnoreType = this.type,
      ignoreRule: String = this.ignoreRule,
      isRegEx: Boolean = this.isRegEx,
      strictness: StrictnessType = this.strictness,
      scope: ScopeType = this.scope,
      scopeRule: String = this.scopeRule,
      isActive: Boolean = this.isActive
    ) = IgnoreListItem(
      type = type,
      ignoreRule = ignoreRule,
      isRegEx = isRegEx,
      strictness = strictness,
      scope = scope,
      scopeRule = scopeRule,
      isActive = isActive,
      regEx = ExpressionMatch(
        ignoreRule,
        if (isRegEx) ExpressionMatch.MatchMode.MatchRegEx
        else ExpressionMatch.MatchMode.MatchWildcard,
        caseSensitive = false
      ),
      scopeRegEx = ExpressionMatch(
        scopeRule,
        ExpressionMatch.MatchMode.MatchMultiWildcard,
        caseSensitive = false
      )
    )

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as IgnoreListItem

      if (type != other.type) return false
      if (ignoreRule != other.ignoreRule) return false
      if (isRegEx != other.isRegEx) return false
      if (strictness != other.strictness) return false
      if (scope != other.scope) return false
      if (scopeRule != other.scopeRule) return false
      if (isActive != other.isActive) return false

      return true
    }

    override fun hashCode(): Int {
      var result = type.hashCode()
      result = 31 * result + ignoreRule.hashCode()
      result = 31 * result + isRegEx.hashCode()
      result = 31 * result + strictness.hashCode()
      result = 31 * result + scope.hashCode()
      result = 31 * result + scopeRule.hashCode()
      result = 31 * result + isActive.hashCode()
      return result
    }

    override fun toString(): String {
      return "IgnoreListItem(type=$type, ignoreRule='$ignoreRule', isRegEx=$isRegEx, strictness=$strictness, scope=$scope, scopeRule='$scopeRule', isActive=$isActive)"
    }
  }

  fun match(msgContents: String, msgSender: String, msgType: Message_Types, network: String,
            bufferName: String): StrictnessType {
    if ((Message_Type.of(Message_Type.Plain, Message_Type.Notice, Message_Type.Action) and msgType)
        .isEmpty()) return StrictnessType.UnmatchedStrictness

    return _ignoreList.filter {
      it.isActive && it.type != IgnoreType.CtcpIgnore
    }.filter {
      it.scope == ScopeType.GlobalScope ||
      it.scope == ScopeType.NetworkScope && it.scopeRegEx.match(network) ||
      it.scope == ScopeType.ChannelScope && it.scopeRegEx.match(bufferName)
    }.filter {
      val content = if (it.type == IgnoreType.MessageIgnore) msgContents else msgSender
      it.regEx.match(content)
    }.map {
      it.strictness
    }.sortedByDescending {
      it.value
    }.firstOrNull() ?: StrictnessType.UnmatchedStrictness
  }

  private val live_updates = BehaviorSubject.createDefault(Unit)
  private var _ignoreList = emptyList<IgnoreListItem>()
    set(value) {
      field = value
      live_updates.onNext(Unit)
      if (initialized) session.backlogManager.updateIgnoreRules()
    }

  fun isEqual(other: IgnoreListManager): Boolean =
    this.ignoreList() == other.ignoreList()

  override fun toString(): String {
    return "IgnoreListManager(_ignoreList=$_ignoreList)"
  }
}
