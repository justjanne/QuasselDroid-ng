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
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.interfaces.QBacklogManager;

public class IBacklogManager implements Invoker<QBacklogManager> {
    @NonNull
    private static final IBacklogManager invoker = new IBacklogManager();

    private IBacklogManager() {
    }

    @NonNull
    public static IBacklogManager get() {
        return invoker;
    }

    @Override
    public void invoke(SyncFunction function, QBacklogManager obj) throws SyncInvocationException {
        switch (function.methodName) {
            case "receiveBacklog": {
                obj._receiveBacklog((int) function.params.get(0), (int) function.params.get(1), (int) function.params.get(2), (int) function.params.get(3), (int) function.params.get(4), (List<Message>) function.params.get(5));
            }
            break;
            case "receiveBacklogAll": {
                obj._receiveBacklogAll((int) function.params.get(0), (int) function.params.get(1), (int) function.params.get(2), (int) function.params.get(3), (List<Message>) function.params.get(4));
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
