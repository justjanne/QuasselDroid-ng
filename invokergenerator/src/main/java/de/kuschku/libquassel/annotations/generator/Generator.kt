/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.libquassel.annotations.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import de.kuschku.libquassel.annotations.Context
import de.kuschku.libquassel.annotations.data.ParsedClass
import java.io.File

class Generator(
  private val context: Context
) {
  fun generate(parsedClass: ParsedClass) {
    val file = FileSpec.builder(
      parsedClass.name.packageName + ".invokers",
      parsedClass.quasselName + "Invoker"
    ).addType(
      TypeSpec.objectBuilder(parsedClass.quasselName + "Invoker")
        .addSuperinterface(TYPENAME_INVOKER.parameterizedBy(parsedClass.name))
        .addProperty(
          PropertySpec.builder(
            "className",
            String::class.asTypeName(),
            KModifier.OVERRIDE
          ).initializer("\"${parsedClass.quasselName}\"").build()
        )
        .addFunction(
          FunSpec.builder("invoke")
            .addModifiers(KModifier.OVERRIDE, KModifier.OPERATOR)
            .addParameter(
              ParameterSpec.builder(
                "on",
                ANY.copy(nullable = true)
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
                beginControlFlow("if (on is %T)", parsedClass.name)
                beginControlFlow("when (method)")
                for (method in parsedClass.methods) {
                  beginControlFlow("%S ->", method.quasselName)
                  addStatement("on.${method.name}(")
                  indent()
                  val lastIndex = method.parameters.size - 1
                  for ((i, parameter) in method.parameters.withIndex()) {
                    if (i == lastIndex) {
                      addStatement("${parameter.name} = params[$i].data as %T", parameter.type)
                    } else {
                      addStatement("${parameter.name} = params[$i].data as %T,", parameter.type)
                    }
                  }
                  unindent()
                  addStatement(")")
                  endControlFlow()
                }
                endControlFlow()
                nextControlFlow("else")
                addStatement("throw %T(on, className)", TYPENAME_WRONG_OBJECT_TYPE_EXCEPTION)
                endControlFlow()
              }
            )
            .build()
        )
        .build()
    ).build()

    file.writeTo(File(context.targetPath))
  }

  companion object {
    private val TYPENAME_INVOKER = ClassName(
      "de.kuschku.libquassel.quassel.syncables.interfaces.invokers",
      "Invoker"
    )
    private val TYPENAME_QVARIANTLIST = ClassName(
      "de.kuschku.libquassel.protocol",
      "QVariantList"
    )
    private val TYPENAME_WRONG_OBJECT_TYPE_EXCEPTION = ClassName(
      "de.kuschku.libquassel.quassel.exceptions",
      "WrongObjectTypeException"
    )
  }
}
