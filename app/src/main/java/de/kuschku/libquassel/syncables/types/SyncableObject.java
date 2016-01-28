package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public abstract class SyncableObject<T extends SyncableObject<T>> {
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

    public void doInit() {}

    public abstract void update(T from);
    public abstract void update(Map<String, QVariant> from);
}
