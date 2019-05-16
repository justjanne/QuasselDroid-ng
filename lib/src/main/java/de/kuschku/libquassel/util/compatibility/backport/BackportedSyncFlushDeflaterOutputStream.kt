/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.libquassel.util.compatibility.backport

import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream

class BackportedSyncFlushDeflaterOutputStream(
  rawOut: OutputStream?,
  private val deflater: Deflater? = createSyncFlushDeflater()
) : DeflaterOutputStream(rawOut, deflater) {
  override fun close() {
    deflater?.end()
    super.close()
  }

  companion object {
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
}
