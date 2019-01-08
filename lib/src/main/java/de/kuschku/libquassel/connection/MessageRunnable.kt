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

package de.kuschku.libquassel.connection

import de.kuschku.libquassel.protocol.primitive.serializer.Serializer
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.WARN
import de.kuschku.libquassel.util.helpers.write
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import de.kuschku.libquassel.util.nio.WrappedChannel
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicReference

class MessageRunnable<T>(
  private val data: T,
  private val serializer: Serializer<T>,
  private val chainedBuffer: ChainedByteBuffer,
  private val channel: WrappedChannel?,
  private val features: QuasselFeatures
) : () -> Unit {
  override fun invoke() {
    try {
      serializer.serialize(chainedBuffer, data, features)
      val sizeBuffer = ByteBuffer.allocateDirect(4)
      sizeBuffer.putInt(chainedBuffer.size)
      sizeBuffer.flip()
      channel?.write(sizeBuffer)
      channel?.write(chainedBuffer)
      channel?.flush()
      lastSent.set(data)
    } catch (e: Throwable) {
      log(WARN, "MessageDispatching", e)
    }
  }

  companion object {
    val lastSent = AtomicReference<Any>()
  }
}
