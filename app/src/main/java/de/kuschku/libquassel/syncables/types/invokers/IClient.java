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

import de.kuschku.libquassel.client.QClient;
import de.kuschku.libquassel.exceptions.SyncInvocationException;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.impl.Identity;

public class IClient {
    @NonNull
    private static final IClient invoker = new IClient();

    private IClient() {
    }

    @NonNull
    public static IClient get() {
        return invoker;
    }

    public void invoke(RpcCallFunction function, QClient obj) throws SyncInvocationException {
        switch (function.functionName) {
            case "2displayMsg(Message)": {
                obj._displayMsg((Message) function.params.get(0));
            }
            break;
            case "2bufferInfoUpdated(BufferInfo)": {
                obj._bufferInfoUpdated((BufferInfo) function.params.get(0));
            }
            break;
            case "2identityCreated(Identity)": {
                obj._identityCreated((Identity) function.params.get(0));
            }
            break;
            case "2identityRemoved(IdentityId)": {
                obj._identityRemoved((int) function.params.get(0));
            }
            break;
            case "2networkCreated(NetworkId)": {
                obj._networkCreated((int) function.params.get(0));
            }
            break;
            case "2networkRemoved(NetworkId)": {
                obj._networkRemoved((int) function.params.get(0));
            }
            break;
            case "2passwordChanged(PeerPtr,bool)": {
                obj._passwordChanged((long) function.params.get(0), (boolean) function.params.get(1));
            }
            break;
            case "2displayStatusMsg(QString,QString)": {
                obj._displayStatusMsg((String) function.params.get(0), (String) function.params.get(1));
            }
            break;
            case "__objectRenamed__": {
                obj.___objectRenamed__((String) function.params.get(0), (String) function.params.get(1), (String) function.params.get(2));
            }
            break;
            default: {
                throw new SyncInvocationException("Client::" + function.functionName);
            }
        }
    }
}
