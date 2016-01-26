package de.kuschku.libquassel.protocols;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.kuschku.libquassel.functions.types.HandshakeFunction;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;

public interface RemotePeer {
    byte DATASTREAM = 0x02;
    byte LEGACY = 0x01;
    int PROTOCOL_VERSION_LEGACY = 10;

    void onEventBackgroundThread(@NonNull SyncFunction func);

    void onEventBackgroundThread(@NonNull RpcCallFunction func);

    void onEventBackgroundThread(@NonNull InitRequestFunction func);

    void onEventBackgroundThread(@NonNull InitDataFunction func);

    void onEventBackgroundThread(@NonNull HandshakeFunction func);

    void processMessage() throws IOException;

    @NonNull
    ByteBuffer getBuffer();
}
