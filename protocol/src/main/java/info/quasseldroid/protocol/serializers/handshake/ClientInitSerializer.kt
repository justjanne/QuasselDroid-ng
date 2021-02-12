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

package info.quasseldroid.protocol.serializers.handshake

import de.justjanne.bitflags.of
import de.justjanne.bitflags.toBits
import info.quasseldroid.protocol.features.LegacyFeature
import info.quasseldroid.protocol.features.QuasselFeatureName
import info.quasseldroid.protocol.messages.handshake.ClientInit
import info.quasseldroid.protocol.variant.QVariantMap
import info.quasseldroid.protocol.variant.QtType
import info.quasseldroid.protocol.variant.into
import info.quasseldroid.protocol.variant.qVariant

object ClientInitSerializer : HandshakeSerializer<ClientInit> {
  override val type: String = "ClientInit"
  override val javaType: Class<out ClientInit> = ClientInit::class.java

  override fun serialize(data: ClientInit) = mapOf(
    "MsgType" to qVariant(type, QtType.QString),
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
      clientFeatures = LegacyFeature.of(data["Features"].into<UInt>()),
      featureList = data["FeatureList"].into(emptyList<String>()).map(::QuasselFeatureName),
    )
  }
}
