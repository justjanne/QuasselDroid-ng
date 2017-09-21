package de.kuschku.quasseldroid_ng.util

import android.util.Log
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.LogRecord

/**
 * Make JUL work on Android.
 */
class AndroidLoggingHandler : Handler() {
  override fun close() {}
  override fun flush() {}
  override fun publish(record: LogRecord) {
    if (!super.isLoggable(record))
      return

    val name = record.loggerName
    val maxLength = 30
    val tag = if (name.length > maxLength) name.substring(name.length - maxLength) else name

    try {
      val level = getAndroidLevel(record.level)
      Log.println(level, tag, record.message)
      if (record.thrown != null) {
        Log.println(level, tag, Log.getStackTraceString(record.thrown))
      }
    } catch (e: RuntimeException) {
      Log.e("AndroidLoggingHandler", "Error logging message.", e)
    }

  }

  companion object {
    fun reset(rootHandler: Handler) {
      val rootLogger = LogManager.getLogManager().getLogger("")
      val handlers = rootLogger.handlers
      for (handler in handlers) {
        rootLogger.removeHandler(handler)
      }
      rootLogger.addHandler(rootHandler)
    }

    fun init() {
      reset(AndroidLoggingHandler())
    }

    private fun getAndroidLevel(level: Level): Int {
      val value = level.intValue()
      return when {
        value >= 1000 -> Log.ERROR
        value >= 900  -> Log.WARN
        value >= 800  -> Log.INFO
        else          -> Log.DEBUG
      }
    }
  }
}
