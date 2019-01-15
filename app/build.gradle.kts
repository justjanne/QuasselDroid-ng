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
  compileSdkVersion(28)

  signingConfigs {
    SigningData.of(project.rootProject.properties("signing.properties"))?.let {
      create("default") {
        storeFile = file(it.storeFile)
        storePassword = it.storePassword
        keyAlias = it.keyAlias
        keyPassword = it.keyPassword
      }
    }
  }

  defaultConfig {
    minSdkVersion(16)
    targetSdkVersion(28)

    applicationId = "com.iskrembilen.quasseldroid"
    versionCode = cmd("git", "rev-list", "--count", "HEAD")?.toIntOrNull() ?: 1
    versionName = cmd("git", "describe", "--always", "--tags", "HEAD") ?: "1.0.0"

    buildConfigField("String", "GIT_HEAD", "\"${cmd("git", "rev-parse", "HEAD") ?: ""}\"")
    buildConfigField("String", "FANCY_VERSION_NAME", "\"${fancyVersionName() ?: ""}\"")
    buildConfigField("long", "GIT_COMMIT_DATE", "${cmd("git", "show", "-s", "--format=%ct") ?: 0}L")

    signingConfig = signingConfigs.findByName("default")

    resConfigs("en", "de", "fr-rCA", "lt", "pt", "sr")

    vectorDrawables.useSupportLibrary = true

    setProperty("archivesBaseName", "Quasseldroid-$versionName")

    multiDexEnabled = false

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
      isUseProguard = false

      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "proguard-rules.pro"
      )
    }

    getByName("debug") {
      applicationIdSuffix = ".debug"

      isZipAlignEnabled = true
      isMinifyEnabled = true
      isShrinkResources = true
      isUseProguard = false

      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "proguard-rules.pro"
      )
    }
  }

  compileOptions {
    setSourceCompatibility(JavaVersion.VERSION_1_8)
    setTargetCompatibility(JavaVersion.VERSION_1_8)
  }

  lintOptions {
    isWarningsAsErrors = true
    lintConfig = file("../lint.xml")
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.3.11"))

  // App Compat
  implementation("com.google.android.material", "material", "1.0.0-rc01")

  implementation("androidx.appcompat", "appcompat", "1.0.0")
  implementation("androidx.browser", "browser", "1.0.0")
  implementation("androidx.cardview", "cardview", "1.0.0")
  implementation("androidx.recyclerview", "recyclerview", "1.0.0")
  implementation("androidx.preference", "preference", "1.0.0")
  implementation("androidx.legacy", "legacy-preference-v14", "1.0.0")
  implementation("androidx.constraintlayout", "constraintlayout", "1.1.2")

  implementation("androidx.lifecycle", "lifecycle-extensions", "2.0.0-rc01")
  implementation("androidx.lifecycle", "lifecycle-reactivestreams", "2.0.0-rc01")
  testImplementation("androidx.arch.core", "core-testing", "2.0.0-rc01")
  implementation(project(":lifecycle-ktx"))

  implementation("androidx.room", "room-runtime", "2.0.0-rc01")
  kapt("androidx.room", "room-compiler", "2.0.0-rc01")
  implementation("androidx.room", "room-rxjava2", "2.0.0-rc01")
  testImplementation("androidx.room", "room-testing", "2.0.0-rc01")

  implementation("androidx.paging", "paging-runtime", "2.0.0-rc01")

  // Utility
  implementation("io.reactivex.rxjava2", "rxandroid", "2.0.2")
  implementation("io.reactivex.rxjava2", "rxjava", "2.1.9")
  implementation("org.threeten", "threetenbp", "1.3.8", classifier = "no-tzdb")
  implementation("org.jetbrains", "annotations", "16.0.3")
  implementation("com.google.code.gson", "gson", "2.8.5")
  implementation("commons-codec", "commons-codec", "1.11")
  implementation("com.squareup.retrofit2", "retrofit", "2.5.0")
  implementation("com.squareup.retrofit2", "converter-gson", "2.5.0")
  withVersion("10.0.0") {
    implementation("com.jakewharton", "butterknife", version)
    kapt("com.jakewharton", "butterknife-compiler", version)
  }

  // Quassel
  implementation(project(":viewmodel"))
  implementation(project(":persistence"))
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }

  // UI
  implementation("me.zhanghai.android.materialprogressbar", "library", "1.6.0")
  withVersion("0.9.6.0") {
    implementation("com.afollestad.material-dialogs", "core", version)
    implementation("com.afollestad.material-dialogs", "commons", version)
  }
  withVersion("4.8.0") {
    implementation("com.github.bumptech.glide", "glide", version)
    implementation("com.github.bumptech.glide", "recyclerview-integration", version)
    kapt("com.github.bumptech.glide", "compiler", version)
  }

  // Quality Assurance
  implementation(project(":malheur"))
  withVersion("1.6.2") {
    debugImplementation("com.squareup.leakcanary", "leakcanary-android", version)
    releaseImplementation("com.squareup.leakcanary", "leakcanary-android-no-op", version)
    testImplementation("com.squareup.leakcanary", "leakcanary-android-no-op", version)
    androidTestImplementation("com.squareup.leakcanary", "leakcanary-android-no-op", version)
  }

  // Dependency Injection
  withVersion("2.20") {
    implementation("com.google.dagger", "dagger", version)
    kapt("com.google.dagger", "dagger-compiler", version)
    kapt("com.google.dagger", "dagger-android-processor", version)
    implementation("com.google.dagger", "dagger-android", version)
    implementation("com.google.dagger", "dagger-android-support", version)
  }

  testImplementation("junit", "junit", "4.12")
}
