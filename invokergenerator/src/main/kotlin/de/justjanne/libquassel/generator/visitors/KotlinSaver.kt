/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.visitors

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import de.justjanne.libquassel.generator.kotlinmodel.KotlinModel
import de.justjanne.libquassel.generator.kotlinmodel.KotlinModelVisitor
import java.io.IOException

class KotlinSaver : KotlinModelVisitor<CodeGenerator, Unit> {
  private fun generateDependencies(sources: List<KSClassDeclaration>): Dependencies {
    val sourceFiles = sources.mapNotNull(KSClassDeclaration::containingFile)
    return Dependencies(true, *sourceFiles.toTypedArray())
  }

  override fun visitFileModel(model: KotlinModel.FileModel, data: CodeGenerator) {
    require(model.source.isNotEmpty()) {
      "Source may not be empty. Sources was empty for $model"
    }

    try {
      val writer = data.createNewFile(
        generateDependencies(model.source),
        model.data.packageName,
        model.data.name
      ).bufferedWriter(Charsets.UTF_8)
      model.data.writeTo(writer)
      writer.close()
    } catch (_: IOException) {
      // Ignored
    }
  }

  override fun visitFunctionModel(
    model: KotlinModel.FunctionModel,
    data: CodeGenerator
  ) = Unit
}
