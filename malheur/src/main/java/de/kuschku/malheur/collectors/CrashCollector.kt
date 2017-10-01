package de.kuschku.malheur.collectors

import android.app.Application
import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.config.CrashConfig
import de.kuschku.malheur.data.CrashInfo
import de.kuschku.malheur.data.ExceptionInfo
import de.kuschku.malheur.data.ThreadInfo
import de.kuschku.malheur.util.printStackTraceToString
import java.text.SimpleDateFormat
import java.util.*

class CrashCollector(application: Application) : Collector<CrashInfo, CrashConfig> {
  private val configurationCollector = ConfigurationCollector(application)
  private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)

  override fun collect(context: CrashContext, config: CrashConfig) = CrashInfo(
    cause = collectIf(config.cause) {
      ExceptionInfo(context.throwable)
    },
    exception = collectIf(config.exception) {
      context.throwable.printStackTraceToString()
    },
    activeThread = collectIf(config.activeThread) {
      ThreadInfo(context.crashingThread)
    },
    startTime = collectIf(config.startTime) {
      isoFormatter.format(context.startTime)
    },
    crashTime = collectIf(config.crashTime) {
      isoFormatter.format(context.crashTime)
    },
    configuration = configurationCollector.collectIf(context, config.configuration)
  )
}
