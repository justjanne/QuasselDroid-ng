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

package de.kuschku.quasseldroid_ng.util.accounts;

import java.util.UUID;

import de.kuschku.util.ServerAddress;

public class Account {
    public final UUID id;

    public final String name;

    public final String host;
    public final int port;

    public final String user;
    public final String pass;

    public Account(UUID id, String name, String host, int port, String user, String pass) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public ServerAddress toAddress() {
        return new ServerAddress(host, port);
    }
}
