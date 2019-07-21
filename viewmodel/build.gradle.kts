/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Mareike Koschinski
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
    setLintConfig(file("../lint.xml"))
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.3.41"))

  implementation("androidx.appcompat", "appcompat", "1.0.2")
  withVersion("2.0.0") {
    implementation("androidx.lifecycle", "lifecycle-extensions", version)
    implementation("androidx.lifecycle", "lifecycle-reactivestreams", version)
  }

  // Utility
  implementation("io.reactivex.rxjava2", "rxandroid", "2.1.1")
  implementation("io.reactivex.rxjava2", "rxjava", "2.2.10")
  implementation("org.threeten", "threetenbp", "1.4.0", classifier = "no-tzdb")
  implementation("org.jetbrains", "annotations", "17.0.0")

  implementation("javax.inject", "javax.inject", "1")

  // Quassel
  implementation(project(":persistence"))
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }
}
