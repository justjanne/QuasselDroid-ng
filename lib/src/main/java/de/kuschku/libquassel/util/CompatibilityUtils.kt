package de.kuschku.libquassel.util

import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream

object CompatibilityUtils {
  var supportsKeepAlive = false
  var supportsCompression = false

  /**
   * Creates a SyncFlush output stream, even if the current device does not support doing so
   * natively.
   *
   * @param rawOut the raw output stream to be wrapped
   * @return The wrapping output stream
   */
  fun createDeflaterOutputStream(rawOut: OutputStream?): DeflaterOutputStream {
    return if (supportsCompression) {
      DeflaterOutputStream(rawOut, true)
    } else {
      DeflaterOutputStream(rawOut, createSyncFlushDeflater())
    }
  }

  /**
   * Creates a SyncFlush Deflater for use on pre-KitKat Android
   *
   * @return The modified Deflater, or null if the creation failed
   */
  private fun createSyncFlushDeflater(): Deflater? {
    val def = Deflater()
    try {
      val f = def.javaClass.getDeclaredField("flushParm")
      f.isAccessible = true
      f.setInt(def, 2) // Z_SYNC_FLUSH
    } catch (e: Exception) {
      return null
    }

    return def
  }
}
