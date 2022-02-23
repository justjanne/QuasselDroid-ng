/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.kuschku.libquassel.quassel.exceptions

import java.lang.Exception

sealed class RpcInvocationFailedException(message: String) : Exception(message) {
  data class InvokerNotFoundException(
    val className: String
  ) : RpcInvocationFailedException("Could not find invoker for $className")

  data class SyncableNotFoundException(
    val className: String,
    val objectName: String
  ) : RpcInvocationFailedException("Could not find syncable $objectName for type $className")

  data class UnknownMethodException(
    val className: String,
    val methodName: String
  ) : RpcInvocationFailedException("Could not find method $methodName for type $className")

  data class WrongObjectTypeException(
    val obj: Any?,
    val type: String
  ) : RpcInvocationFailedException("Wrong type for invoker, expected $type but got $obj")
}
