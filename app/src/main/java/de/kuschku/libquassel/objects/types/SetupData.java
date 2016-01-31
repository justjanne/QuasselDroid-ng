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

package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;

import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public class SetupData {
    @NonNull
    public final String AdminUser;
    @NonNull
    public final String AdminPasswd;
    @NonNull
    public final String Backend;
    @NonNull
    public final Map<String, QVariant> ConnectionProperties;

    public SetupData(@NonNull String adminUser, @NonNull String adminPasswd, @NonNull String backend, @NonNull Map<String, QVariant> connectionProperties) {
        this.AdminUser = adminUser;
        this.AdminPasswd = adminPasswd;
        this.Backend = backend;
        this.ConnectionProperties = connectionProperties;
    }

    @NonNull
    @Override
    public String toString() {
        return "SetupData{" +
                "AdminUser='" + AdminUser + '\'' +
                ", AdminPasswd='" + AdminPasswd + '\'' +
                ", Backend='" + Backend + '\'' +
                ", ConnectionProperties=" + ConnectionProperties +
                '}';
    }
}
