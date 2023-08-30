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
  id("justjanne.android.app")
}

android {
  namespace = "de.kuschku.quasseldroid"

  defaultConfig {
    resourceConfigurations += setOf(
      "en", "en-rGB", "de", "fr", "fr-rCA", "it", "lt", "pt", "sr"
    )
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
    viewBinding = true
  }
}

dependencies {
  // App Compat
  implementation(libs.google.material)

  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.browser)
  implementation(libs.androidx.cardview)
  implementation(libs.androidx.recyclerview)
  implementation(libs.androidx.swiperefreshlayout)
  implementation(libs.androidx.preference)
  // Only needed for ringtone preference
  implementation(libs.androidx.legacy)
  implementation(libs.androidx.constraintlayout)

  implementation(libs.androidx.room.runtime)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.room.rxjava)
  testImplementation(libs.androidx.room.testing)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.lifecycle.reactivestreams)
  implementation(libs.androidx.lifecycle.service)
  implementation(libs.androidx.paging.runtime)
  implementation(libs.androidx.multidex)

  // Utility
  implementation(libs.rxjava.android)
  implementation(libs.rxjava.java)
  implementation(libs.threetenbp) {
    artifact { classifier = "no-tzdb" }
  }
  implementation(libs.annotations.jetbrains)
  implementation(libs.commons.codec)
  implementation(libs.reactivenetwork)
  implementation(libs.retrofit.core)
  implementation(libs.retrofit.converter.kotlinx)
  implementation(libs.kotlinx.serialization.json)

  // Quassel
  implementation(project(":viewmodel"))
  implementation(project(":persistence"))
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }

  // UI
  implementation(libs.speeddial)
  implementation(libs.materialprogressbar)
  implementation(libs.flexbox)
  implementation(project(":ui_spinner"))
  implementation(libs.materialdialogs.core)
  implementation(libs.materialdialogs.commons)
  implementation(libs.glide.core)
  implementation(libs.glide.recyclerview)
  ksp(libs.glide.compiler)

  // Quality Assurance
  implementation(project(":malheur"))
  debugImplementation(libs.leakcanary.android)

  // Dependency Injection
  implementation(libs.dagger.core)
  kapt(libs.dagger.compiler)
  kapt(libs.dagger.processor)
  implementation(libs.dagger.android.core)
  implementation(libs.dagger.android.support)

  testImplementation(libs.junit.api)
  testRuntimeOnly(libs.junit.engine)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.robolectric) {
    exclude(group = "org.threeten", module = "threetenbp")
    exclude(group = "com.google.auto.service", module = "auto-service")
  }

  androidTestImplementation(libs.junit.api)
  androidTestRuntimeOnly(libs.junit.engine)
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.test.espresso.contrib)
  androidTestImplementation(libs.androidx.test.junit)
  androidTestImplementation(libs.androidx.test.runner)
}
