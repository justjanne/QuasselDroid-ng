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

package de.kuschku.libquassel.syncables.types.impl;

import android.support.annotation.NonNull;

import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.abstracts.ANetworkConfig;

public class NetworkConfig extends ANetworkConfig<NetworkConfig> {
    private boolean standardCtcp;

    private boolean autoWhoEnabled;
    private int autoWhoDelay;
    private int autoWhoNickLimit;
    private int autoWhoInterval;

    private boolean pingTimeoutEnabled;
    private int maxPingCount;
    private int pingInterval;

    public NetworkConfig(boolean standardCtcp, boolean autoWhoEnabled, int autoWhoDelay, int autoWhoNickLimit, int autoWhoInterval, boolean pingTimeoutEnabled, int maxPingCount, int pingInterval) {
        this.standardCtcp = standardCtcp;
        this.autoWhoEnabled = autoWhoEnabled;
        this.autoWhoDelay = autoWhoDelay;
        this.autoWhoNickLimit = autoWhoNickLimit;
        this.autoWhoInterval = autoWhoInterval;
        this.pingTimeoutEnabled = pingTimeoutEnabled;
        this.maxPingCount = maxPingCount;
        this.pingInterval = pingInterval;
    }

    @Override
    public boolean pingTimeoutEnabled() {
        return pingTimeoutEnabled;
    }

    @Override
    public void _setPingTimeoutEnabled(boolean pingTimeoutEnabled) {
        this.pingTimeoutEnabled = pingTimeoutEnabled;
    }

    @Override
    public void _requestSetPingTimeoutEnabled(boolean pingTimeoutEnabled) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public int pingInterval() {
        return pingInterval;
    }

    @Override
    public void _setPingInterval(int pingInterval) {
        this.pingInterval = pingInterval;
    }

    @Override
    public void _requestSetPingInterval(int pingInterval) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public int maxPingCount() {
        return maxPingCount;
    }

    @Override
    public void _setMaxPingCount(int maxPingCount) {
        this.maxPingCount = maxPingCount;
    }

    @Override
    public void _requestSetMaxPingCount(int maxPingCount) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public boolean autoWhoEnabled() {
        return autoWhoEnabled;
    }

    @Override
    public void _setAutoWhoEnabled(boolean autoWhoEnabled) {
        this.autoWhoEnabled = autoWhoEnabled;
    }

    @Override
    public void _requestSetAutoWhoEnabled(boolean autoWhoEnabled) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public int autoWhoInterval() {
        return autoWhoInterval;
    }

    @Override
    public void _setAutoWhoInterval(int autoWhoInterval) {
        this.autoWhoInterval = autoWhoInterval;
    }

    @Override
    public void _requestSetAutoWhoInterval(int autoWhoInterval) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public int autoWhoNickLimit() {
        return autoWhoNickLimit;
    }

    @Override
    public void _setAutoWhoNickLimit(int autoWhoNickLimit) {
        this.autoWhoNickLimit = autoWhoNickLimit;
    }

    @Override
    public void _requestSetAutoWhoNickLimit(int autoWhoNickLimit) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public int autoWhoDelay() {
        return autoWhoDelay;
    }

    @Override
    public void _setAutoWhoDelay(int autoWhoDelay) {
        this.autoWhoDelay = autoWhoDelay;
    }

    @Override
    public void _requestSetAutoWhoDelay(int autoWhoDelay) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public boolean standardCtcp() {
        return standardCtcp;
    }

    @Override
    public void _setStandardCtcp(boolean standardCtcp) {
        this.standardCtcp = standardCtcp;
    }

    @Override
    public void _requestSetStandardCtcp(boolean standardCtcp) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected

    }

    @Override
    public void update(Map<String, QVariant> from) {

    }

    @Override
    public void update(NetworkConfig from) {

    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        super.init(objectName, provider, client);
        client.setGlobalNetworkConfig(this);
    }
}
