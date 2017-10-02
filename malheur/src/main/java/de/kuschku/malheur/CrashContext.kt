package de.kuschku.malheur

import android.app.Application
import de.kuschku.malheur.config.ReportConfig
import java.util.*

data class CrashContext(
  val application: Application,
  val config: ReportConfig,
  val crashingThread: Thread,
  val throwable: Throwable,
  val startTime: Date,
  val crashTime: Date,
  val buildConfig: Class<*>?,
  val stackTraces: Map<Thread, Array<StackTraceElement>>?
)
