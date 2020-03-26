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
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  implementation(kotlin("stdlib", "1.3.61"))

  implementation("androidx.annotation", "annotation", "1.1.0")

  implementation("org.threeten", "threetenbp", "1.4.0")
  implementation("io.reactivex.rxjava2", "rxjava", "2.2.12")

  implementation(project(":invokerannotations"))
  kapt(project(":invokergenerator"))

  testImplementation("junit", "junit", "4.12")
}
