/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.malheur.collectors

import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.config.CrashConfig
import de.kuschku.malheur.data.CrashInfo
import de.kuschku.malheur.data.ExceptionInfo
import java.io.PrintWriter
import java.io.StringWriter

class CrashCollector : Collector<CrashInfo, CrashConfig> {
  override fun collect(context: CrashContext, config: CrashConfig) = CrashInfo(
    cause = collectIf(config.cause) {
      ExceptionInfo(context.throwable)
    },
    exception = collectIf(config.exception) {
      val result = StringWriter()
      val printWriter = PrintWriter(result)
      context.throwable.printStackTrace(printWriter)
      result.toString()
    }
  )
}
