/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.generator.kotlinmodel.KotlinModel
import de.justjanne.libquassel.generator.rpcmodel.RpcModel
import de.justjanne.libquassel.generator.util.kotlinpoet.withIndent

object InvokerRegistryGenerator {
  private fun generateCodeBlock(
    objects: List<RpcModel.ObjectModel>,
    side: ProtocolSide
  ) = buildCodeBlock {
    add("mapOf(\n")
    withIndent {
      for (syncable in objects) {
        addStatement("%S to %T,", syncable.rpcName, Constants.invokerName(syncable, side))
      }
    }
    if (objects.isEmpty()) {
      add("\n")
    }
    add(")")
  }

  fun generateRegistry(objects: List<RpcModel.ObjectModel>): KotlinModel.FileModel {
    val name = ClassName(
      Constants.TYPENAME_INVOKER.packageName,
      "GeneratedInvokerRegistry"
    )
    return KotlinModel.FileModel(
      objects.map(RpcModel.ObjectModel::source),
      FileSpec.builder(name.packageName, name.simpleName)
        .addType(
          TypeSpec.objectBuilder("GeneratedInvokerRegistry")
            .addSuperinterface(Constants.TYPENAME_INVOKERREGISTRY)
            .addProperty(
              PropertySpec.builder("clientInvokers", Constants.TYPENAME_INVOKERMAP)
                .addModifiers(KModifier.OVERRIDE)
                .initializer(generateCodeBlock(objects, ProtocolSide.CLIENT))
                .build()
            )
            .addProperty(
              PropertySpec.builder("coreInvokers", Constants.TYPENAME_INVOKERMAP)
                .addModifiers(KModifier.OVERRIDE)
                .initializer(generateCodeBlock(objects, ProtocolSide.CORE))
                .build()
            )
            .build()
        ).build()
    )
  }
}
