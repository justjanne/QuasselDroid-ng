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
  compileSdkVersion(27)
  buildToolsVersion("27.0.3")

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
    targetSdkVersion(27)

    applicationId = "de.kuschku.quasseldroid_ng"
    versionCode = cmd("git", "rev-list", "--count", "HEAD")?.toIntOrNull() ?: 1
    versionName = cmd("git", "describe", "--always", "--tags", "HEAD") ?: "1.0.0"

    buildConfigField("String", "GIT_HEAD", "\"${cmd("git", "rev-parse", "HEAD") ?: ""}\"")
    buildConfigField("long", "GIT_COMMIT_DATE", "${cmd("git", "show", "-s", "--format=%ct") ?: 0}L")

    signingConfig = signingConfigs.getByName("default")

    resConfigs("auto")

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

dependencies {
  implementation(kotlin("stdlib", "1.2.30"))

  // App Compat
  withVersion("27.1.0") {
    implementation("com.android.support", "appcompat-v7", version)
    implementation("com.android.support", "design", version)
    implementation("com.android.support", "customtabs", version)
    implementation("com.android.support", "cardview-v7", version)
    implementation("com.android.support", "recyclerview-v7", version)
    implementation("com.android.support", "preference-v7", version)
    implementation("com.android.support", "preference-v14", version)
  }
  implementation("com.android.support.constraint", "constraint-layout", "1.0.2")

  // App Arch Lifecycle
  withVersion("1.1.0") {
    implementation("android.arch.lifecycle", "extensions", version)
    implementation("android.arch.lifecycle", "reactivestreams", version)
    kapt("android.arch.lifecycle", "compiler", version)
  }

  // App Arch Persistence
  withVersion("1.1.0-beta1") {
    implementation("android.arch.persistence.room", "runtime", version)
    implementation("android.arch.persistence.room", "rxjava2", version)
    kapt("android.arch.persistence.room", "compiler", version)
    testImplementation("android.arch.persistence.room", "testing", version)
  }

  // App Arch Paging
  implementation("android.arch.paging", "runtime", "1.0.0-alpha7") {
    exclude(group = "junit", module = "junit")
  }

  // Utility
  implementation("io.reactivex.rxjava2", "rxjava", "2.1.9")
  implementation("org.threeten", "threetenbp", "1.3.6", classifier = "no-tzdb")
  implementation("org.jetbrains", "annotations", "15.0")
  withVersion("8.8.1") {
    implementation("com.jakewharton", "butterknife", version)
    kapt("com.jakewharton", "butterknife-compiler", version)
  }

  // Quassel
  implementation(project(":viewmodel"))
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }

  // UI
  implementation("me.zhanghai.android.materialprogressbar", "library", "1.4.2")
  implementation("com.afollestad.material-dialogs", "core", "0.9.6.0")
  implementation(project(":slidingpanel"))

  // Quality Assurance
  implementation(project(":malheur"))
  withVersion("1.5.4") {
    debugImplementation("com.squareup.leakcanary", "leakcanary-android", version)
    releaseImplementation("com.squareup.leakcanary", "leakcanary-android-no-op", version)
    testImplementation("com.squareup.leakcanary", "leakcanary-android-no-op", version)
    androidTestImplementation("com.squareup.leakcanary", "leakcanary-android-no-op", version)
  }

  testImplementation("junit", "junit", "4.12")
  withVersion("1.0.1") {
    androidTestImplementation("com.android.support.test", "runner", version)
    androidTestImplementation("com.android.support.test", "rules", version)
  }
  androidTestImplementation("com.android.support.test.espresso", "espresso-core", "3.0.1")
}