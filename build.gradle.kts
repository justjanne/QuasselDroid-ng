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

buildscript {
  repositories {
    google()
    mavenCentral()
    jcenter()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.0.0-alpha05")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21-2")
  }
}

allprojects {
  extra["androidxAppcompatVersion"] = "1.2.0"
  extra["androidxCoreVersion"] = "1.2.0"
  extra["androidxComposeVersion"] = "1.0.0-alpha11"
  extra["androidxLifecycleVersion"] = "2.3.0-rc01"
  extra["mdcVersion"] = "1.2.1"

  repositories {
    google()
    mavenCentral()
    jcenter()
  }
}
