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

public class FeatureFlags {
    public final boolean supportsSSL;
    public final boolean supportsCompression;
    public final byte flags;

    public FeatureFlags(final byte flags) {
        this.flags = flags;
        this.supportsSSL = (flags & 0x01) > 0;
        this.supportsCompression = (flags & 0x02) > 0;
    }

    public FeatureFlags(final boolean supportsSSL, final boolean supportsCompression) {
        this.supportsSSL = supportsSSL;
        this.supportsCompression = supportsCompression;
        this.flags = (byte) ((this.supportsSSL ? 0x01 : 0x00) |
                (this.supportsCompression ? 0x02 : 0x00));
    }

    @NonNull
    @Override
    public String toString() {
        return "FeatureFlags{" +
                "supportsSSL=" + supportsSSL +
                ", supportsCompression=" + supportsCompression +
                '}';
    }
}
