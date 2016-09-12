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

import java.util.List;

import de.kuschku.libquassel.exceptions.SyncInvocationException;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;

public class IIrcChannel implements Invoker<QIrcChannel> {
    @NonNull
    private static final IIrcChannel invoker = new IIrcChannel();

    private IIrcChannel() {
    }

    @NonNull
    public static IIrcChannel get() {
        return invoker;
    }

    @Override
    public void invoke(SyncFunction function, QIrcChannel obj) throws SyncInvocationException {
        switch (function.methodName) {
            case "setTopic": {
                obj._setTopic((String) function.params.get(0));
            }
            break;
            case "setPassword": {
                obj._setPassword((String) function.params.get(0));
            }
            break;
            case "setEncrypted": {
                obj._setEncrypted((boolean) function.params.get(0));
            }
            break;
            case "joinIrcUsers": {
                obj._joinIrcUsers((List<String>) function.params.get(0), (List<String>) function.params.get(1));
            }
            break;
            case "part": {
                obj._part((String) function.params.get(0));
            }
            break;
            case "setUserModes": {
                obj._setUserModes((String) function.params.get(0), (String) function.params.get(1));
            }
            break;
            case "addUserMode": {
                obj._addUserMode((String) function.params.get(0), (String) function.params.get(1));
            }
            break;
            case "removeUserMode": {
                obj._removeUserMode((String) function.params.get(0), (String) function.params.get(1));
            }
            break;
            case "addChannelMode": {
                obj._addChannelMode((char) function.params.get(0), (String) function.params.get(1));
            }
            break;
            case "removeChannelMode": {
                obj._removeChannelMode((char) function.params.get(0), (String) function.params.get(1));
            }
            break;
            case "ircUserNickChanged": {
                obj._ircUserNickChanged((String) function.params.get(0), (String) function.params.get(1));
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
