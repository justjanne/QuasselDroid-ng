package de.kuschku.quasseldroid_ng.util

import de.kuschku.libquassel.util.compatibility.StreamChannelFactory
import de.kuschku.quasseldroid_ng.util.backport.ReadableStreamChannel
import de.kuschku.quasseldroid_ng.util.backport.WritableStreamChannel
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

object AndroidStreamChannelFactory : StreamChannelFactory {
  override fun create(stream: InputStream): ReadableByteChannel = ReadableStreamChannel(stream)
  override fun create(stream: OutputStream): WritableByteChannel = WritableStreamChannel(stream)

  fun inject() {
    StreamChannelFactory.instance = this
  }
}
