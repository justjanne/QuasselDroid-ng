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

import org.joda.time.DateTime;

import de.kuschku.libquassel.exceptions.SyncInvocationException;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;

public class IIrcUser implements Invoker<QIrcUser> {
    @NonNull
    private static final IIrcUser invoker = new IIrcUser();

    private IIrcUser() {
    }

    @NonNull
    public static IIrcUser get() {
        return invoker;
    }

    @Override
    public void invoke(SyncFunction function, QIrcUser obj) throws SyncInvocationException {
        switch (function.methodName) {
            case "setAway": {
                obj._setAway((boolean) function.params.get(0));
            }
            break;
            case "setUser": {
                obj._setUser((String) function.params.get(0));
            }
            break;
            case "setHost": {
                obj._setHost((String) function.params.get(0));
            }
            break;
            case "setNick": {
                obj._setNick((String) function.params.get(0));
            }
            break;
            case "setRealName": {
                obj._setRealName((String) function.params.get(0));
            }
            break;
            case "setAccount": {
                obj._setAccount((String) function.params.get(0));
            }
            break;
            case "setAwayMessage": {
                obj._setAwayMessage((String) function.params.get(0));
            }
            break;
            case "setIdleTime": {
                obj._setIdleTime((DateTime) function.params.get(0));
            }
            break;
            case "setLoginTime": {
                obj._setLoginTime((DateTime) function.params.get(0));
            }
            break;
            case "setServer": {
                obj._setServer((String) function.params.get(0));
            }
            break;
            case "setIrcOperator": {
                obj._setIrcOperator((String) function.params.get(0));
            }
            break;
            case "setLastAwayMessage": {
                obj._setLastAwayMessage((int) function.params.get(0));
            }
            break;
            case "setWhoisServiceReply": {
                obj._setWhoisServiceReply((String) function.params.get(0));
            }
            break;
            case "setSuserHost": {
                obj._setSuserHost((String) function.params.get(0));
            }
            break;
            case "setEncrypted": {
                obj._setEncrypted((boolean) function.params.get(0));
            }
            break;
            case "updateHostmask": {
                obj._updateHostmask((String) function.params.get(0));
            }
            break;
            case "setUserModes": {
                obj._setUserModes((String) function.params.get(0));
            }
            break;
            case "joinChannel": {
                obj._joinChannel((String) function.params.get(0));
            }
            break;
            case "partChannel": {
                obj._partChannel((String) function.params.get(0));
            }
            break;
            case "addUserModes": {
                obj._addUserModes((String) function.params.get(0));
            }
            break;
            case "removeUserModes": {
                obj._removeUserModes((String) function.params.get(0));
            }
            break;
            case "quit": {
                obj._quit();
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
