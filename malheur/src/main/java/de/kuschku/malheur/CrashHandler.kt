/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
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

package de.kuschku.malheur

import android.app.Application
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import de.kuschku.malheur.collectors.ReportCollector
import de.kuschku.malheur.config.ReportConfig
import java.io.File
import java.util.*

object CrashHandler {
  private val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

  private val startTime = Date()
  private var originalHandler: Thread.UncaughtExceptionHandler? = null

  private lateinit var handler: Handler

  fun init(application: Application, config: ReportConfig = ReportConfig(),
           buildConfig: Class<*>?) {
    if (myHandler == null) {
      originalHandler = Thread.getDefaultUncaughtExceptionHandler()
    }

    val handlerThread = HandlerThread("Malheur")
    handlerThread.start()
    handler = Handler(handlerThread.looper)

    val reportCollector = ReportCollector(application)
    myHandler = Thread.UncaughtExceptionHandler { activeThread, throwable ->
      val crashTime = Date()
      val stackTraces = Thread.getAllStackTraces()
      Log.e("Malheur", "Creating crash report")
      handler.post {
        Toast.makeText(application, "Creating crash report", Toast.LENGTH_LONG).show()
      }
      Thread {
        try {
          val json = gson.toJson(
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
          originalHandler?.uncaughtException(activeThread, throwable)
        }
      }.start()
    }
    Thread.setDefaultUncaughtExceptionHandler(myHandler)
  }

  fun handle(throwable: Throwable) {
    myHandler?.uncaughtException(Thread.currentThread(), throwable)
  }

  private var myHandler: Thread.UncaughtExceptionHandler? = null
}
