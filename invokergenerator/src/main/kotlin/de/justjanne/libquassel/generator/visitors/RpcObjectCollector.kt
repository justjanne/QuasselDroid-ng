/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.visitors

import de.justjanne.libquassel.generator.rpcmodel.RpcModel
import de.justjanne.libquassel.generator.rpcmodel.RpcModelVisitor

class RpcObjectCollector : RpcModelVisitor<Unit, Unit> {
  val objects = mutableListOf<RpcModel.ObjectModel>()
  override fun visitObjectModel(model: RpcModel.ObjectModel, data: Unit) {
    objects.add(model)
  }

  override fun visitFunctionModel(model: RpcModel.FunctionModel, data: Unit) = Unit
  override fun visitParameterModel(model: RpcModel.ParameterModel, data: Unit) = Unit
}
