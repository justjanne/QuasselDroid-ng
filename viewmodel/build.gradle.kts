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
  id("justjanne.android.library")
}

android {
  namespace = "de.kuschku.quasseldroid.viewmodel"
}

dependencies {
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.lifecycle.reactivestreams)

  // Utility
  implementation(libs.rxjava.android)
  implementation(libs.rxjava.java)
  implementation(libs.threetenbp) {
    artifact { classifier = "no-tzdb" }
  }

  implementation(libs.annotations.jetbrains)
  implementation(libs.annotations.inject)
  implementation(libs.kotlinx.serialization.json)

  // Quassel
  implementation(project(":persistence"))
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }

  testImplementation(libs.junit.api)
  testRuntimeOnly(libs.junit.engine)
}
