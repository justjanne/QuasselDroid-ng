package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public class ClientInitAck {
    public final boolean Configured;
    public final boolean LoginEnabled;
    public final int CoreFeatures;
    @Nullable
    public final List<StorageBackend> StorageBackends;

    public ClientInitAck(boolean configured, boolean loginEnabled, int coreFeatures,
                         @Nullable List<StorageBackend> storageBackends) {
        Configured = configured;
        LoginEnabled = loginEnabled;
        CoreFeatures = coreFeatures;
        StorageBackends = storageBackends;
    }

    @NonNull
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
