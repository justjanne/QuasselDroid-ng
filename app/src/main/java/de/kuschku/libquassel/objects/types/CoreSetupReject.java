package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CoreSetupReject {
    @Nullable
    public final String Error;

    public CoreSetupReject(@Nullable String error) {
        Error = error;
    }

    @NonNull
    @Override
    public String toString() {
        return "CoreSetupReject{" +
                "Error='" + Error + '\'' +
                '}';
    }
}
