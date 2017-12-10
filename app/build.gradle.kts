import org.gradle.api.Project
import java.io.FileInputStream
import java.util.*
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.util.*

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
}

android {
  compileSdkVersion(26)
  buildToolsVersion("27.0.2")

  signingConfigs {
    val signing = project.rootProject.properties("signing.properties")
    if (signing != null) {
      create("default") {
        storeFile = file(signing.getProperty("storeFile"))
        storePassword = signing.getProperty("storePassword")
        keyAlias = signing.getProperty("keyAlias")
        keyPassword = signing.getProperty("keyPassword")
      }
    }
  }

  defaultConfig {
    minSdkVersion(16)
    targetSdkVersion(26)

    applicationId = "de.kuschku.quasseldroid_ng"
    versionCode = 1
    versionName = cmd("git", "describe", "--tags", "HEAD") ?: "1.0.0"

    buildConfigField("String", "GIT_HEAD", "\"${cmd("git", "rev-parse", "HEAD") ?: ""}\"")
    buildConfigField("long", "GIT_COMMIT_DATE", "${cmd("git", "show", "-s", "--format=%ct") ?: 0}L")

    signingConfig = signingConfigs.getByName("default")

    resConfigs("en")

    vectorDrawables.useSupportLibrary = true

    setProperty("archivesBaseName", "QuasselDroidNG-$versionName")

    javaCompileOptions {
      annotationProcessorOptions {
        arguments = mapOf("room.schemaLocation" to "$projectDir/schemas")
      }
    }

    // Disable test runner analytics
    testInstrumentationRunnerArguments = mapOf(
      "disableAnalytics" to "true"
    )
  }

  buildTypes {
    getByName("release") {
      isZipAlignEnabled = true
      isMinifyEnabled = true
      isShrinkResources = true

      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "proguard-rules.pro"
      )
    }

    getByName("debug") {
      applicationIdSuffix = "debug"

      isZipAlignEnabled = true
      isMinifyEnabled = true
      isShrinkResources = true

      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "proguard-rules.pro"
      )
    }
  }
}

val appCompatVersion = "27.0.2"
val appArchVersion = "1.0.0"
dependencies {
  implementation(kotlin("stdlib", "1.2.0"))

  implementation(appCompat("appcompat-v7"))
  implementation(appCompat("design"))
  implementation(appCompat("customtabs"))
  implementation(appCompat("cardview-v7"))
  implementation(appCompat("recyclerview-v7"))
  implementation(appCompat("constraint", "constraint-layout", version = "1.0.2"))

  implementation("com.github.StephenVinouze.AdvancedRecyclerView", "core", "1.1.6")

  implementation("io.reactivex.rxjava2", "rxjava", "2.1.3")

  implementation(appArch("lifecycle", "extensions"))
  implementation(appArch("lifecycle", "reactivestreams"))
  kapt(appArch("lifecycle", "compiler"))

  implementation(appArch("persistence.room", "runtime"))
  kapt(appArch("persistence.room", "compiler"))

  implementation(appArch("paging", "runtime", version = "1.0.0-alpha3")) {
    exclude(group = "junit", module = "junit")
  }

  implementation("me.zhanghai.android.materialprogressbar", "library", "1.4.2")

  implementation("org.threeten", "threetenbp", "1.3.6", classifier = "no-tzdb")

  implementation("com.jakewharton", "butterknife", "8.8.1")
  kapt("com.jakewharton", "butterknife-compiler", "8.8.1")

  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }
  implementation(project(":malheur"))

  debugImplementation("com.squareup.leakcanary", "leakcanary-android", "1.5.1")

  testImplementation(appArch("persistence.room", "testing"))
  testImplementation("junit", "junit", "4.12")

  androidTestImplementation("com.android.support.test", "runner", "1.0.1")
  androidTestImplementation("com.android.support.test", "rules", "1.0.1")

  androidTestImplementation("com.android.support.test.espresso", "espresso-core", "3.0.1")
}

tasks.withType(KotlinCompile::class.java) {
  kotlinOptions {
    freeCompilerArgs = listOf(
      "-Xno-param-assertions",
      "-Xno-call-assertions"
    )
  }
}

fun cmd(vararg command: String) = try {
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

/**
 * Builds the dependency notation for the named AppCompat [module] at the given [version].
 *
 * @param module simple name of the AppCompat module, for example "cardview-v7".
 * @param version optional desired version, null implies [appCompatVersion].
 */
fun appCompat(module: String, submodule: String? = null, version: String? = null)
  = if (submodule != null) {
  "com.android.support.$module:$submodule:${version ?: appCompatVersion}"
} else {
  "com.android.support:$module:${version ?: appCompatVersion}"
}

/**
 * Builds the dependency notation for the named AppArch [module] at the given [version].
 *
 * @param module simple name of the AppArch module, for example "persistence.room".
 * @param submodule simple name of the AppArch submodule, for example "runtime".
 * @param version optional desired version, null implies [appCompatVersion].
 */
fun appArch(module: String, submodule: String, version: String? = null)
  = "android.arch.$module:$submodule:${version ?: appArchVersion}"
