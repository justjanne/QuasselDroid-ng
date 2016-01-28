package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;

import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.BufferSyncerSerializer;
import de.kuschku.libquassel.syncables.serializers.NetworkConfigSerializer;

public class NetworkConfig extends SyncableObject<NetworkConfig> {
    private int autoWhoNickLimit;
    private int autoWhoDelay;
    private boolean autoWhoEnabled;
    private boolean standardCtcp;
    private int pingInterval;
    private int autoWhoInterval;
    private int maxPingCount;
    private boolean pingTimeoutEnabled;

    public NetworkConfig(int autoWhoNickLimit, int autoWhoDelay, boolean autoWhoEnabled, boolean standardCtcp, int pingInterval, int autoWhoInterval, int maxPingCount, boolean pingTimeoutEnabled) {
        this.autoWhoNickLimit = autoWhoNickLimit;
        this.autoWhoDelay = autoWhoDelay;
        this.autoWhoEnabled = autoWhoEnabled;
        this.standardCtcp = standardCtcp;
        this.pingInterval = pingInterval;
        this.autoWhoInterval = autoWhoInterval;
        this.maxPingCount = maxPingCount;
        this.pingTimeoutEnabled = pingTimeoutEnabled;
    }

    public int getAutoWhoNickLimit() {
        return autoWhoNickLimit;
    }

    public void setAutoWhoNickLimit(int autoWhoNickLimit) {
        this.autoWhoNickLimit = autoWhoNickLimit;
    }

    public int getAutoWhoDelay() {
        return autoWhoDelay;
    }

    public void setAutoWhoDelay(int autoWhoDelay) {
        this.autoWhoDelay = autoWhoDelay;
    }

    public boolean isAutoWhoEnabled() {
        return autoWhoEnabled;
    }

    public void setAutoWhoEnabled(boolean autoWhoEnabled) {
        this.autoWhoEnabled = autoWhoEnabled;
    }

    public boolean isStandardCtcp() {
        return standardCtcp;
    }

    public void setStandardCtcp(boolean standardCtcp) {
        this.standardCtcp = standardCtcp;
    }

    public int getPingInterval() {
        return pingInterval;
    }

    public void setPingInterval(int pingInterval) {
        this.pingInterval = pingInterval;
    }

    public int getAutoWhoInterval() {
        return autoWhoInterval;
    }

    public void setAutoWhoInterval(int autoWhoInterval) {
        this.autoWhoInterval = autoWhoInterval;
    }

    public int getMaxPingCount() {
        return maxPingCount;
    }

    public void setMaxPingCount(int maxPingCount) {
        this.maxPingCount = maxPingCount;
    }

    public boolean isPingTimeoutEnabled() {
        return pingTimeoutEnabled;
    }

    public void setPingTimeoutEnabled(boolean pingTimeoutEnabled) {
        this.pingTimeoutEnabled = pingTimeoutEnabled;
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {

    }

    @Override
    public void update(NetworkConfig from) {
        this.autoWhoNickLimit = from.autoWhoNickLimit;
        this.autoWhoDelay = from.autoWhoDelay;
        this.autoWhoEnabled = from.autoWhoEnabled;
        this.standardCtcp = from.standardCtcp;
        this.pingInterval = from.pingInterval;
        this.autoWhoInterval = from.autoWhoInterval;
        this.maxPingCount = from.maxPingCount;
        this.pingTimeoutEnabled = from.pingTimeoutEnabled;
    }

    @Override
    public void update(Map<String, QVariant> from) {
        update(NetworkConfigSerializer.get().fromDatastream(from));
    }
}
