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

@SyncedObject("NetworkConfig")
interface INetworkConfig : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)
  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetAutoWhoDelay(delay: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetAutoWhoDelay",
      qVariant(delay, QtType.Int)
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoWhoDelay(delay: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoWhoDelay",
      qVariant(delay, QtType.Int)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetAutoWhoEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetAutoWhoEnabled",
      qVariant(enabled, QtType.Bool)
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoWhoEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoWhoEnabled",
      qVariant(enabled, QtType.Bool)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetAutoWhoInterval(interval: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetAutoWhoInterval",
      qVariant(interval, QtType.Int)
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun setAutoWhoInterval(interval: Int) {
    sync(
      target = ProtocolSide.CLIENT,
      "setAutoWhoInterval",
      qVariant(interval, QtType.Int)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetAutoWhoNickLimit(limit: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetAutoWhoNickLimit",
      qVariant(limit, QtType.Int)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun setAutoWhoNickLimit(limit: Int) {
    sync(
      target = ProtocolSide.CORE,
      "setAutoWhoNickLimit",
      qVariant(limit, QtType.Int)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetMaxPingCount(count: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetMaxPingCount",
      qVariant(count, QtType.Int)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun setMaxPingCount(count: Int) {
    sync(
      target = ProtocolSide.CORE,
      "setMaxPingCount",
      qVariant(count, QtType.Int)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetPingInterval(interval: Int) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetPingInterval",
      qVariant(interval, QtType.Int)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun setPingInterval(interval: Int) {
    sync(
      target = ProtocolSide.CORE,
      "setPingInterval",
      qVariant(interval, QtType.Int)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetPingTimeoutEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetPingTimeoutEnabled",
      qVariant(enabled, QtType.Bool)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun setPingTimeoutEnabled(enabled: Boolean) {
    sync(
      target = ProtocolSide.CORE,
      "setPingTimeoutEnabled",
      qVariant(enabled, QtType.Bool)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun requestSetStandardCtcp(enabled: Boolean) {
    sync(
      target = ProtocolSide.CORE,
      "requestSetStandardCtcp",
      qVariant(enabled, QtType.Bool)
    )
  }

  @SyncedCall(target = ProtocolSide.CORE)
  fun setStandardCtcp(enabled: Boolean) {
    sync(
      target = ProtocolSide.CORE,
      "setStandardCtcp",
      qVariant(enabled, QtType.Bool)
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  override fun update(properties: QVariantMap) = super.update(properties)

  @SyncedCall(target = ProtocolSide.CORE)
  override fun requestUpdate(properties: QVariantMap) = super.requestUpdate(properties)
}
