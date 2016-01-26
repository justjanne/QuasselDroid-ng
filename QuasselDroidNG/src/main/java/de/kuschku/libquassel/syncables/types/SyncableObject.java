package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;

import static de.kuschku.util.AndroidAssert.*;

public abstract class SyncableObject {
    @Nullable
    protected BusProvider provider;
    @Nullable
    private String objectName;

    protected void sync(@NonNull String methodName, @NonNull Object[] params) {
        assertNotNull(provider);

        provider.dispatch(new SyncFunction<>(getClassName(), getObjectName(), methodName, Arrays.asList(params)));
    }

    public void setBusProvider(@NonNull BusProvider provider) {
        this.provider = provider;
    }

    @NonNull
    public String getClassName() {
        return getClass().getSimpleName();
    }

    @Nullable
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(@Nullable String objectName) {
        this.objectName = objectName;
    }

    public void renameObject(@Nullable String objectName) {
        setObjectName(objectName);
    }

    public abstract void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client);
}
