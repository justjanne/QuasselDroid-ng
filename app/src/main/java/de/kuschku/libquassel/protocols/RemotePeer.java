package de.kuschku.libquassel.protocols;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.kuschku.libquassel.functions.types.HandshakeFunction;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;

public interface RemotePeer {
    int DATASTREAM = 0x02;
    int LEGACY = 0x01;
    int PROTOCOL_VERSION_LEGACY = 10;

    void onEventBackgroundThread(SyncFunction func) throws IOException;

    void onEventBackgroundThread(RpcCallFunction func) throws IOException;

    void onEventBackgroundThread(InitRequestFunction func) throws IOException;

    void onEventBackgroundThread(InitDataFunction func) throws IOException;

    void onEventBackgroundThread(HandshakeFunction func) throws IOException;

    void processMessage() throws IOException;

    ByteBuffer getBuffer();
}
