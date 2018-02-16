package de.kuschku.quasseldroid_ng.util.compatibility

import de.kuschku.libquassel.util.compatibility.StreamChannelFactory
import de.kuschku.quasseldroid_ng.util.backport.ReadableWrappedChannel
import de.kuschku.quasseldroid_ng.util.backport.WritableWrappedChannel
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
