package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;

public class ClientLogin {
    @NonNull
    public final String User;
    @NonNull
    public final String Password;

    public ClientLogin(@NonNull String user, @NonNull String password) {
        User = user;
        Password = password;
    }

    @NonNull
    @Override
    public String toString() {
        return "ClientLogin{" +
                "User='" + User + '\'' +
                ", Password='" + Password + '\'' +
                '}';
    }
}
