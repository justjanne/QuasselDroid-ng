package de.kuschku.malheur.collectors

import android.app.Application
import android.os.Process
import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.config.LogConfig
import de.kuschku.malheur.util.readLogCat
import java.text.SimpleDateFormat
import java.util.*

class LogCollector(application: Application) : Collector<Map<String, List<String>>, LogConfig> {
  private val logcatTimeFormatter = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)
  private val pid = Process.myPid().toString()

  override fun collect(context: CrashContext, config: LogConfig): Map<String, List<String>> {
    val since = logcatTimeFormatter.format(context.startTime)
    return config.buffers.map { buffer ->
      buffer to readLogCat(since, buffer, pid)
    }.toMap()
  }
}
