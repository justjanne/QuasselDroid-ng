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
