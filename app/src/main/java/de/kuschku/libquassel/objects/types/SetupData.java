package de.kuschku.libquassel.objects.types;

import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public class SetupData {
    public final String AdminUser;
    public final String AdminPasswd;
    public final String Backend;
    public final Map<String, QVariant> ConnectionProperties;

    public SetupData(String adminUser, String adminPasswd, String backend, Map<String, QVariant> connectionProperties) {
        this.AdminUser = adminUser;
        this.AdminPasswd = adminPasswd;
        this.Backend = backend;
        this.ConnectionProperties = connectionProperties;
    }

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
