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

dependencies {
  implementation(libs.androidx.appcompat)

  implementation(libs.androidx.room.runtime)
  kapt(libs.androidx.room.compiler)
  implementation(libs.androidx.room.rxjava)
  testImplementation(libs.androidx.room.testing)

  implementation(libs.androidx.paging.runtime)

  // Utility
  implementation(libs.threetenbp) {
    artifact { classifier = "no-tzdb"}
  }
  implementation(libs.annotations.jetbrains)

  // Quassel
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }
}
