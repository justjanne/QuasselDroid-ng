/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.util.ksp

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.asClassName

internal inline fun <reified T : Enum<T>> KSType.toEnum(): T? {
  return asClassName().toEnum(T::class.java)
}

internal inline fun <reified T : Enum<T>> ClassName.toEnum(): T? {
  return toEnum(T::class.java)
}

internal fun <T : Enum<T>> ClassName.toEnum(clazz: Class<T>): T? {
  val enumClassName = clazz.asClassName()
  return clazz.enumConstants.find {
    this.canonicalName == enumClassName.nestedClass(it.name).canonicalName
  }
}
