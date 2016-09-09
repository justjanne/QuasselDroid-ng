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
import de.kuschku.libquassel.syncables.types.interfaces.QNetworkConfig;

public class INetworkConfig implements Invoker<QNetworkConfig> {
    @NonNull
    private static final INetworkConfig invoker = new INetworkConfig();

    private INetworkConfig() {
    }

    @NonNull
    public static INetworkConfig get() {
        return invoker;
    }

    @Override
    public void invoke(SyncFunction function, QNetworkConfig obj) {
        switch (function.methodName) {
            case "setPingTimeoutEnabled": {
                obj._setPingTimeoutEnabled((boolean) function.params.get(0));
            } break;
            case "setPingInterval": {
                obj._setPingInterval((int) function.params.get(0));
            } break;
            case "setMaxPingCount": {
                obj._setMaxPingCount((int) function.params.get(0));
            } break;
            case "setAutoWhoEnabled": {
                obj._setAutoWhoEnabled((boolean) function.params.get(0));
            } break;
            case "setAutoWhoInterval": {
                obj._setAutoWhoInterval((int) function.params.get(0));
            } break;
            case "setAutoWhoNickLimit": {
                obj._setAutoWhoNickLimit((int) function.params.get(0));
            } break;
            case "setAutoWhoDelay": {
                obj._setAutoWhoDelay((int) function.params.get(0));
            } break;
            case "setStandardCtcp": {
                obj._setStandardCtcp((boolean) function.params.get(0));
            } break;
            case "update": {
                InvokerHelper.update(obj, function.params.get(0));
            } break;
        }
    }
}
