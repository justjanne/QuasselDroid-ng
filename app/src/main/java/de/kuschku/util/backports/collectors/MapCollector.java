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

package de.kuschku.util.backports.collectors;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.util.backports.ICollector;
import de.kuschku.util.backports.Stream;

public class MapCollector<T> implements ICollector<T, Map<T, T>> {
    @NonNull
    @Override
    public Map<T, T> collect(@NonNull Stream<T> stream) {
        Map<T, T> map = new HashMap<>(stream.count() / 2);

        if (stream.count() % 2 == 1)
            throw new IllegalArgumentException("This only works with equally many keys and values");

        List<T> list = Collectors.toList(stream);

        for (int i = 0; i < stream.count(); i += 2) {
            map.put(list.get(i), list.get(i + 1));
        }

        return map;
    }
}
