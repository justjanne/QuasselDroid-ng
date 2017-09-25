/*
 * Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package de.kuschku.quasseldroid_ng.util.backport

import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.spi.AbstractInterruptibleChannel

class ReadableStreamChannel(
  private var stream: InputStream
) : AbstractInterruptibleChannel(), ReadableByteChannel {
  private var buffer = ByteArray(0)
  private var open = true
  private val readLock = Any()

  @Throws(IOException::class)
  override fun read(dst: ByteBuffer): Int {
    val len = dst.remaining()
    var totalRead = 0
    var bytesRead = 0
    synchronized(readLock) {
      while (totalRead < len) {
        val bytesToRead = Math.min(len - totalRead,
                                   TRANSFER_SIZE)

        if (buffer.size < bytesToRead)
          buffer = ByteArray(bytesToRead)
        if ((totalRead > 0) && !(stream.available() > 0))
          break // block at most once
        try {
          begin()
          bytesRead = stream.read(buffer, 0, bytesToRead)
        } finally {
          end(bytesRead > 0)
        }
        if (bytesRead < 0)
          break
        else
          totalRead += bytesRead
        dst.put(buffer, 0, bytesRead)
      }
      if (bytesRead < 0 && totalRead == 0)
        return -1

      return totalRead
    }
  }

  @Throws(IOException::class)
  override fun implCloseChannel() {
    stream.close()
    open = false
  }

  companion object {
    private val TRANSFER_SIZE = 8192
  }
}
