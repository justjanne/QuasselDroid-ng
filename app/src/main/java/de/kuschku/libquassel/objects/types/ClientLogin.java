package de.kuschku.libquassel.objects.types;

public class ClientLogin {
    public final String User;
    public final String Password;

    public ClientLogin(String user, String password) {
        User = user;
        Password = password;
    }

    @Override
    public String toString() {
        return "ClientLogin{" +
                "User='" + User + '\'' +
                ", Password='" + Password + '\'' +
                '}';
    }
}
