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

package de.kuschku.libquassel.syncables.types.interfaces;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.objects.types.Command;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.Synced;
import de.kuschku.util.observables.lists.ObservableSortedList;

public interface QAliasManager extends QObservable<QAliasManager> {
    boolean contains(final String name);

    boolean isEmpty();

    int count();

    ObservableSortedList<Alias> aliases();

    ObservableSortedList<Alias> defaults();

    // TODO: specify later on
    @NonNull
    List<Command> processInput(final BufferInfo info, final String message);

    void _update(Map<String, QVariant> from);

    void _update(QAliasManager from);

    @Synced
    void addAlias(final String name, final String expansion);

    void _addAlias(final String name, final String expansion);

    void _addAlias(Alias alias);

    void _removeAlias(Alias alias);

    Alias alias(String name);

    void requestUpdate(Map<String, QVariant<Object>> variantMap);

    void requestUpdate();

    class Alias {
        public final String name;
        public final String expansion;

        public Alias(String name, String expansion) {
            this.name = name;
            this.expansion = expansion;
        }
    }
}
