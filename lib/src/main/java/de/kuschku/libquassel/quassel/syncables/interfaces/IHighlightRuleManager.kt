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

package de.kuschku.libquassel.quassel.syncables.interfaces

import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.annotations.SyncedCall
import de.justjanne.libquassel.annotations.SyncedObject
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.qVariant

@SyncedObject(name = "HighlightRuleManager")
interface IHighlightRuleManager : ISyncableObject {
  enum class HighlightNickType(val value: Int) {
    NoNick(0x00),
    CurrentNick(0x01),
    AllNicks(0x02);

    companion object {
      private val map = values().associateBy(HighlightNickType::value)
      fun of(value: Int) = map[value]
    }
  }

  fun initHighlightRuleList(): QVariantMap
  fun initSetHighlightRuleList(highlightRuleList: QVariantMap)

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestRemoveHighlightRule(highlightRule: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestRemoveHighlightRule",
      qVariant(highlightRule, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun removeHighlightRule(highlightRule: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "removeHighlightRule",
      qVariant(highlightRule, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestToggleHighlightRule(highlightRule: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestToggleHighlightRule",
      qVariant(highlightRule, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun toggleHighlightRule(highlightRule: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "toggleHighlightRule",
      qVariant(highlightRule, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestAddHighlightRule(
    id: Int,
    content: String?,
    isRegEx: Boolean,
    isCaseSensitive: Boolean,
    isEnabled: Boolean,
    isInverse: Boolean,
    sender: String?,
    channel: String?
  ) {
    sync(
      target = ProtocolSide.CORE,
      "requestToggleHighlightRule",
      qVariant(id, QtType.Int),
      qVariant(content, QtType.QString),
      qVariant(isRegEx, QtType.Bool),
      qVariant(isCaseSensitive, QtType.Bool),
      qVariant(isEnabled, QtType.Bool),
      qVariant(isInverse, QtType.Bool),
      qVariant(sender, QtType.QString),
      qVariant(channel, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun addHighlightRule(
    id: Int,
    content: String?,
    isRegEx: Boolean,
    isCaseSensitive: Boolean,
    isEnabled: Boolean,
    isInverse: Boolean,
    sender: String?,
    channel: String?
  ) {
    sync(
      target = ProtocolSide.CLIENT,
      "addHighlightRule",
      qVariant(id, QtType.Int),
      qVariant(content, QtType.QString),
      qVariant(isRegEx, QtType.Bool),
      qVariant(isCaseSensitive, QtType.Bool),
      qVariant(isEnabled, QtType.Bool),
      qVariant(isInverse, QtType.Bool),
      qVariant(sender, QtType.QString),
      qVariant(channel, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetHighlightNick(highlightNick: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetHighlightNick",
      qVariant(highlightNick, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setHighlightNick(highlightNick: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setHighlightNick",
      qVariant(highlightNick, QtType.Int),
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetNicksCaseSensitive(nicksCaseSensitive: Boolean) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetNicksCaseSensitive",
      qVariant(nicksCaseSensitive, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setNicksCaseSensitive(nicksCaseSensitive: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setNicksCaseSensitive",
      qVariant(nicksCaseSensitive, QtType.Bool),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)
}
