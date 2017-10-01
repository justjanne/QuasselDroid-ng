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
