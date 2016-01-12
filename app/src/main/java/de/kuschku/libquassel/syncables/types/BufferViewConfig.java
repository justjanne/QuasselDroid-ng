package de.kuschku.libquassel.syncables.types;

import java.util.List;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.quasseldroid_ng.BufferViewManagerChangedEvent;

public class BufferViewConfig extends SyncableObject {
    String bufferViewName;
    List<Integer> TemporarilyRemovedBuffers;
    boolean hideInactiveNetworks;
    List<Integer> BufferList;
    int allowedBufferTypes;
    boolean sortAlphabetically;
    boolean disableDecoration;
    boolean addNewBuffersAutomatically;
    int networkId;
    int minimumActivity;
    boolean hideInactiveBuffers;
    List<Integer> RemovedBuffers;

    public BufferViewConfig(String bufferViewName, List<Integer> temporarilyRemovedBuffers, boolean hideInactiveNetworks, List<Integer> bufferList, int allowedBufferTypes, boolean sortAlphabetically, boolean disableDecoration, boolean addNewBuffersAutomatically, int networkId, int minimumActivity, boolean hideInactiveBuffers, List<Integer> removedBuffers) {
        this.bufferViewName = bufferViewName;
        TemporarilyRemovedBuffers = temporarilyRemovedBuffers;
        this.hideInactiveNetworks = hideInactiveNetworks;
        BufferList = bufferList;
        this.allowedBufferTypes = allowedBufferTypes;
        this.sortAlphabetically = sortAlphabetically;
        this.disableDecoration = disableDecoration;
        this.addNewBuffersAutomatically = addNewBuffersAutomatically;
        this.networkId = networkId;
        this.minimumActivity = minimumActivity;
        this.hideInactiveBuffers = hideInactiveBuffers;
        RemovedBuffers = removedBuffers;
    }

    public String getBufferViewName() {
        return bufferViewName;
    }

    public void setBufferViewName(String bufferViewName) {
        this.bufferViewName = bufferViewName;
    }

    public List<Integer> getTemporarilyRemovedBuffers() {
        return TemporarilyRemovedBuffers;
    }

    public void setTemporarilyRemovedBuffers(List<Integer> temporarilyRemovedBuffers) {
        TemporarilyRemovedBuffers = temporarilyRemovedBuffers;
    }

    public boolean isHideInactiveNetworks() {
        return hideInactiveNetworks;
    }

    public void setHideInactiveNetworks(boolean hideInactiveNetworks) {
        this.hideInactiveNetworks = hideInactiveNetworks;
    }

    public List<Integer> getBufferList() {
        return BufferList;
    }

    public void setBufferList(List<Integer> bufferList) {
        BufferList = bufferList;
    }

    public int getAllowedBufferTypes() {
        return allowedBufferTypes;
    }

    public void setAllowedBufferTypes(int allowedBufferTypes) {
        this.allowedBufferTypes = allowedBufferTypes;
    }

    public boolean isSortAlphabetically() {
        return sortAlphabetically;
    }

    public void setSortAlphabetically(boolean sortAlphabetically) {
        this.sortAlphabetically = sortAlphabetically;
    }

    public boolean isDisableDecoration() {
        return disableDecoration;
    }

    public void setDisableDecoration(boolean disableDecoration) {
        this.disableDecoration = disableDecoration;
    }

    public boolean isAddNewBuffersAutomatically() {
        return addNewBuffersAutomatically;
    }

    public void setAddNewBuffersAutomatically(boolean addNewBuffersAutomatically) {
        this.addNewBuffersAutomatically = addNewBuffersAutomatically;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public int getMinimumActivity() {
        return minimumActivity;
    }

    public void setMinimumActivity(int minimumActivity) {
        this.minimumActivity = minimumActivity;
    }

    public boolean isHideInactiveBuffers() {
        return hideInactiveBuffers;
    }

    public void setHideInactiveBuffers(boolean hideInactiveBuffers) {
        this.hideInactiveBuffers = hideInactiveBuffers;
    }

    public List<Integer> getRemovedBuffers() {
        return RemovedBuffers;
    }

    public void setRemovedBuffers(List<Integer> removedBuffers) {
        RemovedBuffers = removedBuffers;
    }

    @Override
    public String toString() {
        return "BufferViewConfig{" +
                "bufferViewName='" + bufferViewName + '\'' +
                ", TemporarilyRemovedBuffers=" + TemporarilyRemovedBuffers +
                ", hideInactiveNetworks=" + hideInactiveNetworks +
                ", BufferList=" + BufferList +
                ", allowedBufferTypes=" + allowedBufferTypes +
                ", sortAlphabetically=" + sortAlphabetically +
                ", disableDecoration=" + disableDecoration +
                ", addNewBuffersAutomatically=" + addNewBuffersAutomatically +
                ", networkId=" + networkId +
                ", minimumActivity=" + minimumActivity +
                ", hideInactiveBuffers=" + hideInactiveBuffers +
                ", RemovedBuffers=" + RemovedBuffers +
                '}';
    }

    @Override
    public void init(InitDataFunction function, BusProvider provider, Client client) {
        setObjectName(function.objectName);
        client.getBufferViewManager().BufferViews.put(Integer.valueOf(function.objectName), this);
        provider.sendEvent(new BufferViewManagerChangedEvent(Integer.valueOf(function.objectName), BufferViewManagerChangedEvent.Action.ADD));
    }

    public void addBuffer(int bufferId, int position) {
        BufferList.add(position, bufferId);
    }

    public void SYNC_addBuffer(int bufferId, int position) {
        addBuffer(bufferId, position);
        sync("addBuffer", new Object[] {bufferId, position});
    }
}
