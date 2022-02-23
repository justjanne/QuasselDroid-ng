/*
 * Copyright (C) 2021 Zac Sweers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.justjanne.libquassel.generator.util

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import de.justjanne.libquassel.generator.util.ksp.asTypeName

internal inline fun <reified T> KSAnnotation.getMember(name: String): T? {
  val matchingArg = arguments.find { it.name?.asString() == name }
    ?: error(
      "No member name found for '$name'. All arguments: ${arguments.map { it.name?.asString() }}"
    )
  return when (val argValue = matchingArg.value) {
    is T -> argValue
    is KSType ->
      when {
        T::class.java != ClassName::class.java -> null
        else -> argValue.asTypeName() as T
      }
    else -> null
  }
}
