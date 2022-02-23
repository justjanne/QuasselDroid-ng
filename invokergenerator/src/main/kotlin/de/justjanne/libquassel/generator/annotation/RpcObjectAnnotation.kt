/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.annotation

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import de.justjanne.libquassel.annotations.SyncedObject
import de.justjanne.libquassel.generator.util.findAnnotationWithType
import de.justjanne.libquassel.generator.util.getMember

data class RpcObjectAnnotation(
  val name: String?
) {
  companion object {
    fun of(it: KSAnnotated, resolver: Resolver): RpcObjectAnnotation? {
      val annotation = it.findAnnotationWithType<SyncedObject>(resolver)
        ?: return null
      return RpcObjectAnnotation(
        name = annotation.getMember("name"),
      )
    }
  }
}
