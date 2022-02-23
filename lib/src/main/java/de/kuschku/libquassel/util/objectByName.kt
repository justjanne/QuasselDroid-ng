/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.protocol.util.reflect

inline fun <reified T> objectByName(name: String): T {
  val clazz = try {
    Class.forName(name)
  } catch (t: Throwable) {
    throw IllegalArgumentException("Could not load class $name", t)
  }
  val element = clazz.getDeclaredField("INSTANCE").get(null)
  require(element != null) {
    "No object found for $name"
  }
  require(element is T) {
    "Object of wrong type found for $name:" +
      "expected ${T::class.java.canonicalName}, " +
      "got ${element::class.java.canonicalName}"
  }
  return element
}
