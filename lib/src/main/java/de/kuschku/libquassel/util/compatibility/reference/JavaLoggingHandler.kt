package de.kuschku.libquassel.util.compatibility.reference

import de.kuschku.libquassel.util.compatibility.LoggingHandler
import java.util.logging.Level
import java.util.logging.Logger

object JavaLoggingHandler : LoggingHandler() {
  override fun isLoggable(logLevel: LogLevel, tag: String): Boolean {
    return Logger.getLogger(tag).isLoggable(
      priority(logLevel)
    )
  }

  override fun log(logLevel: LogLevel, tag: String, message: String?, throwable: Throwable?) {
    val priority = priority(
      logLevel
    )
    val logger = Logger.getLogger(tag)
    if (message != null)
      logger.log(priority, message)
    if (throwable != null)
      logger.log(priority, "", throwable)
  }

  private fun priority(logLevel: LogLevel): Level = when (logLevel) {
    LogLevel.VERBOSE -> Level.FINEST
    LogLevel.DEBUG   -> Level.FINE
    LogLevel.INFO    -> Level.INFO
    LogLevel.WARN    -> Level.WARNING
    LogLevel.ERROR   -> Level.SEVERE
    LogLevel.ASSERT  -> Level.SEVERE
  }

  fun inject() {
    loggingHandlers.clear()
    loggingHandlers.add(this)
  }
}
