/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
