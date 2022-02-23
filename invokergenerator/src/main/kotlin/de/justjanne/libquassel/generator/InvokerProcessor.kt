/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.annotations.SyncedObject
import de.justjanne.libquassel.generator.rpcmodel.RpcModel
import de.justjanne.libquassel.generator.visitors.KSDeclarationParser
import de.justjanne.libquassel.generator.visitors.KotlinSaver
import de.justjanne.libquassel.generator.visitors.RpcModelProcessor
import de.justjanne.libquassel.generator.visitors.RpcObjectCollector

class InvokerProcessor(
  private val codeGenerator: CodeGenerator,
  private val logger: KSPLogger
) : SymbolProcessor {
  private var invoked = false

  override fun process(resolver: Resolver): List<KSAnnotated> {
    if (invoked) {
      return emptyList()
    }
    invoked = true

    val annotationModels = resolver.getSymbolsWithAnnotation(SyncedObject::class.java.canonicalName)
    val rpcModels = annotationModels.mapNotNull { it.accept(KSDeclarationParser(resolver, logger), Unit) }
    val registry = InvokerRegistryGenerator.generateRegistry(
      RpcObjectCollector().apply {
        rpcModels.forEach { it.accept(this, Unit) }
      }.objects
    )
    val invokerFiles = rpcModels.flatMap { model ->
      listOfNotNull(
        model.accept(RpcModelProcessor(), ProtocolSide.CLIENT),
        model.accept(RpcModelProcessor(), ProtocolSide.CORE),
      ) + registry
    }
    invokerFiles.forEach {
      it.accept(KotlinSaver(), codeGenerator)
    }

    return emptyList()
  }
}
