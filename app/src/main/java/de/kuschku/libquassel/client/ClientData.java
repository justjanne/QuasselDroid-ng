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

package de.kuschku.libquassel.client;

import android.support.annotation.NonNull;

import java.util.Arrays;

public class ClientData {
    /**
     * The flags the client supports.
     */
    @NonNull
    public final FeatureFlags flags;

    /**
     * The list of protocols supported, 0x01 is Legacy and 0x02 is Datastream.
     */
    @NonNull
    public final byte[] supportedProtocols;

    /**
     * A string identifying the client.
     */
    @NonNull
    public final String identifier;

    /**
     * The protocol version of Quassel internally, for example 10.
     */
    public final int protocolVersion;

    public ClientData(@NonNull FeatureFlags flags, @NonNull byte[] supportedProtocols, @NonNull String identifier, int protocolVersion) {
        this.flags = flags;
        this.supportedProtocols = supportedProtocols;
        this.identifier = identifier;
        this.protocolVersion = protocolVersion;
    }

    @NonNull
    public byte[] getSupportedProtocols() {
        return supportedProtocols;
    }

    @NonNull
    @Override
    public String toString() {
        return "ClientData{" +
                "flags=" + flags +
                ", supportedProtocols=" + Arrays.toString(supportedProtocols) +
                ", identifier='" + identifier + '\'' +
                ", protocolVersion=" + protocolVersion +
                '}';
    }

}
