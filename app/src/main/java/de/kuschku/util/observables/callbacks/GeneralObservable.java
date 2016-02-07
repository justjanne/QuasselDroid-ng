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

package de.kuschku.util.observables.callbacks;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.libquassel.syncables.types.interfaces.QObservable;

public class GeneralObservable implements QObservable, GeneralCallback {
    Set<GeneralCallback> callbackSet = new HashSet<>();

    @Override
    public void addObserver(GeneralCallback o) {
        callbackSet.add(o);
    }

    @Override
    public void deleteObserver(GeneralCallback o) {
        callbackSet.remove(o);
    }


    @Override
    public void notifyChanged() {
        for (GeneralCallback callback : callbackSet) {
            callback.notifyChanged();
        }
    }
}
