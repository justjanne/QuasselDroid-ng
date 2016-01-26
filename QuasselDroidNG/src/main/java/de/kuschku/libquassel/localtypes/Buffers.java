package de.kuschku.libquassel.localtypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.util.AndroidAssert;

import static de.kuschku.util.AndroidAssert.*;

public class Buffers {
    private Buffers() {

    }

    @Nullable
    public static Buffer fromType(@NonNull BufferInfo info, @NonNull Network network) {
        Buffer result;
        switch (info.type) {
            case QUERY:
                assertNotNull(info.name);
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
