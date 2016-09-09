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

import java.util.Map;

import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.interfaces.QCoreInfo;

public class ICoreInfo implements Invoker<QCoreInfo> {
    @NonNull
    private static final ICoreInfo invoker = new ICoreInfo();

    private ICoreInfo() {
    }

    @NonNull
    public static ICoreInfo get() {
        return invoker;
    }

    @Override
    public void invoke(SyncFunction function, QCoreInfo obj) {
        switch (function.methodName) {
            case "setCoreData": {
                obj._setCoreData((Map<String, QVariant>) function.params.get(0));
            } break;
            case "update": {
                InvokerHelper.update(obj, function.params.get(0));
            } break;
        }
    }
}
