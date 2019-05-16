/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.libquassel.annotations

import com.google.auto.service.AutoService
import de.kuschku.libquassel.annotations.generator.Generator
import de.kuschku.libquassel.annotations.parser.ParserEnvironment
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("de.kuschku.libquassel.annotations.Syncable")
class InvokerProcessor : AbstractProcessor() {
  lateinit var parserEnvironment: ParserEnvironment
  lateinit var generator: Generator

  @Synchronized
  override fun init(processingEnv: ProcessingEnvironment) {
    val context = Context(processingEnv)
    parserEnvironment = ParserEnvironment(context)
    generator = Generator(context)
  }

  override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
    parserEnvironment.use { parser ->
      for (annotatedElement in roundEnv.getElementsAnnotatedWith(Syncable::class.java)) {
        val parsedClass = parser.parse(annotatedElement)
        if (parsedClass != null) {
          generator.generate(parsedClass)
        }
      }
    }
    return true
  }
}
