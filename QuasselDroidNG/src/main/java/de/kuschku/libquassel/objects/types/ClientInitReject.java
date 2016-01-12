package de.kuschku.libquassel.objects.types;

public class ClientInitReject {
    public final String Error;

    public ClientInitReject(String error) {
        Error = error;
    }

    @Override
    public String toString() {
        return "ClientInitReject{" +
                "Error='" + Error + '\'' +
                '}';
    }
}
