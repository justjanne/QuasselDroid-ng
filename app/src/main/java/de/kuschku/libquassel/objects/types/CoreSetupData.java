package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;

public class CoreSetupData {
    @NonNull
    public final SetupData SetupData;

    public CoreSetupData(@NonNull SetupData setupData) {
        SetupData = setupData;
    }

    @NonNull
    @Override
    public String toString() {
        return "CoreSetupData{" +
                "SetupData=" + SetupData +
                '}';
    }
}
