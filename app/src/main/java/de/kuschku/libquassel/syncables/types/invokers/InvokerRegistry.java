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

import de.kuschku.libquassel.functions.types.SyncFunction;

public class InvokerRegistry {
    private InvokerRegistry() {

    }

    public static <T> Invoker<T> getInvoker(SyncFunction function) {
        switch (function.className) {
            case "AliasManager":
                return (Invoker<T>) IAliasManager.get();
            case "BufferSyncer":
                return (Invoker<T>) IBufferSyncer.get();
            case "BufferViewManager":
                return (Invoker<T>) IBufferViewManager.get();
            case "Identity":
                return (Invoker<T>) IIdentity.get();
            case "IrcChannel":
                return (Invoker<T>) IIrcChannel.get();
            case "Network":
                return (Invoker<T>) INetwork.get();
            case "BacklogManager":
                return (Invoker<T>) IBacklogManager.get();
            case "BufferViewConfig":
                return (Invoker<T>) IBufferViewConfig.get();
            case "CoreInfo":
                return (Invoker<T>) ICoreInfo.get();
            case "IgnoreListManager":
                return (Invoker<T>) IIgnoreListManager.get();
            case "IrcUser":
                return (Invoker<T>) IIrcUser.get();
            case "NetworkConfig":
                return (Invoker<T>) INetworkConfig.get();
            default:
                return null;
        }
    }

    public static void invoke(SyncFunction function, Object obj) {
        Invoker invoker = getInvoker(function);
        if (invoker != null)
            invoker.invoke(function, obj);
    }
}
