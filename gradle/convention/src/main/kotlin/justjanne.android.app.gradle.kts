import java.io.ByteArrayOutputStream
import java.util.*

plugins {
  id("com.android.application")
  id("justjanne.kotlin.android")
}

android {
  compileSdk = 30

  defaultConfig {
    minSdk = 21
    targetSdk = 30

    applicationId = "${rootProject.group}.${rootProject.name/*.lowercase(Locale.ROOT)*/}"
    versionCode = cmd("git", "rev-list", "--count", "HEAD")?.toIntOrNull() ?: 1
    versionName = cmd("git", "describe", "--always", "--tags", "HEAD") ?: "1.0.0"

    buildConfigField("String", "GIT_HEAD", "\"${cmd("git", "rev-parse", "HEAD") ?: ""}\"")
    buildConfigField("String", "FANCY_VERSION_NAME", "\"${fancyVersionName() ?: ""}\"")
    buildConfigField("long", "GIT_COMMIT_DATE", "${cmd("git", "show", "-s", "--format=%ct") ?: 0}L")

    signingConfig = signingConfigs.findByName("default")

    setProperty("archivesBaseName", "${rootProject.name}-$versionName")

    // Disable test runner analytics
    testInstrumentationRunnerArguments["disableAnalytics"] = "true"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
  }

  lint {
    warningsAsErrors = true
    lintConfig = file("../lint.xml")
  }
}

fun Project.fancyVersionName(): String? {
  val commit = cmd("git", "rev-parse", "HEAD")
  val name = cmd("git", "describe", "--always", "--tags", "HEAD")

  return if (commit != null && name != null) "<a href=\\\"https://git.kuschku.de/justJanne/QuasselDroid-ng/commit/$commit\\\">$name</a>"
  else name
}

fun Project.cmd(vararg command: String) = try {
  val stdOut = ByteArrayOutputStream()
  exec {
    commandLine(*command)
    standardOutput = stdOut
  }
  stdOut.toString(Charsets.UTF_8.name()).trim()
} catch (e: Throwable) {
  e.printStackTrace()
  null
}

fun Project.properties(fileName: String): Properties? {
  val file = file(fileName)
  if (!file.exists())
    return null
  val props = Properties()
  props.load(file.inputStream())
  return props
}

data class SigningData(
  val storeFile: String,
  val storePassword: String,
  val keyAlias: String,
  val keyPassword: String
) {
  companion object {
    fun of(properties: Properties?): SigningData? {
      if (properties == null) return null

      val storeFile = properties.getProperty("storeFile") ?: return null
      val storePassword = properties.getProperty("storePassword") ?: return null
      val keyAlias = properties.getProperty("keyAlias") ?: return null
      val keyPassword = properties.getProperty("keyPassword") ?: return null

      return SigningData(storeFile, storePassword, keyAlias, keyPassword)
    }
  }
}
