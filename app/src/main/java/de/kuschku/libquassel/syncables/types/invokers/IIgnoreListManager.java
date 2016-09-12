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
import de.kuschku.libquassel.syncables.types.interfaces.QIgnoreListManager;

public class IIgnoreListManager implements Invoker<QIgnoreListManager> {
    @NonNull
    private static final IIgnoreListManager invoker = new IIgnoreListManager();

    private IIgnoreListManager() {
    }

    @NonNull
    public static IIgnoreListManager get() {
        return invoker;
    }

    public void invoke(SyncFunction function, QIgnoreListManager object) throws SyncInvocationException {
        switch (function.methodName) {
            case "removeIgnoreListItem": {
                object._removeIgnoreListItem((String) function.params.get(0));
            }
            break;
            case "toggleIgnoreRule": {
                object._toggleIgnoreRule((String) function.params.get(0));
            }
            break;
            case "addIgnoreListItem": {
                object._addIgnoreListItem((int) function.params.get(0), (String) function.params.get(1), (boolean) function.params.get(2), (int) function.params.get(3), (int) function.params.get(4), (String) function.params.get(5), (boolean) function.params.get(6));
            }
            break;
            case "update": {
                InvokerHelper.update(object, function.params.get(0));
            }
            break;
            default: {
                throw new SyncInvocationException(function.className + "::" + function.methodName);
            }
        }
    }
}
