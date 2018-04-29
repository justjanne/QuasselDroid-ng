/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.malheur.data

import android.os.Debug

data class MemoryInfo(
  var dalvikPss: Int?,
  var dalvikPrivateDirty: Int?,
  var dalvikSharedDirty: Int?,

  var nativePss: Int?,
  var nativePrivateDirty: Int?,
  var nativeSharedDirty: Int?,

  var otherPss: Int?,
  var otherPrivateDirty: Int?,
  var otherSharedDirty: Int?
) {
  constructor(memoryInfo: Debug.MemoryInfo?) : this(
    dalvikPss = memoryInfo?.dalvikPss,
    dalvikPrivateDirty = memoryInfo?.dalvikPrivateDirty,
    dalvikSharedDirty = memoryInfo?.dalvikSharedDirty,

    nativePss = memoryInfo?.nativePss,
    nativePrivateDirty = memoryInfo?.nativePrivateDirty,
    nativeSharedDirty = memoryInfo?.nativeSharedDirty,

    otherPss = memoryInfo?.otherPss,
    otherPrivateDirty = memoryInfo?.otherPrivateDirty,
    otherSharedDirty = memoryInfo?.otherSharedDirty
  )
}
