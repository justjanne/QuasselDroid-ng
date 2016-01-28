package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.Heartbeat;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertTrue;

public class HeartbeatSerializer implements FunctionSerializer<Heartbeat> {
    @NonNull
    private static final HeartbeatSerializer serializer = new HeartbeatSerializer();

    private HeartbeatSerializer() {
    }

    @NonNull
    public static HeartbeatSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull Heartbeat data) {
        return Arrays.asList(
                FunctionType.HEARTBEAT.id,
                new QVariant<>(data.dateTime)
        );
    }

    @NonNull
    @Override
    public Heartbeat deserialize(@NonNull List packedFunc) {
        assertTrue(packedFunc.size() == 1);

        return new Heartbeat((DateTime) ((QVariant) packedFunc.remove(0)).data);
    }
}
