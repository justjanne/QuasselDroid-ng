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

package de.kuschku.libquassel.syncables.types.abstracts;

import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.interfaces.QNetworkConfig;

public abstract class ANetworkConfig extends SyncableObject<QNetworkConfig> implements QNetworkConfig {
    @Override
    public void setPingTimeoutEnabled(boolean pingTimeoutEnabled) {
        _setPingTimeoutEnabled(pingTimeoutEnabled);
        requestSetPingTimeoutEnabled(pingTimeoutEnabled);
    }

    @Override
    public void requestSetPingTimeoutEnabled(boolean pingTimeoutEnabled) {
        _requestSetPingTimeoutEnabled(pingTimeoutEnabled);
        syncVar("requestSetPingTimeoutEnabled", pingTimeoutEnabled);
    }

    @Override
    public void setPingInterval(int pingInterval) {
        _setPingInterval(pingInterval);
        requestSetPingInterval(pingInterval);
    }

    @Override
    public void requestSetPingInterval(int pingInterval) {
        _requestSetPingInterval(pingInterval);
        syncVar("requestSetPingInterval", pingInterval);
    }

    @Override
    public void setMaxPingCount(int maxPingCount) {
        _setMaxPingCount(maxPingCount);
        requestSetMaxPingCount(maxPingCount);
    }

    @Override
    public void requestSetMaxPingCount(int maxPingCount) {
        _requestSetMaxPingCount(maxPingCount);
        syncVar("requestSetMaxPingCount", maxPingCount);
    }

    @Override
    public void setAutoWhoEnabled(boolean autoWhoEnabled) {
        _setAutoWhoEnabled(autoWhoEnabled);
        requestSetAutoWhoEnabled(autoWhoEnabled);
    }

    @Override
    public void requestSetAutoWhoEnabled(boolean autoWhoEnabled) {
        _requestSetAutoWhoEnabled(autoWhoEnabled);
        syncVar("requestSetAutoWhoEnabled", autoWhoEnabled);
    }

    @Override
    public void setAutoWhoInterval(int autoWhoInterval) {
        _setAutoWhoInterval(autoWhoInterval);
        requestSetAutoWhoInterval(autoWhoInterval);
    }

    @Override
    public void requestSetAutoWhoInterval(int autoWhoInterval) {
        _requestSetAutoWhoInterval(autoWhoInterval);
        syncVar("requestSetAutoWhoInterval", autoWhoInterval);
    }

    @Override
    public void setAutoWhoNickLimit(int autoWhoNickLimit) {
        _setAutoWhoNickLimit(autoWhoNickLimit);
        requestSetAutoWhoNickLimit(autoWhoNickLimit);
    }

    @Override
    public void requestSetAutoWhoNickLimit(int autoWhoNickLimit) {
        _requestSetAutoWhoNickLimit(autoWhoNickLimit);
        syncVar("requestSetAutoWhoNickLimit", autoWhoNickLimit);
    }

    @Override
    public void setAutoWhoDelay(int autoWhoDelay) {
        _setAutoWhoDelay(autoWhoDelay);
        requestSetAutoWhoDelay(autoWhoDelay);
    }

    @Override
    public void requestSetAutoWhoDelay(int autoWhoDelay) {
        _requestSetAutoWhoDelay(autoWhoDelay);
        syncVar("requestSetAutoWhoDelay", autoWhoDelay);
    }

    @Override
    public void setStandardCtcp(boolean standardCtcp) {
        _setStandardCtcp(standardCtcp);
        requestSetStandardCtcp(standardCtcp);
    }

    @Override
    public void requestSetStandardCtcp(boolean standardCtcp) {
        _requestSetStandardCtcp(standardCtcp);
        syncVar("requestSetStandardCtcp", standardCtcp);
    }
}
