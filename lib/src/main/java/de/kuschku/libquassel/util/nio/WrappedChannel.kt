package de.kuschku.libquassel.util.nio

import de.kuschku.libquassel.connection.HostnameVerifier
import de.kuschku.libquassel.connection.SocketAddress
import de.kuschku.libquassel.util.compatibility.CompatibilityUtils
import de.kuschku.libquassel.util.compatibility.StreamChannelFactory
import java.io.Flushable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel
import java.nio.channels.InterruptibleChannel
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel
import java.security.GeneralSecurityException
import java.security.cert.X509Certificate
import java.util.zip.InflaterInputStream
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class WrappedChannel(
  private val socket: Socket,
  private var rawInStream: InputStream? = null,
  private var rawOutStream: OutputStream? = null,
  private var flusher: (() -> Unit)? = null
) : Flushable, ByteChannel, InterruptibleChannel {
  private var rawIn: ReadableByteChannel? = null
  private var rawOut: WritableByteChannel? = null

  init {
    val rawInStream = this.rawInStream
    if (rawInStream != null)
      this.rawIn = StreamChannelFactory.create(rawInStream)

    val rawOutStream = this.rawOutStream
    if (rawOutStream != null)
      this.rawOut = StreamChannelFactory.create(rawOutStream)
  }

  companion object {
    fun ofSocket(s: Socket): WrappedChannel {
      return WrappedChannel(
        s, s.getInputStream(),
        s.getOutputStream()
      )
    }
  }

  fun withCompression(): WrappedChannel {
    val deflaterOutputStream = CompatibilityUtils.createDeflaterOutputStream(rawOutStream)
    return WrappedChannel(
      socket, InflaterInputStream(rawInStream), deflaterOutputStream,
      deflaterOutputStream::flush
    )
  }

  @Throws(GeneralSecurityException::class, IOException::class)
  fun withSSL(certificateManager: X509TrustManager, hostnameVerifier: HostnameVerifier,
              address: SocketAddress): WrappedChannel {
    val context = SSLContext.getInstance("TLSv1.2")
    val managers = arrayOf(certificateManager)
    context.init(null, managers, null)
    val factory = context.socketFactory
    SSLSocketFactory.getDefault()

    val socket = factory.createSocket(socket, address.host, address.port, true) as SSLSocket
    socket.useClientMode = true
    socket.addHandshakeCompletedListener {
      hostnameVerifier.checkValid(
        address,
        socket.session.peerCertificates.map { it as X509Certificate }.toTypedArray()
      )
    }
    socket.startHandshake()
    return WrappedChannel.ofSocket(socket)
  }

  /**
   * Reads a sequence of bytes from this channel into the given buffer.
   * <p>
   * <p> An attempt is made to read up to <i>r</i> bytes from the channel,
   * where <i>r</i> is the number of bytes remaining in the buffer, that is,
   * <tt>dst.remaining()</tt>, at the moment this method is invoked.
   * <p>
   * <p> Suppose that a byte sequence of length <i>n</i> is read, where
   * <tt>0</tt>&nbsp;<tt>&lt;=</tt>&nbsp;<i>n</i>&nbsp;<tt>&lt;=</tt>&nbsp;<i>r</i>.
   * This byte sequence will be transferred into the buffer so that the first
   * byte in the sequence is at index <i>p</i> and the last byte is at index
   * <i>p</i>&nbsp;<tt>+</tt>&nbsp;<i>n</i>&nbsp;<tt>-</tt>&nbsp;<tt>1</tt>,
   * where <i>p</i> is the buffer's position at the moment this method is
   * invoked.  Upon return the buffer's position will be equal to
   * <i>p</i>&nbsp;<tt>+</tt>&nbsp;<i>n</i>; its limit will not have changed.
   * <p>
   * <p> A read operation might not fill the buffer, and in fact it might not
   * read any bytes at all.  Whether or not it does so depends upon the
   * nature and state of the channel.  A socket channel in non-blocking mode,
   * for example, cannot read any more bytes than are immediately available
   * from the socket's input buffer; similarly, a file channel cannot read
   * any more bytes than remain in the file.  It is guaranteed, however, that
   * if a channel is in blocking mode and there is at least one byte
   * remaining in the buffer then this method will block until at least one
   * byte is read.
   * <p>
   * <p> This method may be invoked at any time.  If another thread has
   * already initiated a read operation upon this channel, however, then an
   * invocation of this method will block until the first operation is
   * complete.
   *
   * @param dst The buffer into which bytes are to be transferred
   * @return The number of bytes read, possibly zero, or <tt>-1</tt> if the
   * channel has reached end-of-stream
   * @throws IOException If some other I/O Error occurs
   */
  @Throws(IOException::class)
  override fun read(dst: ByteBuffer): Int {
    val stream = rawIn ?: throw SocketException("Socket Closed")
    return stream.read(dst)
  }

  /**
   * Writes a sequence of bytes to this channel from the given buffer.
   * <p>
   * <p> An attempt is made to write up to <i>r</i> bytes to the channel,
   * where <i>r</i> is the number of bytes remaining in the buffer, that is,
   * <tt>src.remaining()</tt>, at the moment this method is invoked.
   * <p>
   * <p> Suppose that a byte sequence of length <i>n</i> is written, where
   * <tt>0</tt>&nbsp;<tt>&lt;=</tt>&nbsp;<i>n</i>&nbsp;<tt>&lt;=</tt>&nbsp;<i>r</i>.
   * This byte sequence will be transferred from the buffer starting at index
   * <i>p</i>, where <i>p</i> is the buffer's position at the moment this
   * method is invoked; the index of the last byte written will be
   * <i>p</i>&nbsp;<tt>+</tt>&nbsp;<i>n</i>&nbsp;<tt>-</tt>&nbsp;<tt>1</tt>.
   * Upon return the buffer's position will be equal to
   * <i>p</i>&nbsp;<tt>+</tt>&nbsp;<i>n</i>; its limit will not have changed.
   * <p>
   * <p> Unless otherwise specified, a write operation will return only after
   * writing all of the <i>r</i> requested bytes.  Some types of channels,
   * depending upon their state, may write only some of the bytes or possibly
   * none at all.  A socket channel in non-blocking mode, for example, cannot
   * write any more bytes than are free in the socket's output buffer.
   * <p>
   * <p> This method may be invoked at any time.  If another thread has
   * already initiated a write operation upon this channel, however, then an
   * invocation of this method will block until the first operation is
   * complete. </p>
   *
   * @param src The buffer from which bytes are to be retrieved
   * @return The number of bytes written, possibly zero
   * @throws IOException If some other I/O Error occurs
   */
  @Throws(IOException::class)
  override fun write(src: ByteBuffer): Int {
    val stream = rawOut ?: throw SocketException("Socket Closed")
    return stream.write(src)
  }

  override fun isOpen(): Boolean {
    return rawIn != null || rawOut != null
  }

  /**
   * Closes this channel.
   * <p>
   * <p> After a channel is closed, any further attempt to invoke I/O
   * operations upon it will cause a {@link ClosedChannelException} to be
   * thrown.
   * <p>
   * <p> If this channel is already closed then invoking this method has no
   * effect.
   * <p>
   * <p> This method may be invoked at any time.  If some other thread has
   * already invoked it, however, then another invocation will block until
   * the first invocation is complete, after which it will return without
   * effect. </p>
   *
   * @throws IOException If an I/O Error occurs
   */
  @Throws(IOException::class)
  override fun close() {
    rawIn?.close()
    rawIn = null
    rawOut?.close()
    rawOut = null
  }

  /**
   * Flushes this stream by writing any buffered output to the underlying
   * stream.
   *
   * @throws IOException If an I/O Error occurs
   */
  @Throws(IOException::class)
  override fun flush() {
    flusher?.invoke()
  }

  val sslSession
    get() = (socket as? SSLSocket)?.session
}
