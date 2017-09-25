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
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
import java.nio.channels.spi.AbstractInterruptibleChannel


class WritableStreamChannel(
  private var stream: OutputStream
) : AbstractInterruptibleChannel(), WritableByteChannel {
  private var buffer = ByteArray(0)
  private var open = true
  private val writeLock = Any()

  @Throws(IOException::class)
  override fun write(src: ByteBuffer): Int {
    val len = src.remaining()
    var totalWritten = 0
    synchronized(writeLock) {
      while (totalWritten < len) {
        val bytesToWrite = Math.min(len - totalWritten,
                                    TRANSFER_SIZE)

        if (buffer.size < bytesToWrite)
          buffer = ByteArray(bytesToWrite)
        src.get(buffer, 0, bytesToWrite)

        try {
          begin()
          stream.write(buffer, 0, bytesToWrite)
        } finally {
          end(bytesToWrite > 0)
        }
        totalWritten += bytesToWrite
      }
      return totalWritten
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
