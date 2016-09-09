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

package de.kuschku.libquassel.syncables.types.invokers;

import android.support.annotation.NonNull;

import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferSyncer;

public class IBufferSyncer implements Invoker<QBufferSyncer> {
    @NonNull
    private static final IBufferSyncer invoker = new IBufferSyncer();

    private IBufferSyncer() {
    }

    @NonNull
    public static IBufferSyncer get() {
        return invoker;
    }

    @Override
    public void invoke(SyncFunction function, QBufferSyncer obj) {
        switch (function.methodName) {
            case "setLastSeenMsg": {
                obj._setLastSeenMsg((int) function.params.get(0), (int) function.params.get(1));
            } break;
            case "setMarkerLine": {
                obj._setMarkerLine((int) function.params.get(0), (int) function.params.get(1));
            } break;
            case "removeBuffer": {
                obj._removeBuffer((int) function.params.get(0));
            } break;
            case "renameBuffer": {
                obj._renameBuffer((int) function.params.get(0), (String) function.params.get(1));
            } break;
            case "mergeBuffersPermanently": {
                obj._mergeBuffersPermanently((int) function.params.get(0), (int) function.params.get(1));
            } break;
            case "markBufferAsRead": {
                obj._markBufferAsRead((int) function.params.get(0));
            } break;
            case "update": {
                InvokerHelper.update(obj, function.params.get(0));
            } break;
        }
    }
}
