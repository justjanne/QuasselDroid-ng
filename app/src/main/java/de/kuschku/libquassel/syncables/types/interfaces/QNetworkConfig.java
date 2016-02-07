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

package de.kuschku.libquassel.syncables.types.interfaces;

import de.kuschku.libquassel.syncables.Synced;

public interface QNetworkConfig extends QObservable {
    boolean pingTimeoutEnabled();

    @Synced
    void setPingTimeoutEnabled(boolean pingTimeoutEnabled);

    void _setPingTimeoutEnabled(boolean pingTimeoutEnabled);

    @Synced
    void requestSetPingTimeoutEnabled(boolean pingTimeoutEnabled);

    void _requestSetPingTimeoutEnabled(boolean pingTimeoutEnabled);

    int pingInterval();

    @Synced
    void setPingInterval(int pingInterval);

    void _setPingInterval(int pingInterval);

    @Synced
    void requestSetPingInterval(int pingInterval);

    void _requestSetPingInterval(int pingInterval);

    int maxPingCount();

    @Synced
    void setMaxPingCount(int maxPingCount);

    void _setMaxPingCount(int maxPingCount);

    @Synced
    void requestSetMaxPingCount(int maxPingCount);

    void _requestSetMaxPingCount(int maxPingCount);

    boolean autoWhoEnabled();

    @Synced
    void setAutoWhoEnabled(boolean autoWhoEnabled);

    void _setAutoWhoEnabled(boolean autoWhoEnabled);

    @Synced
    void requestSetAutoWhoEnabled(boolean autoWhoEnabled);

    void _requestSetAutoWhoEnabled(boolean autoWhoEnabled);

    int autoWhoInterval();

    @Synced
    void setAutoWhoInterval(int autoWhoInterval);

    void _setAutoWhoInterval(int autoWhoInterval);

    @Synced
    void requestSetAutoWhoInterval(int autoWhoInterval);

    void _requestSetAutoWhoInterval(int autoWhoInterval);

    int autoWhoNickLimit();

    @Synced
    void setAutoWhoNickLimit(int autoWhoNickLimit);

    void _setAutoWhoNickLimit(int autoWhoNickLimit);

    @Synced
    void requestSetAutoWhoNickLimit(int autoWhoNickLimit);

    void _requestSetAutoWhoNickLimit(int autoWhoNickLimit);

    int autoWhoDelay();

    @Synced
    void setAutoWhoDelay(int autoWhoDelay);

    void _setAutoWhoDelay(int autoWhoDelay);

    @Synced
    void requestSetAutoWhoDelay(int autoWhoDelay);

    void _requestSetAutoWhoDelay(int autoWhoDelay);

    boolean standardCtcp();

    @Synced
    void setStandardCtcp(boolean standardCtcp);

    void _setStandardCtcp(boolean standardCtcp);

    @Synced
    void requestSetStandardCtcp(boolean standardCtcp);

    void _requestSetStandardCtcp(boolean standardCtcp);
}
