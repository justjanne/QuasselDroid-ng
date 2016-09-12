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

import de.kuschku.libquassel.exceptions.SyncInvocationException;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;

public class IBufferViewConfig implements Invoker<QBufferViewConfig> {
    @NonNull
    private static final IBufferViewConfig invoker = new IBufferViewConfig();

    private IBufferViewConfig() {
    }

    @NonNull
    public static IBufferViewConfig get() {
        return invoker;
    }

    @Override
    public void invoke(SyncFunction function, QBufferViewConfig obj) throws SyncInvocationException {
        switch (function.methodName) {
            case "setBufferViewName": {
                obj._setBufferViewName((String) function.params.get(0));
            }
            break;
            case "setNetworkId": {
                obj._setNetworkId((int) function.params.get(0));
            }
            break;
            case "setAddNewBuffersAutomatically": {
                obj._setAddNewBuffersAutomatically((boolean) function.params.get(0));
            }
            break;
            case "setSortAlphabetically": {
                obj._setSortAlphabetically((boolean) function.params.get(0));
            }
            break;
            case "setDisableDecoration": {
                obj._setDisableDecoration((boolean) function.params.get(0));
            }
            break;
            case "setAllowedBufferTypes": {
                obj._setAllowedBufferTypes((int) function.params.get(0));
            }
            break;
            case "setMinimumActivity": {
                obj._setMinimumActivity((int) function.params.get(0));
            }
            break;
            case "setHideInactiveBuffers": {
                obj._setHideInactiveBuffers((boolean) function.params.get(0));
            }
            break;
            case "setHideInactiveNetworks": {
                obj._setHideInactiveNetworks((boolean) function.params.get(0));
            }
            break;
            case "addBuffer": {
                obj._addBuffer((int) function.params.get(0), (int) function.params.get(1));
            }
            break;
            case "moveBuffer": {
                obj._moveBuffer((int) function.params.get(0), (int) function.params.get(1));
            }
            break;
            case "removeBuffer": {
                obj._removeBuffer((int) function.params.get(0));
            }
            break;
            case "removeBufferPermanently": {
                obj._removeBufferPermanently((int) function.params.get(0));
            }
            break;
            case "update": {
                InvokerHelper.update(obj, function.params.get(0));
            }
            break;
            default: {
                throw new SyncInvocationException(function.className + "::" + function.methodName);
            }
        }
    }
}
