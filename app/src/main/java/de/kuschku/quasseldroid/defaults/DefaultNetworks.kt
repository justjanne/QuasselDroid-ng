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

package de.kuschku.quasseldroid.defaults

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import javax.inject.Inject

class DefaultNetworks @Inject constructor(context: Context, gson: Gson) {
  val networks: List<DefaultNetwork> by lazy {
    try {
      context.assets.open("networks.json").use {
        gson.fromJson<List<DefaultNetwork>>(
          it.bufferedReader(Charsets.UTF_8),
          object : TypeToken<List<DefaultNetwork>>() {}.type
        )
      }
    } catch (e: IOException) {
      throw IllegalStateException("networks.json missing from assets.", e)
    }
  }
}
