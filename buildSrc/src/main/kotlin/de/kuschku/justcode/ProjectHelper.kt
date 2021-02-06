/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.justcode

import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.util.*

fun Project.cmd(vararg command: String) = try {
  val stdOut = ByteArrayOutputStream()
  exec {
    commandLine(*command)
    standardOutput = stdOut
    errorOutput = NullOutputStream()
  }
  stdOut.toString(Charsets.UTF_8.name()).trim()
} catch (e: Throwable) {
  null
}

fun Project.properties(fileName: String): Properties? {
  val file = file(fileName)
  if (!file.exists())
    return null
  val props = Properties()
  props.load(file.inputStream())
  return props
}

inline fun <reified T> setBuildConfigField(
  setter: (String, String, String) -> Unit,
  name: String,
  value: T
) {
  if (T::class == Long::class) {
    setter("long", name, "${value ?: 0}L")
  } else if (T::class == String::class) {
    setter("String", name, "\"${value}\"")
  }
}
