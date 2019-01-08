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

package de.kuschku.quasseldroid.util.compatibility

import de.kuschku.libquassel.util.compatibility.StreamChannelFactory
import de.kuschku.quasseldroid.util.backport.ReadableWrappedChannel
import de.kuschku.quasseldroid.util.backport.WritableWrappedChannel
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

object AndroidStreamChannelFactory : StreamChannelFactory {
  override fun create(stream: InputStream): ReadableByteChannel = ReadableWrappedChannel(stream)
  override fun create(stream: OutputStream): WritableByteChannel = WritableWrappedChannel(stream)

  fun inject() {
    StreamChannelFactory.instance = this
  }
}
