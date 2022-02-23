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
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.util.flag.Flags

object ClientInitSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientInit> {
  override fun serialize(data: HandshakeMessage.ClientInit) = mapOf(
    "MsgType" to QVariant.of("ClientInit", QtType.QString),
    "ClientVersion" to QVariant.of(data.clientVersion, QtType.QString),
    "ClientDate" to QVariant.of(data.buildDate, QtType.QString),
    "Features" to QVariant.of(data.clientFeatures?.toUInt(), QtType.UInt),
    "FeatureList" to QVariant.of(data.featureList, QtType.QStringList)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientInit(
    clientVersion = data["ClientVersion"].value(),
    buildDate = data["ClientDate"].value(),
    clientFeatures = Flags.of(data["Features"].value(0u)),
    featureList = data["FeatureList"].value(emptyList())
  )
}
