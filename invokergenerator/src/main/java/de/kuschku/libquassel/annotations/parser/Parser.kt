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

package de.kuschku.libquassel.annotations.parser

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import de.kuschku.libquassel.annotations.Context
import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.annotations.data.ParsedClass
import de.kuschku.libquassel.annotations.data.ParsedMethod
import de.kuschku.libquassel.annotations.data.ParsedParameter
import de.kuschku.libquassel.annotations.splitQualifiedName
import org.jetbrains.kotlin.builtins.jvm.JavaToKotlinClassMap
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import java.io.File
import java.net.JarURLConnection
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class Parser(
  private val context: Context,
  private val environment: KotlinCoreEnvironment
) {
  fun parse(element: Element): ParsedClass? {
    val typeElement = element as TypeElement

    val sourcePath = typeElement.qualifiedName.toString().replace('.', '/') + ".kt"
    val sourceFile = File(context.sourcePath, sourcePath)

    val source = sourceFile.readText(Charsets.UTF_8)

    val file = PsiFileFactory.getInstance(environment.project)
      .createFileFromText(KotlinLanguage.INSTANCE, source)

    val importDirectives = file.collectDescendantsOfType<KtImportDirective>()
    val imports = buildImports(
      wildcard = importDirectives.filter {
        it.importedName == null
      }.mapNotNull {
        it.importPath?.toString()
      },
      named = importDirectives.mapNotNull {
        val simpleName = it.alias?.toString() ?: it.importedName?.toString()
        val qualifiedName = it.importPath?.toString()
        if (simpleName != null && qualifiedName != null) {
          Pair(simpleName, qualifiedName)
        } else {
          null
        }
      }
    )

    val clazz = file.collectDescendantsOfType<KtClass> {
      it.isInterface() && it.annotationEntries.any {
        it.shortName.toString() == Syncable::class.java.simpleName
      }
    }.first()
    val body = clazz.findDescendantOfType<KtClassBody>()

    if (body != null) {
      val methods = body.collectDescendantsOfType<KtFunction> {
        it.annotationEntries.any {
          it.shortName.toString() == Slot::class.java.simpleName
        }
      }

      val subclassImports = body.children.mapNotNull {
        it as? KtClass
      }.map {
        Pair(it.name!!, typeElement.asClassName().canonicalName + "." + it.name)
      }.toMap()
      val importsWithSubclasses = imports + subclassImports

      return ParsedClass(
        name = typeElement.asClassName(),
        quasselName = clazz.parseAnnotations<Syncable>()["name"]
                      ?: clazz.name
                      ?: "",
        methods = methods.map { method ->
          parseMethod(method, importsWithSubclasses)
        }
      )
    }
    return null
  }

  private fun parseMethod(method: KtFunction, imports: Map<String, String>) = ParsedMethod(
    name = method.name
           ?: "",
    quasselName = method.parseAnnotations<Slot>()["value"]
                  ?: method.name
                  ?: "",
    parameters = method
      .findDescendantOfType<KtParameterList>()
      ?.collectDescendantsOfType<KtParameter>()
      .orEmpty()
      .map {
        parseParameter(it, imports)
      }
  )

  private fun parseTypeReference(typeReference: KtTypeReference?,
                                 imports: Map<String, String>): TypeName {
    val child = typeReference?.firstChild
    return when (child) {
             is KtUserType     -> parseUserType(child, imports)
             is KtNullableType -> parseNullableType(child, imports)
             else              -> throw IllegalArgumentException("Invalid Type")
           } ?: throw IllegalArgumentException("Invalid Type")
  }

  private fun parseUserType(type: KtUserType, imports: Map<String, String>): TypeName? {
    val qualifiedName = resolveImport(imports, type.referencedName.toString())
    val typeArguments = type.children.mapNotNull {
      it as? KtTypeArgumentList
    }.firstOrNull()?.children.orEmpty().mapNotNull {
      it as? KtTypeProjection
    }.mapNotNull {
      it.typeReference
    }.map {
      parseTypeReference(it, imports)
    }.toTypedArray()
    val (packageName, className) = splitQualifiedName(qualifiedName)
    val typeName = ClassName(packageName, className)
    return if (typeArguments.isEmpty()) {
      typeName
    } else {
      typeName.parameterizedBy(*typeArguments)
    }
  }

  private fun parseNullableType(type: KtNullableType, imports: Map<String, String>) =
    type.findDescendantOfType<KtUserType>()?.let {
      parseUserType(it, imports)
    }?.copy(nullable = true)

  private fun parseParameter(parameter: KtParameter, imports: Map<String, String>) =
    ParsedParameter(
      parameter.name!!,
      parseTypeReference(parameter.findDescendantOfType(), imports)
    )

  private inline fun <reified T> KtAnnotated.parseAnnotations(): Map<String, String?> =
    annotationEntries
      .first {
        it.shortName.toString() == T::class.java.simpleName
      }
      .findDescendantOfType<KtValueArgumentList>()
      ?.collectDescendantsOfType<KtValueArgument>()
      .orEmpty()
      .map {
        Pair(
          it.findDescendantOfType<KtValueArgumentName>()
            ?.findDescendantOfType<KtReferenceExpression>()
            ?.text
          ?: "value",
          it.findDescendantOfType<KtLiteralStringTemplateEntry>()?.text
        )
      }
      .toMap()

  private fun resolveImport(imports: Map<String, String>, import: String): String {
    val qualifiedName = imports.getOrDefault(import, import)
    return JavaToKotlinClassMap.mapJavaToKotlin(FqName(qualifiedName))?.asSingleFqName()?.asString()
           ?: qualifiedName
  }

  private fun resolveWildcardImport(import: String): List<Pair<String, String>> {
    val imports = mutableListOf<Pair<String, String>>()

    val packageName = import.removeSuffix(".*")
    val packagePath = packageName.replace('.', '/')
    val folder = File(context.sourcePath, packagePath)
    val sourceFiles = folder.listFiles()?.filter { it.isFile }
    if (sourceFiles == null) {
      val jarURLConnection = Object::class.java.getResource("Object.class").openConnection() as JarURLConnection
      val jdkFile = jarURLConnection.jarFile
      for (classEntry in jdkFile.entries()) {
        if (classEntry.name.endsWith(".class") &&
            classEntry.name.startsWith(packagePath) &&
            !classEntry.name.contains('$')) {
          val qualifiedName = classEntry.name.removeSuffix(".class").replace('/', '.')
          val (_, simpleName) = splitQualifiedName(qualifiedName)
          imports.add(Pair(simpleName, qualifiedName))
        }
      }
    } else {
      for (sourceFile in sourceFiles) {
        val source = sourceFile.readText(Charsets.UTF_8)
        val file = PsiFileFactory.getInstance(environment.project)
          .createFileFromText(KotlinLanguage.INSTANCE, source)

        val foundPackageName = file.findDescendantOfType<KtPackageDirective>()
          ?.qualifiedName

        val classes = file.findDescendantOfType<KtScript>()?.children?.mapNotNull {
          it as? KtBlockExpression
        }.orEmpty().flatMap { it.children.asIterable() }.mapNotNull {
          it as? KtClass
        }
        for (clazz in classes) {
          val className = clazz.name
          if (className != null) {
            imports.add(
              Pair(
                className,
                listOfNotNull(foundPackageName, className).joinToString(".")
              )
            )
          }
        }

        val typeAliases = file.collectDescendantsOfType<KtTypeAlias>()
        for (typeAlias in typeAliases) {
          val className = typeAlias.name
          if (className != null) {
            imports.add(Pair(
              className,
              listOfNotNull(foundPackageName, className).joinToString(".")))
          }
        }
      }
    }
    return imports
  }

  private fun buildImports(wildcard: List<String>, named: List<Pair<String, String>>) =
    (wildcard.flatMap(this::resolveWildcardImport) + named).toMap()
}
