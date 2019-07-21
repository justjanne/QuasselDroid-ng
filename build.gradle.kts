import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Mareike Koschinski
 * Copyright (c) 2018 The Quassel Project
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

buildscript {
  repositories {
    google()
    jcenter()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:3.4.1")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
  }
}

allprojects {
  repositories {
    google()
    jcenter()
    maven(url = "https://jitpack.io")
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      freeCompilerArgs += listOf(
        "-XXLanguage:+InlineClasses",
        "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes"
      )
      jvmTarget = "1.6"
    }
  }
}
