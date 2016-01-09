package de.kuschku.libquassel.protocols;

import android.support.annotation.NonNull;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.CoreConnection;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.serializers.InitDataFunctionSerializer;
import de.kuschku.libquassel.functions.serializers.InitRequestFunctionSerializer;
import de.kuschku.libquassel.functions.serializers.PackedInitDataFunctionSerializer;
import de.kuschku.libquassel.functions.serializers.PackedRpcCallFunctionSerializer;
import de.kuschku.libquassel.functions.serializers.PackedSyncFunctionSerializer;
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
import de.kuschku.libquassel.primitives.serializers.VariantVariantListSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.util.niohelpers.WrappedChannel;

/**
 * A helper class processing incoming and outgoing messages.
 * Implements the Qt Datastream protocol.
 *
 * @author Janne Koschinski
 */
public class DatastreamPeer implements RemotePeer {
    private final IntSerializer intSerializer = new IntSerializer();
    private final VariantVariantListSerializer variantListSerializer = new VariantVariantListSerializer<>();

    private ByteBuffer buffer;
    private CoreConnection connection;
    private BusProvider busProvider;

    public DatastreamPeer(CoreConnection connection, BusProvider busProvider) {
        this.connection = connection;
        this.busProvider = busProvider;
        this.busProvider.dispatch.register(this);
    }

    public static List<QVariant> mapToList(Map<String, QVariant> data) {
        final List<QVariant> list = new ArrayList<>(data.size() * 2);
        for (Map.Entry<String, QVariant> entry : data.entrySet()) {
            list.add(new QVariant<>(QMetaType.Type.QByteArray, entry.getKey()));
            list.add(entry.getValue());
        }
        return list;
    }

    public static Map<String, QVariant> listToMap(List<QVariant> data) {
        final Map<String, QVariant> map = new HashMap<>(data.size() / 2);
        for (int i = 0; i < data.size(); i += 2) {
            map.put((String) data.get(i).data, data.get(i + 1));
        }
        return map;
    }

    public static <T> Map<T, T> unboxedListToMap(List<T> data) {
        final Map<T, T> map = new HashMap<>(data.size() / 2);
        for (int i = 0; i < data.size(); i += 2) {
            map.put(data.get(i), data.get(i + 1));
        }
        return map;
    }

    @NonNull
    public static List unboxList(List packedFunc) {
        return Lists.transform(packedFunc, new Function<QVariant, Object>() {
            @Override
            public Object apply(QVariant input) {
                return input.data;
            }
        });
    }

    public void onEventBackgroundThread(SyncFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                new VariantVariantListSerializer(),
                new UnpackedSyncFunctionSerializer().serialize(func))
        );
    }

    public void onEventBackgroundThread(RpcCallFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                new VariantVariantListSerializer(),
                new UnpackedRpcCallFunctionSerializer().serialize(func))
        );
    }

    public void onEventBackgroundThread(InitRequestFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                new VariantVariantListSerializer(),
                new InitRequestFunctionSerializer().serializePacked(func)
        ));
    }

    public void onEventBackgroundThread(InitDataFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                new VariantVariantListSerializer(),
                new InitDataFunctionSerializer().serialize(func))
        );
    }

    public void onEventBackgroundThread(HandshakeFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                new VariantVariantListSerializer(),
                DatastreamPeer.mapToList(MessageTypeRegistry.toVariantMap(func.data).data))
        );
    }

    private void handleHandshakeMessage(List<QVariant> data) {
        busProvider.handle(MessageTypeRegistry.from(DatastreamPeer.listToMap(data)));
    }

    private void handlePackedFunc(List<QVariant> data) {
        final FunctionType type = FunctionType.fromId((Integer) data.remove(0).data);
        switch (type) {
            case SYNC:
                busProvider.handle(new PackedSyncFunctionSerializer().deserialize(data));
                break;
            case RPCCALL:
                busProvider.handle(new PackedRpcCallFunctionSerializer().deserialize(data));
                break;
            case INITREQUEST:
                busProvider.handle(new InitRequestFunctionSerializer().deserialize(data));
                break;
            case INITDATA:
                busProvider.handle(new PackedInitDataFunctionSerializer().deserialize(data));
                break;
            case HEARTBEAT:
            case HEARTBEATREPLY:
            default:
                busProvider.sendEvent(new GeneralErrorEvent("Unknown package received: " + data));
                break;
        }
    }

    public void processMessage() throws IOException {
        buffer = ByteBuffer.allocate(4);
        connection.getChannel().read(buffer);

        final int size = intSerializer.deserialize(buffer);
        if (size == 0) return;

        buffer = ByteBuffer.allocate(size);
        connection.getChannel().read(buffer);

        // TODO: Put this into a future with a time limit, and parallelize it.
        final List<QVariant> data = variantListSerializer.deserialize(buffer);
        if (connection.getStatus() == ConnectionChangeEvent.Status.CONNECTING
                || connection.getStatus() == ConnectionChangeEvent.Status.HANDSHAKE
                || connection.getStatus() == ConnectionChangeEvent.Status.CORE_SETUP_REQUIRED
                || connection.getStatus() == ConnectionChangeEvent.Status.USER_SETUP_REQUIRED
                || connection.getStatus() == ConnectionChangeEvent.Status.LOGIN_REQUIRED) {
            handleHandshakeMessage(data);
        } else {
            handlePackedFunc(data);
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
                new IntSerializer().serialize(connection.getChannel(), out.size());
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
