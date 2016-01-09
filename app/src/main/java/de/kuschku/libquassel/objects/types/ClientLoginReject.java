package de.kuschku.libquassel.objects.types;

public class ClientLoginReject {
    public final String Error;

    public ClientLoginReject(String error) {
        Error = error;
    }

    @Override
    public String toString() {
        return "ClientLoginReject{" +
                "Error='" + Error + '\'' +
                '}';
    }
}
