/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.util.kotlinpoet

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock

class WhenBlockBuilder constructor(
  private val over: ArgString
) {
  private val cases = mutableListOf<Pair<ArgString, CodeBlock>>()

  constructor(name: String, vararg args: Any?) : this(ArgString(name, args))

  fun addCase(condition: ArgString, block: CodeBlock) {
    cases.add(Pair(condition, block))
  }

  fun build(): CodeBlock = buildCodeBlock {
    beginControlFlow("when (${over.name})", over.args)
    for ((condition, code) in cases) {
      beginControlFlow("${condition.name} ->", *condition.args)
      add(code)
      endControlFlow()
    }
    endControlFlow()
  }

  inline fun addCase(name: String, vararg args: Any?, f: CodeBlock.Builder.() -> Unit) {
    addCase(ArgString(name, args), buildCodeBlock(f))
  }

  inline fun buildElse(f: CodeBlock.Builder.() -> Unit) {
    addCase(ArgString("else"), buildCodeBlock(f))
  }
}
