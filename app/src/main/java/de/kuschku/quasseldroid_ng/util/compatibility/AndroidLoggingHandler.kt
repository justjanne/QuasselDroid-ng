package de.kuschku.quasseldroid_ng.util.compatibility

import android.util.Log
import de.kuschku.libquassel.util.compatibility.LoggingHandler

object AndroidLoggingHandler : LoggingHandler() {
  override fun isLoggable(logLevel: LogLevel, tag: String): Boolean {
    return true || Log.isLoggable(tag, priority(logLevel))
  }

  override fun log(logLevel: LogLevel, tag: String, message: String?, throwable: Throwable?) {
    val priority = priority(
      logLevel
    )
    if (message != null)
      Log.println(priority, tag, message)
    if (throwable != null)
      Log.println(priority, tag, Log.getStackTraceString(throwable))
  }

  private fun priority(logLevel: LogLevel): Int = when (logLevel) {
    LogLevel.VERBOSE -> Log.VERBOSE
    LogLevel.DEBUG   -> Log.DEBUG
    LogLevel.INFO    -> Log.INFO
    LogLevel.WARN    -> Log.WARN
    LogLevel.ERROR   -> Log.ERROR
    LogLevel.ASSERT  -> Log.ASSERT
  }

  fun inject() {
    LoggingHandler.loggingHandlers.clear()
    LoggingHandler.loggingHandlers.add(this)
  }
}
