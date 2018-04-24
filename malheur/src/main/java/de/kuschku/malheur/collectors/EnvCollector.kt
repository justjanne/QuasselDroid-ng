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

package de.kuschku.malheur.collectors

import android.app.Application
import android.os.Debug
import android.os.Environment
import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.config.EnvConfig
import de.kuschku.malheur.data.EnvInfo
import de.kuschku.malheur.data.MemoryInfo
import de.kuschku.malheur.util.reflectionCollectGetters
import java.io.File

class EnvCollector(application: Application) : Collector<EnvInfo, EnvConfig> {
  private val configurationCollector = ConfigurationCollector(application)

  override fun collect(context: CrashContext, config: EnvConfig) = EnvInfo(
    paths = collectIf(config.paths) {
      reflectionCollectGetters(
        Environment::class.java
      )?.map { (key, value) ->
        key to if (value is File) {
          value.canonicalPath
        } else {
          value
        }
      }?.toMap()
    },
    memory = collectIf(config.memory) {
      val memoryInfo = Debug.MemoryInfo()
      Debug.getMemoryInfo(memoryInfo)
      MemoryInfo(memoryInfo)
    },
    configuration = configurationCollector.collectIf(context, config.configuration),
    startTime = collectIf(config.startTime) {
      context.startTime.time
    },
    crashTime = collectIf(config.crashTime) {
      context.crashTime.time
    }
  )
}
