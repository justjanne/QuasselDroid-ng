/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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
  compileSdkVersion(30)

  defaultConfig {
    minSdkVersion(20)
    targetSdkVersion(30)

    consumerProguardFiles("proguard-rules.pro")

    // Disable test runner analytics
    testInstrumentationRunnerArguments["disableAnalytics"] = "true"
  }

  lintOptions {
    isWarningsAsErrors = true
    lintConfig = file("../lint.xml")
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.5.10"))

  implementation("com.google.code.gson", "gson", "2.8.5")
  implementation("androidx.annotation", "annotation", "1.1.0")
}
