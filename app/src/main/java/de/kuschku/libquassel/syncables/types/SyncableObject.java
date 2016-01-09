package de.kuschku.libquassel.syncables.types;

import java.util.Arrays;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;

public abstract class SyncableObject {
    protected BusProvider provider;
    private String objectName;

    public void sync(String methodName, Object[] params) {
        provider.dispatch(new SyncFunction<>(getClassName(), getObjectName(), methodName, Arrays.asList(params)));
    }

    public void setBusProvider(BusProvider provider) {
        this.provider = provider;
    }

    public String getClassName() {
        return getClass().getSimpleName();
    }

    public String getObjectName() {
        return objectName;
    }

    public void renameObject(String objectName) {
        setObjectName(objectName);
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public abstract void init(InitDataFunction function, BusProvider provider, Client client);
}
