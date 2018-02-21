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
