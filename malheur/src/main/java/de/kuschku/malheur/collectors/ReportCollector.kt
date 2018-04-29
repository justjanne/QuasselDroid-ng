/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
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
import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.config.ReportConfig
import de.kuschku.malheur.data.Report

class ReportCollector(application: Application) : Collector<Report, ReportConfig> {
  private val crashCollector = CrashCollector()
  private val threadCollector = ThreadCollector()
  private val logcatCollector = LogCollector()
  private val applicationCollector = AppCollector(application)
  private val deviceCollector = DeviceCollector(application)
  private val environmentCollector = EnvCollector(application)

  override fun collect(context: CrashContext, config: ReportConfig) = Report(
    crash = crashCollector.collectIf(context, config.crash),
    threads = threadCollector.collectIf(context, config.threads),
    /*logcat = logcatCollector.collectIf(context, config.logcat),*/
    application = applicationCollector.collectIf(context, config.application),
    device = deviceCollector.collectIf(context, config.device),
    environment = environmentCollector.collectIf(context, config.environment)
  )
}
