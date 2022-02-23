/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.visitors

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.ClassName
import de.justjanne.libquassel.generator.annotation.RpcFunctionAnnotation
import de.justjanne.libquassel.generator.annotation.RpcObjectAnnotation
import de.justjanne.libquassel.generator.rpcmodel.RpcModel
import de.justjanne.libquassel.generator.util.ksp.asTypeName

class KSDeclarationParser(
  private val resolver: Resolver,
  private val logger: KSPLogger
) : KSEmptyVisitor<Unit, RpcModel?>() {
  override fun visitClassDeclaration(
    classDeclaration: KSClassDeclaration,
    data: Unit
  ): RpcModel.ObjectModel? {
    val annotation = RpcObjectAnnotation.of(classDeclaration, resolver)
      ?: return null
    try {
      return RpcModel.ObjectModel(
        classDeclaration,
        ClassName(
          classDeclaration.packageName.asString(),
          classDeclaration.simpleName.asString()
        ),
        annotation.name,
        classDeclaration.getDeclaredFunctions()
          .mapNotNull { it.accept(this, Unit) }
          .mapNotNull { it as? RpcModel.FunctionModel }
          .toList()
      )
    } catch (t: Throwable) {
      logger.error("Error processing  ${annotation.name}", classDeclaration)
      logger.exception(t)
      throw t
    }
  }

  override fun visitFunctionDeclaration(
    function: KSFunctionDeclaration,
    data: Unit
  ): RpcModel.FunctionModel? {
    val annotation = RpcFunctionAnnotation.of(function, resolver)
      ?: return null
    try {
      return RpcModel.FunctionModel(
        function,
        function.simpleName.asString(),
        annotation.name,
        annotation.target,
        function.parameters
          .mapNotNull { it.accept(this, Unit) }
          .mapNotNull { it as? RpcModel.ParameterModel }
      )
    } catch (t: Throwable) {
      logger.error("Error processing  ${annotation.name ?: function.simpleName.asString()}", function)
      logger.exception(t)
      throw t
    }
  }

  override fun visitValueParameter(
    valueParameter: KSValueParameter,
    data: Unit
  ): RpcModel.ParameterModel {
    try {
      return RpcModel.ParameterModel(
        valueParameter,
        valueParameter.name?.asString(),
        valueParameter.type.asTypeName()
      )
    } catch (t: Throwable) {
      logger.error("Error processing  ${valueParameter.name?.asString()}", valueParameter)
      logger.exception(t)
      throw t
    }
  }

  override fun defaultHandler(node: KSNode, data: Unit): RpcModel? = null
}
