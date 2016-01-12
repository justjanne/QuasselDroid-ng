package de.kuschku.libquassel.protocols;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.CoreConnection;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.serializers.InitDataFunctionSerializer;
import de.kuschku.libquassel.functions.serializers.InitRequestFunctionSerializer;
import de.kuschku.libquassel.functions.serializers.UnpackedInitDataFunctionSerializer;
import de.kuschku.libquassel.functions.serializers.UnpackedRpcCallFunctionSerializer;
import de.kuschku.libquassel.functions.serializers.UnpackedSyncFunctionSerializer;
import de.kuschku.libquassel.functions.types.HandshakeFunction;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.objects.MessageTypeRegistry;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.serializers.IntSerializer;
import de.kuschku.libquassel.primitives.serializers.PrimitiveSerializer;
import de.kuschku.libquassel.primitives.serializers.VariantSerializer;
import de.kuschku.libquassel.primitives.serializers.VariantVariantListSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.util.niohelpers.WrappedChannel;

import static de.kuschku.libquassel.primitives.QMetaType.Type.QVariantList;
import static de.kuschku.libquassel.primitives.QMetaType.Type.QVariantMap;

/**
 * A helper class processing incoming and outgoing messages.
 * Implements the Legacy Protocol with modern Handshake
 *
 * @author Janne Koschinski
 */
public class LegacyPeer implements RemotePeer {
    private ByteBuffer buffer;
    private CoreConnection connection;
    private BusProvider busProvider;

    public LegacyPeer(CoreConnection connection, BusProvider busProvider) {
        this.connection = connection;
        this.busProvider = busProvider;
        this.busProvider.dispatch.register(this);
    }

    public final void onEventBackgroundThread(SyncFunction func) {
        final List serialize = UnpackedSyncFunctionSerializer.get().serialize(func);
        connection.getOutputExecutor().submit(new OutputRunnable<>(VariantSerializer.get(),
                new QVariant(new QMetaType(List.class, QMetaType.Type.QVariantList, VariantVariantListSerializer.get()),
                        serialize)));
    }

    public void onEventBackgroundThread(RpcCallFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(VariantSerializer.get(),
                new QVariant<>(UnpackedRpcCallFunctionSerializer.get().serialize(func))));
    }

    public void onEventBackgroundThread(InitRequestFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(VariantSerializer.get(),
                new QVariant<>(InitRequestFunctionSerializer.get().serialize(func))));
    }

    public void onEventBackgroundThread(InitDataFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(VariantSerializer.get(),
                new QVariant<>(InitDataFunctionSerializer.get().serialize(func))));
    }

    public void onEventBackgroundThread(HandshakeFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                VariantSerializer.get(), MessageTypeRegistry.toVariantMap(func.data)));
    }

    public void processMessage() throws IOException {
        buffer = ByteBuffer.allocate(4);
        connection.getChannel().read(buffer);

        final int size = IntSerializer.get().deserialize(buffer);
        if (size == 0) return;

        buffer = ByteBuffer.allocate(size);
        connection.getChannel().read(buffer);

        // TODO: Put this into a future with a time limit, and parallelize it.
        final QVariant data = VariantSerializer.get().deserialize(buffer);
        if (data.type.type == QVariantMap) {
            busProvider.handle(MessageTypeRegistry.from((Map<String, QVariant>) data.data));
        } else if (data.type.type == QVariantList) {
            final FunctionType type = FunctionType.fromId((Integer) ((List<Object>) data.data).remove(0));
            switch (type) {
                case SYNC:
                    busProvider.handle(UnpackedSyncFunctionSerializer.get().deserialize((List<QVariant>) data.data));
                    break;
                case RPCCALL:
                    busProvider.handle(UnpackedRpcCallFunctionSerializer.get().deserialize((List<QVariant>) data.data));
                    break;
                case INITREQUEST:
                    busProvider.handle(InitRequestFunctionSerializer.get().deserialize((List<QVariant>) data.data));
                    break;
                case INITDATA:
                    busProvider.handle(UnpackedInitDataFunctionSerializer.get().deserialize((List<QVariant>) data.data));
                    break;
                case HEARTBEAT:
                case HEARTBEATREPLY:
                default:
                    busProvider.sendEvent(new GeneralErrorEvent("Unknown package received: " + data));
                    break;
            }
        }
    }

    @Override
    public ByteBuffer getBuffer() {
        return buffer;
    }

    /**
     * A special runnable that serializes an object into a buffer, writes the size of the buffer, and, if necessary,
     * compresses it with deflate.
     *
     * @param <T>
     */
    private class OutputRunnable<T> implements Runnable {
        private final T data;
        private final PrimitiveSerializer<T> serializer;

        public OutputRunnable(PrimitiveSerializer<T> serializer, T data) {
            this.data = data;
            this.serializer = serializer;
        }

        @Override
        public void run() {
            try {
                // TODO: Reuse buffer

                // Create a buffer
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                // Wrap it in a fake channel
                final WrappedChannel fakeChannel = WrappedChannel.ofStreams(null, new DataOutputStream(out));
                // Serialize the object into the buffer-channel
                serializer.serialize(fakeChannel, data);
                // Write the size of the buffer over the network
                IntSerializer.get().serialize(connection.getChannel(), out.size());
                // Write the content of the buffer over the network
                connection.getChannel().write(ByteBuffer.wrap(out.toByteArray()));
                // Flush the deflater, if existing
                connection.getChannel().flush();
                // Close the buffer
                fakeChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
