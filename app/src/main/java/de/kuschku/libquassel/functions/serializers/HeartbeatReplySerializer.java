package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.Heartbeat;
import de.kuschku.libquassel.functions.types.HeartbeatReply;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertTrue;

public class HeartbeatReplySerializer implements FunctionSerializer<HeartbeatReply> {
    @NonNull
    private static final HeartbeatReplySerializer serializer = new HeartbeatReplySerializer();

    private HeartbeatReplySerializer() {
    }

    @NonNull
    public static HeartbeatReplySerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull HeartbeatReply data) {
        return Arrays.asList(
                FunctionType.HEARTBEATREPLY.id,
                new QVariant<>(data.dateTime)
        );
    }

    @NonNull
    @Override
    public HeartbeatReply deserialize(@NonNull List packedFunc) {
        assertTrue(packedFunc.size() == 1);

        return new HeartbeatReply((DateTime) ((QVariant) packedFunc.remove(0)).data);
    }
}
