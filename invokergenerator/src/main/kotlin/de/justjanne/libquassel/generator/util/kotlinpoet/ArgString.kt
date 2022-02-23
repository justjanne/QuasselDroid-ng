/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.util.kotlinpoet

class ArgString constructor(
  val name: String,
  vararg val args: Any?
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ArgString

    if (name != other.name) return false
    if (!args.contentEquals(other.args)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + args.contentHashCode()
    return result
  }

  override fun toString(): String {
    return "ArgString(name='$name', args=${args.contentToString()})"
  }
}
