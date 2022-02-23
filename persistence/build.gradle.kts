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
  implementation(kotlin("stdlib", "1.6.10"))

  implementation("androidx.appcompat", "appcompat", "1.1.0")

  withVersion("2.2.5") {
    implementation("androidx.room", "room-runtime", version)
    kapt("androidx.room", "room-compiler", version)
    implementation("androidx.room", "room-rxjava2", version)
    testImplementation("androidx.room", "room-testing", version)
  }

  implementation("androidx.paging", "paging-runtime", "2.1.2")

  // Utility
  implementation("org.threeten", "threetenbp", "1.4.0", classifier = "no-tzdb")
  implementation("org.jetbrains", "annotations", "17.0.0")

  // Quassel
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }
}

data class VersionContext<T>(val version: T)

inline fun <T> withVersion(version: T?, f: VersionContext<T>.() -> Unit) {
  version?.let {
    f.invoke(VersionContext(version))
  }
}
