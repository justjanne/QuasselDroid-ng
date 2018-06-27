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

plugins {
  id("com.android.library")
  kotlin("android")
}

android {
  compileSdkVersion(28)
  buildToolsVersion("28.0.0")

  defaultConfig {
    minSdkVersion(16)
    targetSdkVersion(28)

    consumerProguardFiles("proguard-rules.pro")

    // Disable test runner analytics
    testInstrumentationRunnerArguments = mapOf(
      "disableAnalytics" to "true"
    )
  }

  lintOptions {
    isWarningsAsErrors = true
    lintConfig = file("../lint.xml")
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.2.50"))

  // App Compat
  withVersion("27.1.1") {
    implementation("com.android.support", "appcompat-v7", version)
  }

  // App Arch Lifecycle
  withVersion("1.1.1") {
    implementation("android.arch.lifecycle", "extensions", version)
    implementation("android.arch.lifecycle", "reactivestreams", version)
  }

  // Utility
  implementation("io.reactivex.rxjava2", "rxandroid", "2.0.2")
  implementation("io.reactivex.rxjava2", "rxjava", "2.1.9")
  implementation("org.threeten", "threetenbp", "1.3.6", classifier = "no-tzdb")
  implementation("org.jetbrains", "annotations", "16.0.1")

  // Quassel
  implementation(project(":persistence"))
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }
}
