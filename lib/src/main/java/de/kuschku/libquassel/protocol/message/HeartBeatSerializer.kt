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

package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.value
import org.threeten.bp.Instant

object HeartBeatSerializer : SignalProxyMessageSerializer<SignalProxyMessage.HeartBeat> {
  override fun serialize(data: SignalProxyMessage.HeartBeat) = listOf(
    QVariant.of(RequestType.HeartBeat.value, QtType.Int),
    QVariant.of(data.timestamp, QtType.QDateTime)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.HeartBeat(
    data[0].value(Instant.EPOCH)
  )
}
