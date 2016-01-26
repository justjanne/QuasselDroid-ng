package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.QVariant;

public class SessionState {
    @NonNull
    public final List<Map<String, QVariant>> Identities;
    @NonNull
    public final List<BufferInfo> BufferInfos;
    @NonNull
    public final List<Integer> NetworkIds;

    public SessionState(@NonNull List<Map<String, QVariant>> identities, @NonNull List<BufferInfo> bufferInfos,
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
