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

package de.kuschku.libquassel.protocol.coresetup

import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.quassel.QuasselFeatures
import java.io.Serializable

data class CoreSetupData(
  val backendInfo: List<CoreSetupBackend>,
  val authenticatorInfo: List<CoreSetupBackend>,
  val features: QuasselFeatures
) : Serializable {
  companion object {
    fun of(data: HandshakeMessage.ClientInitAck): CoreSetupData {

      return CoreSetupData(
        backendInfo = data.backendInfo.orEmpty().map {
          CoreSetupBackend.of(it.value(emptyMap()))
        },
        authenticatorInfo = data.authenticatorInfo.orEmpty().map {
          CoreSetupBackend.of(it.value(emptyMap()))
        },
        features = QuasselFeatures(data.coreFeatures, data.featureList)
      )
    }
  }
}
