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

package de.kuschku.quasseldroid.util.backport

import android.content.Context
import org.threeten.bp.zone.TzdbZoneRulesProvider
import org.threeten.bp.zone.ZoneRulesInitializer
import org.threeten.bp.zone.ZoneRulesProvider
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

object AndroidThreeTenBackport {
  private val initialized = AtomicBoolean()

  fun init(context: Context, assetPath: String = "TZDB.dat") {
    if (!initialized.getAndSet(true)) {
      ZoneRulesInitializer.setInitializer(AndroidThreeTenBackportInitializer(context, assetPath))
    }
  }

  class AndroidThreeTenBackportInitializer(
    private val context: Context,
    private val assetPath: String
  ) : ZoneRulesInitializer() {
    override fun initializeProviders() {
      try {
        ZoneRulesProvider.registerProvider(context.assets.open(assetPath).use {
          TzdbZoneRulesProvider(it)
        })
      } catch (exception: IOException) {
        throw IllegalStateException("$assetPath missing from assets", exception)
      }
    }
  }
}
