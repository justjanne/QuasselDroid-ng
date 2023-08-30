/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2023 Janne Mareike Koschinski
 * Copyright (c) 2023 The Quassel Project
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

package de.kuschku.quasseldroid.defaults

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.DecodeSequenceMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeToSequence
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
class DefaultNetworks @Inject constructor(context: Context) {
  val networks: List<DefaultNetwork> by lazy {
    try {
      context.assets.open("networks.json").use {
        Json.decodeToSequence<DefaultNetwork>(it, DecodeSequenceMode.ARRAY_WRAPPED).toList()
      }
    } catch (e: IOException) {
      throw IllegalStateException("networks.json missing from assets.", e)
    }
  }
}
