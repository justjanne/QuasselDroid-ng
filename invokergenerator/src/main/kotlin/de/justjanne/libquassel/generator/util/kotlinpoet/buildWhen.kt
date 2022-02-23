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

inline fun CodeBlock.Builder.buildWhen(name: String, vararg args: Any?, f: WhenBlockBuilder.() -> Unit) {
  this.add(WhenBlockBuilder(name, args).apply(f).build())
}
