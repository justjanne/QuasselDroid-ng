/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.util.ksp

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName

fun KSDeclaration.asTypeName(): TypeName =
  ClassName(packageName.asString(), simpleName.asString())

fun KSTypeReference.asTypeName(): TypeName = resolve().asTypeName()

fun KSType.asTypeName(): TypeName {
  when (val decl = declaration) {
    is KSTypeAlias -> return decl.type.resolve().asTypeName()
  }

  val baseType = asClassName()
  if (arguments.isEmpty()) {
    return baseType
  }

  val parameters = arguments.map {
    val type = it.type?.resolve()
    when (it.variance) {
      Variance.STAR ->
        WildcardTypeName.producerOf(Any::class)
          .copy(nullable = true)
      Variance.INVARIANT ->
        type!!.asTypeName()
          .copy(nullable = type.isMarkedNullable)
      Variance.COVARIANT ->
        WildcardTypeName.producerOf(type!!.asTypeName())
          .copy(nullable = type.isMarkedNullable)
      Variance.CONTRAVARIANT ->
        WildcardTypeName.consumerOf(type!!.asTypeName())
          .copy(nullable = type.isMarkedNullable)
    }
  }

  return baseType.parameterizedBy(parameters)
}
