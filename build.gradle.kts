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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

buildscript {
  repositories {
    google()
    mavenCentral()
    jcenter()
  }
  dependencies {
    classpath("com.android.tools.build", "gradle", "7.0.0-alpha05")
    classpath("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.4.30")
    classpath("com.google.dagger", "hilt-android-gradle-plugin", "2.31.2-alpha")
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    jcenter()
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
