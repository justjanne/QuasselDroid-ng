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

plugins {
  id("justjanne.android.signing")
  id("justjanne.android.app")
}

android {
  defaultConfig {
    vectorDrawables.useSupportLibrary = true
    testInstrumentationRunner = "de.kuschku.quasseldroid.util.TestRunner"
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
    kotlinCompilerExtensionVersion = libs.versions.androidx.compose.get()
  }
}

dependencies {
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.appcompat.resources)

  implementation(libs.androidx.activity)
  implementation(libs.androidx.activity.compose)

  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.compiler)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)

  implementation(libs.androidx.navigation.compose)

  implementation(libs.libquassel.client)

  implementation(libs.compose.htmltext)

  debugImplementation(libs.androidx.compose.ui.tooling)
  implementation(libs.androidx.compose.ui.preview)
  testImplementation(libs.androidx.compose.ui.test)
}
