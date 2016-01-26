package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ClientInitReject {
    @Nullable
    public final String Error;

    public ClientInitReject(@Nullable String error) {
        Error = error;
    }

    @NonNull
    @Override
    public String toString() {
        return "ClientInitReject{" +
                "Error='" + Error + '\'' +
                '}';
    }
}
