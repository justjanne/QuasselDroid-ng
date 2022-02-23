/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.kotlinmodel

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec

sealed class KotlinModel {
  data class FileModel(
    val source: List<KSClassDeclaration>,
    val data: FileSpec
  ) : KotlinModel() {
    override fun <D, R> accept(visitor: KotlinModelVisitor<D, R>, data: D) =
      visitor.visitFileModel(this, data)
  }

  data class FunctionModel(
    val source: KSFunctionDeclaration,
    val data: CodeBlock
  ) : KotlinModel() {
    override fun <D, R> accept(visitor: KotlinModelVisitor<D, R>, data: D) =
      visitor.visitFunctionModel(this, data)
  }

  abstract fun <D, R> accept(visitor: KotlinModelVisitor<D, R>, data: D): R
}
