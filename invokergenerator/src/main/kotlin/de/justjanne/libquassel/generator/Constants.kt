/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.generator.rpcmodel.RpcModel
import transformName

object Constants {
  fun invokerName(model: RpcModel.ObjectModel, side: ProtocolSide) = ClassName(
    TYPENAME_INVOKER.packageName,
    "${model.rpcName}${transformName(side.name)}Invoker"
  )

  val TYPENAME_ANY = ANY.copy(nullable = true)
  val TYPENAME_SYNCABLESTUB = ClassName(
    "de.kuschku.libquassel.quassel.syncables.interfaces",
    "ISyncableObject"
  )
  val TYPENAME_INVOKER = ClassName(
    "de.kuschku.libquassel.quassel.syncables.interfaces.invokers",
    "Invoker"
  )
  val TYPENAME_INVOKERREGISTRY = ClassName(
    "de.kuschku.libquassel.quassel.syncables.interfaces.invokers",
    "InvokerRegistry"
  )
  val TYPENAME_INVOKERMAP = MAP.parameterizedBy(STRING, TYPENAME_INVOKER)
  val TYPENAME_UNKNOWN_METHOD_EXCEPTION = ClassName(
    "de.kuschku.libquassel.quassel.exceptions",
    "RpcInvocationFailedException", "UnknownMethodException"
  )
  val TYPENAME_WRONG_OBJECT_TYPE_EXCEPTION = ClassName(
    "de.kuschku.libquassel.quassel.exceptions",
    "RpcInvocationFailedException", "WrongObjectTypeException"
  )
  val TYPENAME_QVARIANTLIST = ClassName(
    "de.kuschku.libquassel.protocol",
    "QVariantList"
  )
  val TYPENAME_QVARIANT_INTOORTHROW = ClassName(
    "de.kuschku.libquassel.protocol",
    "valueOrThrow"
  )
  val TYPENAME_QVARIANT_INTO = ClassName(
    "de.kuschku.libquassel.protocol",
    "value"
  )
  val TYPENAME_GENERATED = ClassName(
    "de.justjanne.libquassel.annotations",
    "Generated"
  )

  init {
    System.setProperty("idea.io.use.nio2", "true")
  }
}
