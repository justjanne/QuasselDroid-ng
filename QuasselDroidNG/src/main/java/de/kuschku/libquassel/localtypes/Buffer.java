package de.kuschku.libquassel.localtypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.kuschku.libquassel.primitives.types.BufferInfo;

public interface Buffer {
    @NonNull
    BufferInfo getInfo();

    @Nullable
    String getName();
}
