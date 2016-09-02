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

package de.kuschku.libquassel;

import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.NoSubscriberEvent;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

import de.kuschku.libquassel.events.BacklogReceivedEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.events.LagChangedEvent;

public class BusProvider {
    @NonNull
    public final EventBus handle;
    @NonNull
    public final EventBus dispatch;
    @NonNull
    public final EventBus event;
    @NonNull
    private final String id;

    @NonNull
    private final BusHandler handleHandler = new BusHandler("QHANDLE");
    @NonNull
    private final BusHandler dispatchHandler = new BusHandler("QDISPATCH");
    @NonNull
    private final BusHandler eventHandler = new BusHandler("QEVENT");

    public BusProvider() {
        this.id = UUID.randomUUID().toString();
        this.handle = new EventBus();
        this.handle.register(handleHandler);
        this.dispatch = new EventBus();
        this.dispatch.register(dispatchHandler);
        this.event = new EventBus();
        this.event.register(eventHandler);
    }

    public void handle(Object o) {
        this.handle.post(o);
    }

    public void dispatch(Object o) {
        this.dispatch.post(o);
    }

    public void sendEvent(Object o) {
        this.event.post(o);
    }

    @NonNull
    @Override
    public String toString() {
        return "BusProvider{" +
                "id='" + id + '\'' +
                '}';
    }

    public static class BusHandler {
        private final String identifier;

        public BusHandler(String identifier) {
            this.identifier = identifier;
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(NoSubscriberEvent event) {
            if (!(event.originalEvent instanceof LagChangedEvent) && !(event.originalEvent instanceof BacklogReceivedEvent))
                Log.e(identifier, String.valueOf(event));
        }
    }
}
