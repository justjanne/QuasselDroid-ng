package de.kuschku.libquassel.objects.types;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.QVariant;

public class SessionState {
    public final List<Map<String, QVariant>> Identities;
    public final List<BufferInfo> BufferInfos;
    public final List<Integer> NetworkIds;

    public SessionState(List<Map<String, QVariant>> identities, List<BufferInfo> bufferInfos,
                        List<Integer> networkIds) {
        this.Identities = identities;
        this.BufferInfos = bufferInfos;
        this.NetworkIds = networkIds;
    }

    @Override
    public String toString() {
        return "SessionState{" +
                "Identities=" + Identities +
                ", BufferInfos=" + BufferInfos +
                ", NetworkIds=" + NetworkIds +
                '}';
    }
}
