import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

dependencies {
  implementation(kotlin("stdlib", "1.3.61"))
  implementation(project(":invokerannotations"))
  implementation("org.jetbrains.kotlin", "kotlin-compiler-embeddable", "1.3.61")
  implementation("com.squareup", "kotlinpoet", "1.3.0")
  compileOnly("com.google.auto.service:auto-service:1.0-rc6")
  kapt("com.google.auto.service:auto-service:1.0-rc6")
}
