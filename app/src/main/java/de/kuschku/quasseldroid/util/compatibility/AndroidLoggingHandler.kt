/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util.compatibility

import android.util.Log
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.quasseldroid.BuildConfig

object AndroidLoggingHandler : LoggingHandler() {
  override fun isLoggable(logLevel: LogLevel, tag: String): Boolean {
    return BuildConfig.DEBUG || Log.isLoggable(tag, priority(logLevel))
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
