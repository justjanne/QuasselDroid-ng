package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;

import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public class SetupData {
    @NonNull
    public final String AdminUser;
    @NonNull
    public final String AdminPasswd;
    @NonNull
    public final String Backend;
    @NonNull
    public final Map<String, QVariant> ConnectionProperties;

    public SetupData(@NonNull String adminUser, @NonNull String adminPasswd, @NonNull String backend, @NonNull Map<String, QVariant> connectionProperties) {
        this.AdminUser = adminUser;
        this.AdminPasswd = adminPasswd;
        this.Backend = backend;
        this.ConnectionProperties = connectionProperties;
    }

    @NonNull
    @Override
    public String toString() {
        return "SetupData{" +
                "AdminUser='" + AdminUser + '\'' +
                ", AdminPasswd='" + AdminPasswd + '\'' +
                ", Backend='" + Backend + '\'' +
                ", ConnectionProperties=" + ConnectionProperties +
                '}';
    }
}
