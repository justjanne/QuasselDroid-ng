package de.kuschku.libquassel.objects.types;

import java.util.List;

public class ClientInitAck {
    public final boolean Configured;
    public final boolean LoginEnabled;
    public final int CoreFeatures;
    public final List<StorageBackend> StorageBackends;

    public ClientInitAck(boolean configured, boolean loginEnabled, int coreFeatures,
                         List<StorageBackend> storageBackends) {
        Configured = configured;
        LoginEnabled = loginEnabled;
        CoreFeatures = coreFeatures;
        StorageBackends = storageBackends;
    }

    @Override
    public String toString() {
        return "ClientInitAck{" +
                "Configured=" + Configured +
                ", LoginEnabled=" + LoginEnabled +
                ", CoreFeatures=" + CoreFeatures +
                ", StorageBackends=" + StorageBackends +
                '}';
    }
}
