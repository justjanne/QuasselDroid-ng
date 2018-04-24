/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util.backport

import android.content.Context
import org.threeten.bp.zone.TzdbZoneRulesProvider
import org.threeten.bp.zone.ZoneRulesProvider
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean

object AndroidThreeTenBackport {
  private val initialized = AtomicBoolean()

  fun init(context: Context) {
    if (initialized.getAndSet(true)) {
      return
    }

    val provider: TzdbZoneRulesProvider
    var inputStream: InputStream? = null
    try {
      inputStream = context.assets.open("org/threeten/bp/TZDB.dat")
      provider = TzdbZoneRulesProvider(inputStream)
    } catch (e: IOException) {
      throw IllegalStateException("TZDB.dat missing from assets.", e)
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close()
        } catch (ignored: IOException) {
        }

      }
    }

    ZoneRulesProvider.registerProvider(provider)
  }
}
