package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;

import java.util.List;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.Identity;

public class SessionState {
    @NonNull
    public final List<Identity> Identities;
    @NonNull
    public final List<BufferInfo> BufferInfos;
    @NonNull
    public final List<Integer> NetworkIds;

    public SessionState(@NonNull List<Identity> identities, @NonNull List<BufferInfo> bufferInfos,
                        @NonNull List<Integer> networkIds) {
        this.Identities = identities;
        this.BufferInfos = bufferInfos;
        this.NetworkIds = networkIds;
    }

    @NonNull
    @Override
    public String toString() {
        return "SessionState{" +
                "Identities=" + Identities +
                ", BufferInfos=" + BufferInfos +
                ", NetworkIds=" + NetworkIds +
                '}';
    }
}
