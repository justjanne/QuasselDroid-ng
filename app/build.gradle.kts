@file:Suppress("UnstableApiUsage")

/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

import util.buildConfigField
import util.cmd

plugins {
  id("justjanne.android.app")
}

fun Project.fancyVersionName(): String? {
  val name = cmd("git", "describe", "--always", "--tags", "HEAD") ?: return null
  val commit = cmd("git", "rev-parse", "HEAD") ?: return name

  return """<a href="https://git.kuschku.de/justJanne/QuasselDroid-ng/commit/$commit">$name</a>"""
}

android {
  defaultConfig {
    buildConfigField("FANCY_VERSION_NAME", fancyVersionName())

    vectorDrawables.useSupportLibrary = true
    testInstrumentationRunner = "de.justjanne.quasseldroid.util.TestRunner"
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true

      multiDexEnabled = false

      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "proguard-rules.pro"
      )
    }

    getByName("debug") {
      applicationIdSuffix = ".debug"

      multiDexEnabled = true
    }
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
  }
}

dependencies {
  implementation(libs.kotlin.stdlib)

  implementation(libs.kotlinx.coroutines.android)
  testImplementation(libs.kotlinx.coroutines.test)

  testImplementation(libs.kotlin.test)
  testImplementation(libs.junit.api)
  testImplementation(libs.junit.params)
  testRuntimeOnly(libs.junit.engine)

  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.appcompat.resources)

  implementation(libs.androidx.activity)
  implementation(libs.androidx.activity.compose)

  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.compiler)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.material.icons)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui.tooling)

  implementation(libs.androidx.collection.ktx)
  implementation(libs.androidx.core.ktx)

  implementation(libs.androidx.navigation.compose)

  implementation(libs.androidx.paging.runtime)
  testImplementation(libs.androidx.paging.test)
  implementation(libs.androidx.paging.compose)

  implementation(libs.androidx.room.runtime)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.paging)

  implementation(libs.libquassel.client)
  implementation(libs.libquassel.irc)

  //implementation(libs.compose.htmltext)

  debugImplementation(libs.androidx.compose.ui.tooling)
  implementation(libs.androidx.compose.ui.preview)
  testImplementation(libs.androidx.compose.ui.test)
}
