package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ClientLoginReject {
    @Nullable
    public final String Error;

    public ClientLoginReject(@Nullable String error) {
        Error = error;
    }

    @NonNull
    @Override
    public String toString() {
        return "ClientLoginReject{" +
                "Error='" + Error + '\'' +
                '}';
    }
}
