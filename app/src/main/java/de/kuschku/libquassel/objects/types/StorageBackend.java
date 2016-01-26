package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public class StorageBackend {
    @NonNull
    public final String DisplayName;
    @NonNull
    public final Map<String, QVariant> SetupDefaults;
    @NonNull
    public final String Description;
    @NonNull
    public final List<String> SetupKeys;

    public StorageBackend(@NonNull String displayName, @NonNull Map<String, QVariant> setupDefaults, @NonNull String description,
                          @NonNull List<String> setupKeys) {
        this.DisplayName = displayName;
        this.SetupDefaults = setupDefaults;
        this.Description = description;
        this.SetupKeys = setupKeys;
    }

    @NonNull
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
