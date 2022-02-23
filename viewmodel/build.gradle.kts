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
  withVersion("2.2.0") {
    implementation("androidx.lifecycle", "lifecycle-extensions", version)
    implementation("androidx.lifecycle", "lifecycle-reactivestreams", version)
  }

  // Utility
  implementation("io.reactivex.rxjava2", "rxandroid", "2.1.1")
  implementation("io.reactivex.rxjava2", "rxjava", "2.2.12")
  implementation("org.threeten", "threetenbp", "1.4.0", classifier = "no-tzdb")
  implementation("org.jetbrains", "annotations", "17.0.0")

  implementation("javax.inject", "javax.inject", "1")

  // Quassel
  implementation(project(":persistence"))
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }

  testImplementation("junit", "junit", "4.12")
}

data class VersionContext<T>(val version: T)

inline fun <T> withVersion(version: T?, f: VersionContext<T>.() -> Unit) {
  version?.let {
    f.invoke(VersionContext(version))
  }
}
