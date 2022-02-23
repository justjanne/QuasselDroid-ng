/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.kuschku.libquassel.quassel.syncables.interfaces.invokers

import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.quassel.exceptions.RpcInvocationFailedException
import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject

interface Invoker {
  val className: String

  @Throws(RpcInvocationFailedException::class)
  fun invoke(on: ISyncableObject, method: String, params: QVariantList)
}
