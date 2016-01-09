package de.kuschku.libquassel.objects.types;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public class StorageBackend {
    public final String DisplayName;
    public final Map<String, QVariant> SetupDefaults;
    public final String Description;
    public final List<String> SetupKeys;

    public StorageBackend(String displayName, Map<String, QVariant> setupDefaults, String description,
                          List<String> setupKeys) {
        this.DisplayName = displayName;
        this.SetupDefaults = setupDefaults;
        this.Description = description;
        this.SetupKeys = setupKeys;
    }

    @Override
    public String toString() {
        return "StorageBackend{" +
                "DisplayName='" + DisplayName + '\'' +
                ", SetupDefaults=" + SetupDefaults +
                ", Description='" + Description + '\'' +
                ", SetupKeys=" + SetupKeys +
                '}';
    }
}
