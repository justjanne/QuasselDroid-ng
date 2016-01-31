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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.events;

import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class ConnectionChangeEvent {
    @NonNull
    public final Status status;
    @NonNull
    public final String reason;

    public ConnectionChangeEvent(@NonNull Status status) {
        this(status, "");
    }

    public ConnectionChangeEvent(@NonNull Status status, @NonNull String reason) {
        this.status = status;
        this.reason = reason;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConnectionChangeEvent{" +
                "status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }

    public enum Status {
        CONNECTING,
        HANDSHAKE,
        CORE_SETUP_REQUIRED,
        LOGIN_REQUIRED,
        USER_SETUP_REQUIRED,
        INITIALIZING_DATA,
        LOADING_BACKLOG,
        CONNECTED,
        DISCONNECTED
    }
}
