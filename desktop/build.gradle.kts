/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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
  application
  kotlin("jvm")
  kotlin("kapt")
}

application {
  mainClassName = "de.kuschku.cli.MainKt"
}

dependencies {
  implementation(kotlin("stdlib", "1.3.21"))

  implementation("io.reactivex.rxjava2", "rxjava", "2.1.9")
  implementation("info.picocli", "picocli", "3.9.0")

  implementation(project(":lib"))

  testImplementation("junit", "junit", "4.12")
}
