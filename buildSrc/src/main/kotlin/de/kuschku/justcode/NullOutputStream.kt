/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package de.kuschku.justcode

import java.io.IOException
import java.io.OutputStream

class NullOutputStream : OutputStream() {
  @Volatile
  private var closed = false

  @Throws(IOException::class)
  private fun ensureOpen() {
    if (closed) {
      throw IOException("Stream closed")
    }
  }

  @Throws(IOException::class)
  override fun write(b: Int) {
    ensureOpen()
  }

  @Throws(IOException::class)
  override fun write(b: ByteArray, off: Int, len: Int) {
    ensureOpen()
  }

  override fun close() {
    closed = true
  }
}
