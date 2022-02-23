import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
  id("com.android.application") version "7.1.1" apply false
  id("com.android.library") version "7.1.1" apply false
  id("org.jetbrains.kotlin.android") version "1.6.10" apply false
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io")
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      freeCompilerArgs = listOf(
        "-Xinline-classes",
        "-Xopt-in=kotlin.ExperimentalUnsignedTypes"
      )
      jvmTarget = "1.8"
    }
  }
}
