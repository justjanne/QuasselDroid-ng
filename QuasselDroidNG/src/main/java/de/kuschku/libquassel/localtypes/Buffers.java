package de.kuschku.libquassel.localtypes;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.Network;

public class Buffers {
    private Buffers() {

    }

    public static Buffer fromType(BufferInfo info, Network network) {
        Buffer result;
        switch (info.type) {
            case QUERY:
                result = new QueryBuffer(info, network.getUser(info.name));
                break;
            case CHANNEL:
                result = new ChannelBuffer(info, network.getChannels().get(info.name));
                break;
            case STATUS:
                result = new StatusBuffer(info, network);
                break;
            default:
                return null;
        }
        network.getBuffers().add(result);
        return result;
    }
}
