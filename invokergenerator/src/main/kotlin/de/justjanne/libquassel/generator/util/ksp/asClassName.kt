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
import com.squareup.kotlinpoet.ClassName

private fun KSDeclaration.parents(): List<KSDeclaration> {
  val declarations = mutableListOf(this)
  var parent = this.parentDeclaration
  while (parent != null) {
    declarations.add(parent)
    parent = parent.parentDeclaration
  }
  return declarations.reversed()
}

fun KSDeclaration.asClassName(): ClassName {
  return ClassName(
    packageName.asString(),
    parents().map { it.simpleName.asString() }
  )
}

fun KSType.asClassName(): ClassName = declaration.asClassName()
