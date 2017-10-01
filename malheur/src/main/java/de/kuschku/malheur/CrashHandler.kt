package de.kuschku.malheur

import android.app.Application
import com.google.gson.GsonBuilder
import de.kuschku.malheur.collectors.ReportCollector
import de.kuschku.malheur.config.ReportConfig
import java.util.*

object CrashHandler {
  private val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

  private val startTime = Date()
  private var originalHandler: Thread.UncaughtExceptionHandler? = null

  fun init(application: Application, config: ReportConfig = ReportConfig(),
           buildConfig: Class<*>?) {
    if (myHandler == null) {
      originalHandler = Thread.getDefaultUncaughtExceptionHandler()
    }

    val reportCollector = ReportCollector(application)
    myHandler = Thread.UncaughtExceptionHandler { activeThread, throwable ->
      Thread {
        try {
          val json = gson.toJson(reportCollector.collect(CrashContext(
            application = application,
            config = config,
            crashingThread = activeThread,
            throwable = throwable,
            startTime = startTime,
            crashTime = Date(),
            buildConfig = buildConfig
          ), config))
          println(json)
        } catch (e: Throwable) {
          originalHandler?.uncaughtException(activeThread, throwable)
        }
      }.start()
    }
    Thread.setDefaultUncaughtExceptionHandler(myHandler)
  }

  var myHandler: Thread.UncaughtExceptionHandler? = null
}
