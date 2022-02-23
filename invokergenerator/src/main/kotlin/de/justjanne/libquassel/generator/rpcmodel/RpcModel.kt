/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.rpcmodel

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import de.justjanne.libquassel.annotations.ProtocolSide

sealed class RpcModel {
  data class ObjectModel(
    val source: KSClassDeclaration,
    val name: ClassName,
    val rpcName: String?,
    val methods: List<FunctionModel>
  ) : RpcModel() {
    override fun <D, R> accept(visitor: RpcModelVisitor<D, R>, data: D) =
      visitor.visitObjectModel(this, data)
  }

  data class FunctionModel(
    val source: KSFunctionDeclaration,
    val name: String,
    val rpcName: String?,
    val side: ProtocolSide?,
    val parameters: List<ParameterModel>
  ) : RpcModel() {
    override fun <D, R> accept(visitor: RpcModelVisitor<D, R>, data: D) =
      visitor.visitFunctionModel(this, data)
  }

  data class ParameterModel(
    val source: KSValueParameter,
    val name: String?,
    val type: TypeName
  ) : RpcModel() {
    override fun <D, R> accept(visitor: RpcModelVisitor<D, R>, data: D) =
      visitor.visitParameterModel(this, data)
  }

  abstract fun <D, R> accept(visitor: RpcModelVisitor<D, R>, data: D): R
}
