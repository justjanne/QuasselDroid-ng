/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
@file:Suppress("NOTHING_TO_INLINE")

package de.kuschku.malheur.collectors

import de.kuschku.malheur.CrashContext

interface Collector<out DataType, in ConfigType> {
  fun collect(context: CrashContext, config: ConfigType): DataType?
}

inline fun <DataType, ConfigType> Collector<DataType, ConfigType>.collectIf(
  context: CrashContext,
  config: ConfigType?
) = if (config != null) collect(context, config) else null

inline fun <DataType> collectIf(enabled: Boolean, closure: () -> DataType?) =
  if (enabled) closure() else null
