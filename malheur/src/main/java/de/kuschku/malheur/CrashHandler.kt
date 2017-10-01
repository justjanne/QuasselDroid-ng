package de.kuschku.malheur

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.os.Environment
import android.os.Process
import android.provider.Settings
import com.google.gson.GsonBuilder
import de.kuschku.malheur.data.*
import de.kuschku.malheur.util.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CrashHandler {
  private lateinit var packageManager: PackageManager
  private lateinit var config: ReportConfiguration

  private lateinit var originalHandler: Thread.UncaughtExceptionHandler

  private val logcatTimeFormatter = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)
  private val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

  data class ReportConfiguration(
    val crash: Boolean = true,
    val crashCause: Boolean = true,
    val crashException: Boolean = true,
    val crashActiveThread: Boolean = true,
    val crashStartTime: Boolean = true,
    val crashCrashTime: Boolean = true,
    val threads: Boolean = true,
    val logcat: List<String> = listOf("main", "events", "crash"),
    val application: Boolean = true,
    val applicationVersionName: Boolean = true,
    val applicationVersionCode: Boolean = true,
    val applicationBuildConfig: Boolean = true,
    val device: Boolean = true,
    val deviceBuild: Boolean = true,
    val deviceVersion: Boolean = true,
    val deviceInstallationId: Boolean = true,
    val deviceProcessor: Boolean = true,
    val deviceRuntime: Boolean = true,
    val environment: Boolean = true,
    val environmentPaths: Boolean = true,
    val environmentMemory: Boolean = true
  )

  @SuppressLint("HardwareIds")
  fun init(application: Application, configuration: ReportConfiguration = ReportConfiguration(),
           buildConfig: Class<*>?) {
    val startTime = Date()
    packageManager = application.packageManager
    originalHandler = Thread.getDefaultUncaughtExceptionHandler()

    config = configuration

    Thread.setDefaultUncaughtExceptionHandler { activeThread, throwable ->
      Thread {
        val pid = Process.myPid().toString()
        val crashTime = Date()
        try {
          val since = logcatTimeFormatter.format(startTime)
          val data = Report(
            crash = CrashInfo(
              cause = orNull(config.crashCause) {
                ExceptionInfo(throwable)
              },
              exception = orNull(config.crashException) {
                throwable.printStackTraceToString()
              },
              activeThread = orNull(config.crashActiveThread) {
                ThreadInfo(activeThread)
              },
              startTime = orNull(config.crashStartTime) {
                startTime.time
              },
              crashTime = orNull(config.crashCrashTime) {
                crashTime.time
              }
            ),
            threads = orNull(config.threads) {
              Thread.getAllStackTraces()
                .filterKeys { it != Thread.currentThread() }
                .map { (thread, stackTrace) ->
                  ThreadInfo(thread, stackTrace)
                }
            },
            logcat = config.logcat.map { buffer ->
              buffer to readLogCat(since, buffer, pid)
            }.toMap(),
            application = orNull(config.application) {
              AppInfo(
                versionName = orNull(config.applicationVersionName) {
                  packageManager.getPackageInfo(application.packageName, 0).versionName
                },
                versionCode = orNull(config.applicationVersionCode) {
                  packageManager.getPackageInfo(application.packageName, 0).versionCode
                },
                buildConfig = orNull(config.applicationBuildConfig) {
                  reflectionCollectConstants(
                    buildConfig ?: getBuildConfigClass(application.packageName))
                }
              )
            },
            device = orNull(config.device) {
              DeviceInfo(
                build = orNull(config.deviceBuild) {
                  reflectionCollectConstants(Build::class.java)
                },
                version = orNull(config.deviceVersion) {
                  reflectionCollectConstants(Build.VERSION::class.java)
                },
                installationId = orNull(config.deviceInstallationId) {
                  Settings.Secure.getString(
                    application.contentResolver, Settings.Secure.ANDROID_ID
                  )
                },
                processor = orNull(config.deviceProcessor) {
                  readProcInfo()
                }
              )
            },
            environment = orNull(config.environment) {
              mapOf(
                "paths" to orNull(config.environmentPaths) {
                  reflectionCollectGetters(Environment::class.java)?.map { (key, value) ->
                    key to if (value is File) {
                      value.canonicalPath
                    } else {
                      value
                    }
                  }?.toMap()
                },
                "memory" to orNull(config.environmentMemory) {
                  val memoryInfo = Debug.MemoryInfo()
                  Debug.getMemoryInfo(memoryInfo)
                  MemoryInfo(memoryInfo)
                }
              )
            }
          )

          val json = gson.toJson(data)
          println(json)
        } catch (e: Throwable) {
          originalHandler.uncaughtException(activeThread, throwable)
        }
      }.start()
    }
  }

  private fun getBuildConfigClass(packageName: String) = try {
    Class.forName("$packageName.BuildConfig")
  } catch (e: ClassNotFoundException) {
    null
  }

  private fun <T> orNull(condition: Boolean, closure: (() -> T)) = if (condition) {
    closure()
  } else {
    null
  }
}
