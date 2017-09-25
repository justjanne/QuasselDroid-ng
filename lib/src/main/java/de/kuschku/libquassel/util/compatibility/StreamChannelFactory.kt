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
