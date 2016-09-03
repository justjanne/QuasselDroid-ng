/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.objects.types;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.primitives.types.QVariant;

public class CoreStatus {
    public final boolean Configured;
    public final boolean LoginEnabled;
    public final int CoreFeatures;
    @Nullable
    public final List<StorageBackend> StorageBackends;

    public CoreStatus(boolean configured, boolean loginEnabled, int coreFeatures,
                      @Nullable List<StorageBackend> storageBackends) {
        Configured = configured;
        LoginEnabled = loginEnabled;
        CoreFeatures = coreFeatures;
        StorageBackends = storageBackends;
    }

    @NonNull
    @Override
    public String toString() {
        return "ClientInitAck{" +
                "Configured=" + Configured +
                ", LoginEnabled=" + LoginEnabled +
                ", CoreFeatures=" + CoreFeatures +
                ", StorageBackends=" + StorageBackends +
                '}';
    }

    public ArrayList<Bundle> getStorageBackendsAsBundle() {
        if (StorageBackends == null)
            return null;

        ArrayList<Bundle> backends = new ArrayList<>(StorageBackends.size());
        for (StorageBackend backend : StorageBackends) {
            Bundle bundle = new Bundle();
            bundle.putString("displayName", backend.DisplayName);
            bundle.putString("description", backend.Description);
            Bundle defaults = new Bundle();
            Bundle types = new Bundle();
            for (String key : backend.SetupKeys) {
                QVariant value = backend.SetupDefaults.get(key);
                if (value != null) {
                    switch (value.type.type) {
                        case Int:
                        case UInt: {
                            defaults.putInt(key, (int) value.data);
                            types.putString(key, "int");
                        }
                        break;
                        case Short:
                        case UShort: {
                            defaults.putShort(key, (short) value.data);
                            types.putString(key, "short");
                        }
                        break;
                        case Long:
                        case ULong: {
                            defaults.putLong(key, (long) value.data);
                            types.putString(key, "long");
                        }
                        break;
                        case Bool: {
                            defaults.putBoolean(key, (boolean) value.data);
                            types.putString(key, "boolean");
                        }
                        break;
                        case Double: {
                            bundle.putDouble(key, (double) value.data);
                            types.putString(key, "double");
                        }
                        break;
                        case Float: {
                            defaults.putDouble(key, (float) value.data);
                            types.putString(key, "float");
                        }
                        break;
                        case QString: {
                            defaults.putString(key, (String) value.data);
                            types.putString(key, "string");
                        }
                        break;
                        default: {
                            Log.w("CoreSetup", "Found configuration element with incompatible type: " + key + " : " + value.type.type);
                        }
                        break;
                    }
                } else {
                    defaults.putString(key, "");
                    types.putString(key, "string");
                }
            }
            bundle.putBundle("defaults", defaults);
            bundle.putBundle("types", types);
            backends.add(bundle);
        }
        return backends;
    }
}
