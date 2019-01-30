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

import de.kuschku.libquassel.annotations.Context
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoot
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CompilerConfiguration

class ParserEnvironment(
  private val context: Context
) {
  fun <T> use(f: (Parser) -> T): T {
    val rootDisposable = Disposer.newDisposable()
    try {
      val environment = KotlinCoreEnvironment.createForProduction(
        rootDisposable,
        CompilerConfiguration().apply {
          context.sourcePath?.let {
            addKotlinSourceRoot(it, isCommon = false)
          }
        },
        EnvironmentConfigFiles.JVM_CONFIG_FILES
      )

      val parser = Parser(context, environment)

      return f(parser)
    } finally {
      rootDisposable.dispose()
    }
  }
}
