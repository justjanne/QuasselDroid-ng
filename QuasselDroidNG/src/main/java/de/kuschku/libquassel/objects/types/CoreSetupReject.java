package de.kuschku.libquassel.objects.types;

public class CoreSetupReject {
    public final String Error;

    public CoreSetupReject(String error) {
        Error = error;
    }

    @Override
    public String toString() {
        return "CoreSetupReject{" +
                "Error='" + Error + '\'' +
                '}';
    }
}
