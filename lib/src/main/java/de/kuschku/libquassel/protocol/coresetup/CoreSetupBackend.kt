/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.libquassel.protocol.coresetup

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.util.helper.getOr
import java.io.Serializable

data class CoreSetupBackend(
  val backendId: String,
  val displayName: String,
  val description: String,
  val setupData: List<CoreSetupBackendConfigElement>
) : Serializable {
  companion object {
    fun of(props: QVariantMap): CoreSetupBackend {
      val entries = if (!props.containsKey("SetupData")) {
        val result = mutableListOf<CoreSetupBackendConfigElement>()
        val setupDefaults = props["SetupDefaults"]?.value<QVariantMap>(emptyMap()).orEmpty()
        for (key in props["SetupKeys"]?.value<QStringList>(emptyList()).orEmpty()) {
          val default = setupDefaults.getOr(key ?: "", QVariant_.of("", Type.QString))
          result.add(CoreSetupBackendConfigElement(key ?: "", key ?: "", default))
        }
        result
      } else {
        props["SetupData"]?.value<QVariantList>(emptyList()).orEmpty().chunked(3) { (key, displayName, defaultValue) ->
          CoreSetupBackendConfigElement(key.value(""), displayName.value(""), defaultValue)
        }
      }

      val fallback = QVariant_.of("", Type.QString)

      return CoreSetupBackend(
        displayName = props.getOr("DisplayName", fallback).value(""),
        backendId = props.getOr("BackendId", props.getOr("DisplayName", fallback)).value(""),
        description = props.getOr("Description", fallback).value(""),
        setupData = entries
      )
    }
  }
}
