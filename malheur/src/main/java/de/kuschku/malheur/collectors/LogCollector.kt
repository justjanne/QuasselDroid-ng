package de.kuschku.malheur.collectors

import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.config.LogConfig
import java.text.SimpleDateFormat
import java.util.*

class LogCollector : Collector<Map<String, List<String>>, LogConfig> {
  private val logcatTimeFormatter = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)

  override fun collect(context: CrashContext, config: LogConfig): Map<String, List<String>> {
    val since = logcatTimeFormatter.format(context.startTime)
    return config.buffers.map { buffer ->
      buffer to readLogCat(since, buffer)
    }.toMap()
  }

  private fun readLogCat(since: String, buffer: String) = ProcessBuilder()
    .command("logcat", "-t", since, "-b", buffer)
    .redirectErrorStream(true)
    .start()
    .inputStream
    .bufferedReader(Charsets.UTF_8)
    .readLines()
}
