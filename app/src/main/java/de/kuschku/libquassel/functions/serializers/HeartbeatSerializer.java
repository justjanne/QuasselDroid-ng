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

package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.Heartbeat;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertTrue;

public class HeartbeatSerializer implements FunctionSerializer<Heartbeat> {
    @NonNull
    private static final HeartbeatSerializer serializer = new HeartbeatSerializer();

    private HeartbeatSerializer() {
    }

    @NonNull
    public static HeartbeatSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull Heartbeat data) {
        return Arrays.asList(
                new QVariant<>(QMetaType.Type.Int, FunctionType.HEARTBEAT.id),
                new QVariant<>(QMetaType.Type.QDateTime, data.dateTime)
        );
    }

    @NonNull
    @Override
    public Heartbeat deserialize(@NonNull List packedFunc) {
        assertTrue(packedFunc.size() == 1);

        return new Heartbeat((DateTime) ((QVariant) packedFunc.remove(0)).data);
    }
}
