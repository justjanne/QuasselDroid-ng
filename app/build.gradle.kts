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
  buildToolsVersion("28.0.0")

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

  lintOptions {
    isWarningsAsErrors = true
    lintConfig = file("../lint.xml")
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.2.51"))

  // App Compat
  withVersion("27.1.1") {
    implementation("com.android.support", "appcompat-v7", version)
    implementation("com.android.support", "design", version)
    implementation("com.android.support", "customtabs", version)
    implementation("com.android.support", "cardview-v7", version)
    implementation("com.android.support", "recyclerview-v7", version)
    implementation("com.android.support", "preference-v7", version)
    implementation("com.android.support", "preference-v14", version)
  }
  implementation("com.android.support.constraint", "constraint-layout", "1.1.0-beta6")

  // App Arch Lifecycle
  withVersion("1.1.1") {
    implementation("android.arch.lifecycle", "extensions", version)
    implementation("android.arch.lifecycle", "reactivestreams", version)
    testImplementation("android.arch.core", "core-testing", version)
    implementation(project(":lifecycle-ktx"))
  }

  // App Arch Persistence
  withVersion("1.1.1-rc1") {
    implementation("android.arch.persistence.room", "runtime", version)
    kapt("android.arch.persistence.room", "compiler", version)
    implementation("android.arch.persistence.room", "rxjava2", version)
    testImplementation("android.arch.persistence.room", "testing", version)
  }

  // App Arch Paging
  implementation("android.arch.paging", "runtime", "1.0.0")

  // Utility
  implementation("io.reactivex.rxjava2", "rxandroid", "2.0.2")
  implementation("io.reactivex.rxjava2", "rxjava", "2.1.9")
  implementation("org.threeten", "threetenbp", "1.3.6", classifier = "no-tzdb")
  implementation("org.jetbrains", "annotations", "16.0.1")
  implementation("com.google.code.gson", "gson", "2.8.2")
  implementation("commons-codec", "commons-codec", "1.11")
  implementation("com.squareup.retrofit2", "retrofit", "2.4.0")
  implementation("com.squareup.retrofit2", "converter-gson", "2.4.0")
  withVersion("8.8.1") {
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
  implementation("me.zhanghai.android.materialprogressbar", "library", "1.4.2")
  withVersion("0.9.6.0") {
    implementation("com.afollestad.material-dialogs", "core", version)
    implementation("com.afollestad.material-dialogs", "commons", version)
  }
  withVersion("4.6.1") {
    implementation("com.github.bumptech.glide", "glide", version)
    implementation("com.github.bumptech.glide", "recyclerview-integration", version)
    kapt("com.github.bumptech.glide", "compiler", version)
  }

  // Quality Assurance
  implementation(project(":malheur"))
  withVersion("1.5.4") {
    debugImplementation("com.squareup.leakcanary", "leakcanary-android", version)
    releaseImplementation("com.squareup.leakcanary", "leakcanary-android-no-op", version)
    testImplementation("com.squareup.leakcanary", "leakcanary-android-no-op", version)
    androidTestImplementation("com.squareup.leakcanary", "leakcanary-android-no-op", version)
  }

  // Dependency Injection
  withVersion("2.15") {
    implementation("com.google.dagger", "dagger", version)
    kapt("com.google.dagger", "dagger-compiler", version)
    kapt("com.google.dagger", "dagger-android-processor", version)
    implementation("com.google.dagger", "dagger-android", version)
    implementation("com.google.dagger", "dagger-android-support", version)
  }

  testImplementation("junit", "junit", "4.12")
  withVersion("1.0.1") {
    androidTestImplementation("com.android.support.test", "runner", version)
    androidTestImplementation("com.android.support.test", "rules", version)
  }
  androidTestImplementation("com.android.support.test.espresso", "espresso-core", "3.0.1")
}
