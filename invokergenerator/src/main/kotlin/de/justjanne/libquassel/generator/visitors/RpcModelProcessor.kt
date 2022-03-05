/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.visitors

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.generator.Constants.TYPENAME_GENERATED
import de.justjanne.libquassel.generator.Constants.TYPENAME_INVOKER
import de.justjanne.libquassel.generator.Constants.TYPENAME_QVARIANTLIST
import de.justjanne.libquassel.generator.Constants.TYPENAME_QVARIANT_INTO
import de.justjanne.libquassel.generator.Constants.TYPENAME_QVARIANT_INTOORTHROW
import de.justjanne.libquassel.generator.Constants.TYPENAME_SYNCABLESTUB
import de.justjanne.libquassel.generator.Constants.TYPENAME_UNKNOWN_METHOD_EXCEPTION
import de.justjanne.libquassel.generator.Constants.TYPENAME_WRONG_OBJECT_TYPE_EXCEPTION
import de.justjanne.libquassel.generator.kotlinmodel.KotlinModel
import de.justjanne.libquassel.generator.rpcmodel.RpcModel
import de.justjanne.libquassel.generator.rpcmodel.RpcModelVisitor
import de.justjanne.libquassel.generator.util.kotlinpoet.ArgString
import de.justjanne.libquassel.generator.util.kotlinpoet.buildWhen
import de.justjanne.libquassel.generator.util.kotlinpoet.withIndent
import transformName

class RpcModelProcessor : RpcModelVisitor<ProtocolSide, KotlinModel?> {
  override fun visitObjectModel(model: RpcModel.ObjectModel, data: ProtocolSide): KotlinModel {
    val name = ClassName(
      TYPENAME_INVOKER.packageName,
      "${model.rpcName}${transformName(data.name)}Invoker"
    )
    return KotlinModel.FileModel(
      listOf(model.source),
      FileSpec.builder(name.packageName, name.simpleName)
        .addImport(
          TYPENAME_QVARIANT_INTOORTHROW.packageName,
          TYPENAME_QVARIANT_INTOORTHROW.simpleName
        )
        .addImport(
          TYPENAME_QVARIANT_INTO.packageName,
          TYPENAME_QVARIANT_INTO.simpleName
        )
        .addAnnotation(TYPENAME_GENERATED)
        .addType(
          TypeSpec.objectBuilder(name.simpleName)
            .addSuperinterface(TYPENAME_INVOKER)
            .addAnnotation(TYPENAME_GENERATED)
            .addProperty(
              PropertySpec.builder(
                "className",
                String::class.asTypeName(),
                KModifier.OVERRIDE
              )
                .initializer("\"${model.rpcName}\"")
                .addAnnotation(TYPENAME_GENERATED)
                .build()
            )
            .addFunction(
              FunSpec.builder("invoke")
                .addModifiers(KModifier.OVERRIDE, KModifier.OPERATOR)
                .addAnnotation(TYPENAME_GENERATED)
                .addParameter(
                  ParameterSpec.builder(
                    "on",
                    TYPENAME_SYNCABLESTUB
                  ).build()
                ).addParameter(
                  ParameterSpec.builder(
                    "method",
                    String::class.asTypeName()
                  ).build()
                ).addParameter(
                  ParameterSpec.builder(
                    "params",
                    TYPENAME_QVARIANTLIST
                  ).build()
                )
                .addCode(
                  buildCodeBlock {
                    beginControlFlow("if (on is %T)", model.name)
                    buildWhen("method") {
                      for (method in model.methods) {
                        val block = method.accept(this@RpcModelProcessor, data)
                          as? KotlinModel.FunctionModel
                          ?: continue
                        addCase(ArgString("%S", method.rpcName ?: method.name), block.data)
                      }
                      buildElse {
                        addStatement(
                          "throw %T(className, method)",
                          TYPENAME_UNKNOWN_METHOD_EXCEPTION
                        )
                      }
                    }
                    nextControlFlow("else")
                    addStatement("throw %T(on, className)", TYPENAME_WRONG_OBJECT_TYPE_EXCEPTION)
                    endControlFlow()
                  }
                )
                .build()
            )
            .build()
        ).build()
    )
  }

  override fun visitFunctionModel(model: RpcModel.FunctionModel, data: ProtocolSide) =
    if (model.side != data) null
    else KotlinModel.FunctionModel(
      model.source,
      buildCodeBlock {
        if (model.parameters.isEmpty()) {
          addStatement("on.${model.name}()")
        } else {
          addStatement("on.${model.name}(")
          withIndent {
            val lastIndex = model.parameters.size - 1
            for ((i, parameter) in model.parameters.withIndex()) {
              val suffix = if (i != lastIndex) "," else ""
              addStatement(
                if (parameter.type.isNullable)
                  "${parameter.name} = params[$i].value<%T>()$suffix"
                else
                  "${parameter.name} = params[$i].valueOrThrow<%T>()$suffix",
                parameter.type
              )
            }
          }
          addStatement(")")
        }
      }
    )

  override fun visitParameterModel(
    model: RpcModel.ParameterModel,
    data: ProtocolSide
  ): KotlinModel? = null
}
