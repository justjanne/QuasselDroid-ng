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
import com.google.devtools.ksp.symbol.KSType
import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.annotations.SyncedCall
import de.justjanne.libquassel.generator.util.findAnnotationWithType
import de.justjanne.libquassel.generator.util.getMember
import de.justjanne.libquassel.generator.util.toEnum

data class RpcFunctionAnnotation(
  val name: String?,
  val target: ProtocolSide?
) {
  companion object {
    fun of(it: KSAnnotated, resolver: Resolver): RpcFunctionAnnotation? {
      val annotation = it.findAnnotationWithType<SyncedCall>(resolver)
        ?: return null
      return RpcFunctionAnnotation(
        name = annotation.getMember<String>("name")?.ifBlank { null },
        target = annotation.getMember<KSType>("target")
          ?.toEnum<ProtocolSide>(),
      )
    }
  }
}
