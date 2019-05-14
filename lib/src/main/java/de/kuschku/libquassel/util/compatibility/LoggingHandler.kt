/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
@file:Suppress("NOTHING_TO_INLINE")

package de.kuschku.libquassel.util.compatibility

import de.kuschku.libquassel.util.compatibility.reference.JavaLoggingHandler

abstract class LoggingHandler {
  abstract fun _log(logLevel: LogLevel, tag: String, message: String? = null,
                    throwable: Throwable? = null)

  abstract fun _isLoggable(logLevel: LogLevel, tag: String): Boolean
  inline fun isLoggable(logLevel: LogLevel, tag: String, f: LogContext.() -> Unit) {
    if (_isLoggable(logLevel, tag)) {
      object : LogContext {
        override fun log(message: String?, throwable: Throwable?) {
          this@LoggingHandler._log(logLevel, tag, message, throwable)
        }
      }.f()
    }
  }

  interface LogContext {
    fun log(message: String? = null, throwable: Throwable? = null)
  }

  enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    ASSERT
  }

  companion object {
    val loggingHandlers: MutableSet<LoggingHandler> = mutableSetOf(JavaLoggingHandler)

    inline fun log(logLevel: LogLevel, tag: String, message: String?,
                   throwable: Throwable?) {
      loggingHandlers
        .filter { it._isLoggable(logLevel, tag) }
        .forEach { it._log(logLevel, tag, message, throwable) }
    }

    inline fun log(logLevel: LogLevel, tag: String, throwable: Throwable) =
      log(logLevel, tag, null, throwable)

    inline fun log(logLevel: LogLevel, tag: String, message: String) =
      log(logLevel, tag, message, null)
  }
}
