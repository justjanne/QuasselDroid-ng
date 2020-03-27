/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import javax.annotation.processing.Messager
import javax.tools.Diagnostic


fun Any?.toIndentString(): String {
  val notFancy = toString()
  return buildString(notFancy.length) {
    var indent = 0
    fun StringBuilder.line() {
      appendln()
      repeat(2 * indent) { append(' ') }
    }

    for (char in notFancy) {
      if (char == ' ') continue

      when (char) {
        ')', ']' -> {
          indent--
          line()
        }
      }

      if (char == '=') append(' ')
      append(char)
      if (char == '=') append(' ')

      when (char) {
        '(', '[', ',' -> {
          if (char != ',') indent++
          line()
        }
      }
    }
  }
}

fun splitQualifiedName(qualifiedName: String): Pair<String, String> {
  val index = qualifiedName.lastIndexOf('.')
  return if (index >= 0 && index + 1 < qualifiedName.length) {
    Pair(qualifiedName.substring(0, index),
         qualifiedName.substring(index + 1))
  } else {
    Pair("", qualifiedName)
  }
}

fun Messager.printAST(element: PsiElement, indent: String = "") {
  printMessage(Diagnostic.Kind.NOTE, "$indent$element {")
  for (child in element.children) {
    printAST(child, "$indent  ")
  }
  printMessage(Diagnostic.Kind.NOTE, "$indent}")
}
