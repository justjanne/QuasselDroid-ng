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
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.syncables.types.impl.NetworkInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;

public class INetwork implements Invoker<QNetwork> {
    @NonNull
    private static final INetwork invoker = new INetwork();

    private INetwork() {
    }

    @NonNull
    public static INetwork get() {
        return invoker;
    }

    @Override
    public void invoke(SyncFunction function, QNetwork obj) throws SyncInvocationException {
        switch (function.methodName) {
            case "setAutoAwayActive": {
                obj._setAutoAwayActive((boolean) function.params.get(0));
            }
            break;
            case "setNetworkName": {
                obj._setNetworkName((String) function.params.get(0));
            }
            break;
            case "setCurrentServer": {
                obj._setCurrentServer((String) function.params.get(0));
            }
            break;
            case "setConnected": {
                obj._setConnected((boolean) function.params.get(0));
            }
            break;
            case "setConnectionState": {
                obj._setConnectionState((int) function.params.get(0));
            }
            break;
            case "setMyNick": {
                obj._setMyNick((String) function.params.get(0));
            }
            break;
            case "setLatency": {
                obj._setLatency((int) function.params.get(0));
            }
            break;
            case "setIdentity": {
                obj._setIdentity((int) function.params.get(0));
            }
            break;
            case "setServerList": {
                obj._setServerList((List<NetworkServer>) function.params.get(0));
            }
            break;
            case "setUseRandomServer": {
                obj._setUseRandomServer((boolean) function.params.get(0));
            }
            break;
            case "setPerform": {
                obj._setPerform((List<String>) function.params.get(0));
            }
            break;
            case "setUseAutoIdentify": {
                obj._setUseAutoIdentify((boolean) function.params.get(0));
            }
            break;
            case "setAutoIdentifyService": {
                obj._setAutoIdentifyService((String) function.params.get(0));
            }
            break;
            case "setAutoIdentifyPassword": {
                obj._setAutoIdentifyPassword((String) function.params.get(0));
            }
            break;
            case "setUseSasl": {
                obj._setUseSasl((boolean) function.params.get(0));
            }
            break;
            case "setSaslAccount": {
                obj._setSaslAccount((String) function.params.get(0));
            }
            break;
            case "setSaslPassword": {
                obj._setSaslPassword((String) function.params.get(0));
            }
            break;
            case "setUseAutoReconnect": {
                obj._setUseAutoReconnect((boolean) function.params.get(0));
            }
            break;
            case "setAutoReconnectInterval": {
                obj._setAutoReconnectInterval((int) function.params.get(0));
            }
            break;
            case "setAutoReconnectRetries": {
                obj._setAutoReconnectRetries((short) function.params.get(0));
            }
            break;
            case "setUnlimitedReconnectRetries": {
                obj._setUnlimitedReconnectRetries((boolean) function.params.get(0));
            }
            break;
            case "setRejoinChannels": {
                obj._setRejoinChannels((boolean) function.params.get(0));
            }
            break;
            case "setCodecForServer": {
                obj._setCodecForServer((String) function.params.get(0));
            }
            break;
            case "setCodecForEncoding": {
                obj._setCodecForEncoding((String) function.params.get(0));
            }
            break;
            case "setCodecForDecoding": {
                obj._setCodecForDecoding((String) function.params.get(0));
            }
            break;
            case "addSupport": {
                if (function.params.size() == 1)
                    obj._addSupport((String) function.params.get(0));
                else if (function.params.size() == 2)
                    obj._addSupport((String) function.params.get(0), (String) function.params.get(1));
            }
            break;
            case "removeSupport": {
                obj._removeSupport((String) function.params.get(0));
            }
            break;
            case "addIrcUser": {
                obj._addIrcUser((String) function.params.get(0));
            }
            break;
            case "addIrcChannel": {
                obj._addIrcChannel((String) function.params.get(0));
            }
            break;
            case "updateNickFromMask": {
                obj._updateNickFromMask((String) function.params.get(0));
            }
            break;
            case "setNetworkInfo": {
                obj._setNetworkInfo((NetworkInfo) function.params.get(0));
            }
            break;
            case "connect": {
                obj._connect();
            }
            break;
            case "disconnect": {
                obj._disconnect();
            }
            break;
            case "removeChansAndUsers": {
                obj._removeChansAndUsers();
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
