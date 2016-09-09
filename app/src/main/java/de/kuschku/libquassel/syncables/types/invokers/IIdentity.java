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

import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;

public class IIdentity implements Invoker<QIdentity> {
    @NonNull
    private static final IIdentity invoker = new IIdentity();

    private IIdentity() {
    }

    @NonNull
    public static IIdentity get() {
        return invoker;
    }

    @Override
    public void invoke(SyncFunction function, QIdentity obj) {
        switch (function.methodName) {
            case "setId": {
                obj._setId((int) function.params.get(0));
            } break;
            case "setIdentityName": {
                obj._setIdentityName((String) function.params.get(0));
            } break;
            case "setRealName": {
                obj._setRealName((String) function.params.get(0));
            } break;
            case "setNicks": {
                obj._setNicks((List<String>) function.params.get(0));
            } break;
            case "setAwayNick": {
                obj._setAwayNick((String) function.params.get(0));
            } break;
            case "setAwayNickEnabled": {
                obj._setAwayNickEnabled((boolean) function.params.get(0));
            } break;
            case "setAwayReason": {
                obj._setAwayReason((String) function.params.get(0));
            } break;
            case "setAwayReasonEnabled": {
                obj._setAwayReasonEnabled((boolean) function.params.get(0));
            } break;
            case "setAutoAwayEnabled": {
                obj._setAutoAwayEnabled((boolean) function.params.get(0));
            } break;
            case "setAutoAwayTime": {
                obj._setAutoAwayTime((int) function.params.get(0));
            } break;
            case "setAutoAwayReason": {
                obj._setAutoAwayReason((String) function.params.get(0));
            } break;
            case "setAutoAwayReasonEnabled": {
                obj._setAutoAwayReasonEnabled((boolean) function.params.get(0));
            } break;
            case "setDetachAwayEnabled": {
                obj._setDetachAwayEnabled((boolean) function.params.get(0));
            } break;
            case "setDetachAwayReason": {
                obj._setDetachAwayReason((String) function.params.get(0));
            } break;
            case "setDetachAwayReasonEnabled": {
                obj._setDetachAwayReasonEnabled((boolean) function.params.get(0));
            } break;
            case "setIdent": {
                obj._setIdent((String) function.params.get(0));
            } break;
            case "setKickReason": {
                obj._setKickReason((String) function.params.get(0));
            } break;
            case "setPartReason": {
                obj._setPartReason((String) function.params.get(0));
            } break;
            case "setQuitReason": {
                obj._setQuitReason((String) function.params.get(0));
            } break;
            case "setSslKey": {
                obj._setSslKey((String) function.params.get(0));
            } break;
            case "setSslCert": {
                obj._setSslCert((String) function.params.get(0));
            } break;
            case "update": {
                InvokerHelper.update(obj, function.params.get(0));
            } break;
        }
    }
}
