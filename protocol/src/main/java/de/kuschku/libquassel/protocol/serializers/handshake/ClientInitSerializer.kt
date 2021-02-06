/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package de.kuschku.libquassel.protocol.serializers.handshake

import de.kuschku.bitflags.toBits
import de.kuschku.bitflags.toFlag
import de.kuschku.libquassel.protocol.features.QuasselFeatureName
import de.kuschku.libquassel.protocol.features.LegacyFeature
import de.kuschku.libquassel.protocol.messages.handshake.ClientInit
import de.kuschku.libquassel.protocol.variant.QVariantMap
import de.kuschku.libquassel.protocol.variant.QtType
import de.kuschku.libquassel.protocol.variant.into
import de.kuschku.libquassel.protocol.variant.qVariant

object ClientInitSerializer : HandshakeSerializer<ClientInit> {
  override fun serialize(data: ClientInit) = mapOf(
    "MsgType" to qVariant("ClientInit", QtType.QString),
    "ClientVersion" to qVariant(data.clientVersion, QtType.QString),
    "ClientDate" to qVariant(data.buildDate, QtType.QString),
    "Features" to qVariant(data.clientFeatures.toBits(), QtType.UInt),
    "FeatureList" to qVariant(
      data.featureList.map(QuasselFeatureName::name),
      QtType.QStringList
    ),
  )

  override fun deserialize(data: QVariantMap): ClientInit {
    return ClientInit(
      clientVersion = data["ClientVersion"].into(),
      buildDate = data["ClientDate"].into(),
      clientFeatures = LegacyFeature.toFlag(data["Features"].into<UInt>()),
      featureList = data["FeatureList"].into(emptyList<String>()).map(::QuasselFeatureName),
    )
  }
}
