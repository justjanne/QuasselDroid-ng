package de.kuschku.libquassel.protocols;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

import static de.kuschku.util.AndroidAssert.assertNotNull;

/**
 * A helper class processing incoming and outgoing messages.
 * Implements the Qt Datastream protocol.
 *
 * @author Janne Koschinski
 */
@SuppressWarnings({"unchecked"})
public class DatastreamPeer implements RemotePeer {
    @NonNull
    private final CoreConnection connection;
    @NonNull
    private final BusProvider busProvider;
    @NonNull
    private ByteBuffer buffer = ByteBuffer.allocate(0);

    public DatastreamPeer(@NonNull CoreConnection connection, @NonNull BusProvider busProvider) {
        this.connection = connection;
        this.busProvider = busProvider;
        this.busProvider.dispatch.register(this);
    }

    @NonNull
    public static List<QVariant<Object>> mapToList(@NonNull Map<String, QVariant> data) {
        final List<QVariant<Object>> list = new ArrayList<>(data.size() * 2);
        for (Map.Entry<String, QVariant> entry : data.entrySet()) {
            list.add(new QVariant<>(QMetaType.Type.QByteArray, entry.getKey()));
            list.add(entry.getValue());
        }
        return list;
    }

    @NonNull
    public static Map<String, QVariant> listToMap(@NonNull List<QVariant> data) {
        final Map<String, QVariant> map = new HashMap<>(data.size() / 2);
        for (int i = 0; i < data.size(); i += 2) {
            map.put((String) data.get(i).data, data.get(i + 1));
        }
        return map;
    }

    @NonNull
    public static <T> Map<T, T> unboxedListToMap(@NonNull List<T> data) {
        final Map<T, T> map = new HashMap<>(data.size() / 2);
        for (int i = 0; i < data.size(); i += 2) {
            map.put(data.get(i), data.get(i + 1));
        }
        return map;
    }

    @NonNull
    public static List unboxList(@NonNull List packedFunc) {
        return Lists.transform(packedFunc, new Function<QVariant, Object>() {
            @Override
            public Object apply(@Nullable QVariant input) {
                assertNotNull(input);

                return input.data;
            }
        });
    }

    public void onEventBackgroundThread(@NonNull SyncFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                VariantVariantListSerializer.<SyncFunction>get(),
                UnpackedSyncFunctionSerializer.get().serialize(func)
        ));
    }

    public void onEventBackgroundThread(@NonNull RpcCallFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                VariantVariantListSerializer.<RpcCallFunction>get(),
                UnpackedRpcCallFunctionSerializer.get().serialize(func)
        ));
    }

    public void onEventBackgroundThread(@NonNull InitRequestFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                VariantVariantListSerializer.<InitRequestFunction>get(),
                InitRequestFunctionSerializer.get().serializePacked(func)
        ));
    }

    public void onEventBackgroundThread(@NonNull InitDataFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                VariantVariantListSerializer.<InitDataFunction>get(),
                InitDataFunctionSerializer.get().serialize(func)
        ));
    }

    public void onEventBackgroundThread(@NonNull HandshakeFunction func) {
        connection.getOutputExecutor().submit(new OutputRunnable<>(
                VariantVariantListSerializer.get(),
                DatastreamPeer.mapToList(MessageTypeRegistry.toVariantMap(func.data).data)
        ));
    }

    private void handleHandshakeMessage(@NonNull List data) {
        busProvider.handle(MessageTypeRegistry.from(DatastreamPeer.listToMap(data)));
    }

    private void handlePackedFunc(@NonNull List<QVariant> data) {
        final FunctionType type = FunctionType.fromId((Integer) data.remove(0).data);
        switch (type) {
            case SYNC:
                busProvider.handle(PackedSyncFunctionSerializer.get().deserialize(data));
                break;
            case RPCCALL:
                busProvider.handle(PackedRpcCallFunctionSerializer.get().deserialize(data));
                break;
            case INITREQUEST:
                busProvider.handle(InitRequestFunctionSerializer.get().deserialize(data));
                break;
            case INITDATA:
                busProvider.handle(PackedInitDataFunctionSerializer.get().deserialize(data));
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

        final int size = IntSerializer.get().deserialize(buffer);

        if (size == 0) return;

        buffer = ByteBuffer.allocate(size);
        connection.getChannel().read(buffer);

        // TODO: Put this into a future with a time limit, and parallelize it.
        final List data = VariantVariantListSerializer.get().deserialize(buffer);
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

    @NonNull
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
        @NonNull
        private final T data;
        @NonNull
        private final PrimitiveSerializer<T> serializer;

        public OutputRunnable(@NonNull PrimitiveSerializer<T> serializer, @NonNull T data) {
            this.data = data;
            this.serializer = serializer;
        }

        @Override
        public void run() {
            assertNotNull(connection.getChannel());

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
