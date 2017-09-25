package de.kuschku.libquassel.util.compatibility.reference

import de.kuschku.libquassel.util.compatibility.StreamChannelFactory
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

object JavaStreamChannelFactory : StreamChannelFactory {
  override fun create(stream: InputStream): ReadableByteChannel = Channels.newChannel(stream)
  override fun create(stream: OutputStream): WritableByteChannel = Channels.newChannel(stream)

  fun inject() {
    StreamChannelFactory.instance = this
  }
}
