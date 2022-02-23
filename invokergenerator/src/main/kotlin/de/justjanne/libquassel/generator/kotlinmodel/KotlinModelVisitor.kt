/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.justjanne.libquassel.generator.kotlinmodel

interface KotlinModelVisitor<D, R> {
  fun visitFileModel(model: KotlinModel.FileModel, data: D): R
  fun visitFunctionModel(model: KotlinModel.FunctionModel, data: D): R
}
