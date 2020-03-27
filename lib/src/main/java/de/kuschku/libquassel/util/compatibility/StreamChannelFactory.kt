/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.libquassel.util.compatibility

import de.kuschku.libquassel.util.compatibility.reference.JavaStreamChannelFactory
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

interface StreamChannelFactory {
  fun create(stream: InputStream): ReadableByteChannel
  fun create(stream: OutputStream): WritableByteChannel

  companion object : StreamChannelFactory {
    override fun create(stream: InputStream) = instance.create(stream)
    override fun create(stream: OutputStream) = instance.create(stream)
    var instance: StreamChannelFactory = JavaStreamChannelFactory
  }
}
