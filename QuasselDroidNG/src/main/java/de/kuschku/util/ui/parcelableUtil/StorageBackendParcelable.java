package de.kuschku.util.ui.parcelableUtil;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.objects.types.StorageBackend;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.util.backports.Stream;

import static de.kuschku.util.AndroidAssert.*;

public class StorageBackendParcelable extends StorageBackend implements Parcelable {
    @NonNull
    public static Creator<StorageBackendParcelable> CREATOR = new Creator<StorageBackendParcelable>() {
        @NonNull
        @Override
        public StorageBackendParcelable createFromParcel(@NonNull Parcel source) {
            String DisplayName = source.readString();
            String Description = source.readString();
            List<String> SetupKeys = new ArrayList<>();
            source.readStringList(SetupKeys);
            Map<String, QVariant> SetupDefaults = new HashMap<>();
            int size = source.readInt();
            for (int i = 0; i < size; i++) {
                String key = source.readString();
                try {
                    QMetaType.Type type = QMetaType.Type.fromId(source.readInt());
                    switch (type) {
                        case Int:
                            SetupDefaults.put(key, new QVariant<>(type, source.readInt()));
                            break;
                        case QString:
                            SetupDefaults.put(key, new QVariant<>(type, source.readString()));
                            break;
                        case Bool:
                            SetupDefaults.put(key, new QVariant<>(type, source.readInt() > 0));
                            break;
                        default:
                            throw new IllegalArgumentException("Can’t serialize type "+ type.name());
                    }
                } catch (IOException e) {
                }
            }

            assertNotNull(DisplayName);
            assertNotNull(Description);

            return new StorageBackendParcelable(DisplayName, SetupDefaults, Description, SetupKeys);
        }

        @NonNull
        @Override
        public StorageBackendParcelable[] newArray(int size) {
            return new StorageBackendParcelable[size];
        }
    };

    public StorageBackendParcelable(@NonNull StorageBackend backend) {
        this(backend.DisplayName, backend.SetupDefaults, backend.Description, backend.SetupKeys);
    }

    public StorageBackendParcelable(@NonNull String displayName, @NonNull Map<String, QVariant> setupDefaults, @NonNull String description, @NonNull List<String> setupKeys) {
        super(displayName, setupDefaults, description, setupKeys);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(DisplayName);
        dest.writeString(Description);
        dest.writeStringList(SetupKeys);
        dest.writeInt(SetupDefaults.size());
        for (String key : SetupDefaults.keySet()) {
            QVariant q = SetupDefaults.get(key);
            QMetaType.Type type = q.type.type;
            dest.writeString(key);
            dest.writeInt(type.getValue());
            switch (type) {
                case Int:
                    dest.writeInt((Integer) q.data);
                    break;
                case QString:
                    dest.writeString((String) q.data);
                    break;
                case Bool:
                    dest.writeInt(((Boolean) q.data) ? 1 : 0);
                    break;
                default:
                    throw new IllegalArgumentException("Can’t serialize type "+q.type.name);
            }
        }
    }

    @NonNull
    public static ArrayList<StorageBackendParcelable> wrap(@NonNull List<StorageBackend> backends) {
        return new ArrayList<>(new Stream<>(backends).map(StorageBackendParcelable::new).list());
    }
}
