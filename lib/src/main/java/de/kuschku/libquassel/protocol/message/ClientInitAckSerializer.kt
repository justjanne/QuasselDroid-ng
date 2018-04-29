/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.util.flag.Flags

object ClientInitAckSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientInitAck> {
  override fun serialize(data: HandshakeMessage.ClientInitAck) = mapOf(
    "MsgType" to QVariant.of<All_>("ClientInitAck", Type.QString),
    "CoreFeatures" to QVariant.of<All_>(data.coreFeatures?.toInt(), Type.UInt),
    "StorageBackends" to QVariant.of<All_>(data.backendInfo, Type.QVariantList),
    "Authenticator" to QVariant.of<All_>(data.authenticatorInfo, Type.QVariantList),
    "Configured" to QVariant.of<All_>(data.coreConfigured, Type.Bool),
    "FeatureList" to QVariant.of<All_>(data.featureList, Type.QStringList)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientInitAck(
    coreFeatures = Flags.of(data["CoreFeatures"].value(0)),
    backendInfo = data["StorageBackends"].value(),
    authenticatorInfo = data["Authenticators"].value(),
    coreConfigured = data["Configured"].value(),
    featureList = data["FeatureList"].value(emptyList())
  )
}
