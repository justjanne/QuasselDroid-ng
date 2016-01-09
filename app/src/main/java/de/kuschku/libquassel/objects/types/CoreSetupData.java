package de.kuschku.libquassel.objects.types;

public class CoreSetupData {
    public final SetupData SetupData;

    public CoreSetupData(de.kuschku.libquassel.objects.types.SetupData setupData) {
        SetupData = setupData;
    }

    @Override
    public String toString() {
        return "CoreSetupData{" +
                "SetupData=" + SetupData +
                '}';
    }
}
