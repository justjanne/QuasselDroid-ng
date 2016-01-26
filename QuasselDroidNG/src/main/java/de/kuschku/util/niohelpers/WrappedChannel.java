package de.kuschku.util.niohelpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.InterruptibleChannel;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import de.kuschku.util.CompatibilityUtils;

public class WrappedChannel implements Flushable, ByteChannel, InterruptibleChannel {
    @Nullable
    private final InputStream rawIn;
    @Nullable
    private final OutputStream rawOut;
    @Nullable
    private DataInputStream in;
    @Nullable
    private DataOutputStream out;

    private WrappedChannel(@Nullable InputStream in, @Nullable OutputStream out) {
        this.rawIn = in;
        this.rawOut = out;
        if (this.rawIn != null) this.in = new DataInputStream(rawIn);
        if (this.rawOut != null) this.out = new DataOutputStream(rawOut);
    }

    @NonNull
    public static WrappedChannel ofStreams(@Nullable InputStream in, @Nullable OutputStream out) {
        return new WrappedChannel(in, out);
    }

    @NonNull
    public static WrappedChannel ofSocket(@NonNull Socket s) throws IOException {
        return new WrappedChannel(s.getInputStream(), s.getOutputStream());
    }

    @Nullable
    public static WrappedChannel withCompression(@NonNull WrappedChannel channel) {
        return new WrappedChannel(
                new InflaterInputStream(channel.rawIn),
                CompatibilityUtils.createDeflaterOutputStream(channel.rawOut)
        );
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
     * complete. </p>
     *
     * @param dst The buffer into which bytes are to be transferred
     * @return The number of bytes read, possibly zero, or <tt>-1</tt> if the
     * channel has reached end-of-stream
     * @throws IOException If some other I/O Error occurs
     */
    @Override
    public int read(@NonNull ByteBuffer dst) throws IOException {
        if (in == null) return 0;

        in.readFully(dst.array(), dst.arrayOffset(), dst.array().length - dst.arrayOffset());
        return dst.array().length;
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
    @Override
    public int write(@NonNull ByteBuffer src) throws IOException {
        if (out == null) return 0;

        out.write(src.array(), src.arrayOffset(), src.array().length - src.arrayOffset());
        return src.array().length;
    }

    /**
     * Tells whether or not this channel is open.
     *
     * @return <tt>true</tt> if, and only if, this channel is open
     */
    @Override
    public boolean isOpen() {
        return true;
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
    @Override
    public void close() throws IOException {
        if (rawIn != null) rawIn.close();
        if (rawOut != null) rawOut.close();
    }

    /**
     * Flushes this stream by writing any buffered output to the underlying
     * stream.
     *
     * @throws IOException If an I/O Error occurs
     */
    @Override
    public void flush() throws IOException {
        if (rawOut instanceof DeflaterOutputStream) rawOut.flush();
    }
}
