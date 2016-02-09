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

package de.kuschku.quasseldroid_ng.ui.chat.drawer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiElementCallbackWrapper;

public class BufferItemManager implements ElementCallback<Integer>, IObservable<ElementCallback<BufferItem>> {

    private final MultiElementCallbackWrapper<BufferItem> callback = MultiElementCallbackWrapper.<BufferItem>of();

    @Nullable
    private final AppContext context;
    private final Map<Integer, BufferItem> items = new HashMap<>();

    public BufferItemManager(@Nullable AppContext context) {
        this.context = context;
        context.client().bufferManager().bufferIds().addCallback(this);
        for (Integer id : context.client().bufferManager().bufferIds()) {
            notifyItemInserted(id);
        }
    }

    public BufferItem get(int bufferId) {
        return items.get(bufferId);
    }

    @Override
    public void notifyItemInserted(Integer element) {
        if (!items.containsKey(element)) {
            BufferItem item = new BufferItem(context.client().bufferManager().buffer(element), context);
            items.put(element, item);
            callback.notifyItemInserted(item);
        }
    }

    @Override
    public void notifyItemRemoved(Integer element) {
        callback.notifyItemRemoved(items.remove(element));
    }

    @Override
    public void notifyItemChanged(Integer element) {
        callback.notifyItemChanged(items.get(element));
    }

    public void addCallback(@NonNull ElementCallback<BufferItem> callback) {
        this.callback.addCallback(callback);
    }

    public void removeCallback(@NonNull ElementCallback<BufferItem> callback) {
        this.callback.removeCallback(callback);
    }
}
