/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.malheur

import android.app.Application
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import de.kuschku.malheur.collectors.ReportCollector
import de.kuschku.malheur.config.ReportConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Date

object CrashHandler {
  private val startTime = Date()
  private var originalHandler: Thread.UncaughtExceptionHandler? = null
  private var myHandler: ((Thread, Throwable) -> Unit)? = null

  private lateinit var handler: Handler

  inline fun <reified T> init(application: Application, config: ReportConfig = ReportConfig()) {
    init(application, config, T::class.java)
  }

  fun init(application: Application, config: ReportConfig = ReportConfig(),
           buildConfig: Class<*>?) {
    if (myHandler == null) {
      originalHandler = Thread.getDefaultUncaughtExceptionHandler()
    }

    val handlerThread = HandlerThread("Malheur")
    handlerThread.start()
    handler = Handler(handlerThread.looper)

    val reportCollector = ReportCollector(application)
    myHandler = { activeThread, throwable ->
      val crashTime = Date()
      val stackTraces = Thread.getAllStackTraces()
      Log.e("Malheur", "Creating crash report")
      handler.post {
        Toast.makeText(application, "Creating crash report", Toast.LENGTH_LONG).show()
      }
      try {
        val json = Json.encodeToString(
          reportCollector.collect(
            CrashContext(
              application = application,
              config = config,
              crashingThread = activeThread,
              throwable = throwable,
              startTime = startTime,
              crashTime = crashTime,
              buildConfig = buildConfig,
              stackTraces = stackTraces
            ), config
          )
        )
        val crashDirectory = File(application.cacheDir, "crashes")
        crashDirectory.mkdirs()
        val crashFile = File(crashDirectory, "${System.currentTimeMillis()}.json")
        crashFile.createNewFile()
        crashFile.writeText(json)
        Log.e("Malheur", "Crash report saved: $crashFile", throwable)
        handler.post {
          Toast.makeText(
            application, "Crash report saved: ${crashFile.name}", Toast.LENGTH_LONG
          ).show()
        }
      } catch (e: Throwable) {
        e.printStackTrace()
        throwable.addSuppressed(e)
      }
    }

    Thread.setDefaultUncaughtExceptionHandler { currentThread, throwable ->
      myHandler?.invoke(currentThread, throwable)
      originalHandler?.uncaughtException(currentThread, throwable)
    }

    val oldHandler = Thread.currentThread().uncaughtExceptionHandler
    Thread.currentThread().setUncaughtExceptionHandler { currentThread, throwable ->
      myHandler?.invoke(currentThread, throwable)
      oldHandler?.uncaughtException(currentThread, throwable)
    }
  }

  fun handle(throwable: Throwable) {
    val thread = Thread.currentThread()
    Thread {
      myHandler?.invoke(thread, throwable)
    }.start()
  }

  fun handleSync(throwable: Throwable) {
    myHandler?.invoke(Thread.currentThread(), throwable)
  }
}
