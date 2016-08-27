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

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialcab.MaterialCab;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.quasseldroid_ng.R;

public class ActionModeHandler implements MaterialCab.Callback {
    private final AppCompatActivity activity;
    private final MaterialCab cab;
    private Set<Buffer> selectedChildren = new HashSet<>();
    private Set<NetworkItem> selectedParents = new HashSet<>();
    private Menu actionModeMenu;

    public ActionModeHandler(AppCompatActivity activity, @IdRes int stub) {
        this.activity = activity;
        cab = new MaterialCab(activity, stub);
    }

    public boolean isActive() {
        return cab.isActive();
    }


    public void start() {
        //cab.start(this);
    }

    public void toggle(Buffer buffer) {
        /*
        if (selectedChildren.contains(buffer))
            selectedChildren.remove(buffer);
        else
            selectedChildren.add(buffer);

        updateSelectionType();
        */
    }

    public void toggle(NetworkItem item) {
        /*
        if (selectedParents.contains(item))
            selectedParents.remove(item);
        else
            selectedParents.add(item);

        updateSelectionType();
        */
    }

    public boolean isChecked(Buffer buffer) {
        return selectedChildren.contains(buffer);
    }

    public boolean isChecked(NetworkItem network) {
        return selectedParents.contains(network);
    }

    private void updateSelectionType() {
        if (actionModeMenu != null) {
            actionModeMenu.setGroupVisible(R.id.context_group_buffer, !selectedChildren.isEmpty() && selectedParents.isEmpty());
            actionModeMenu.setGroupVisible(R.id.context_group_network, !selectedParents.isEmpty() && selectedChildren.isEmpty());
        }
    }

    @Override
    public boolean onCabCreated(MaterialCab cab, Menu menu) {
        menu.clear();
        activity.getMenuInflater().inflate(R.menu.context_buffer_network, menu);
        actionModeMenu = menu;
        updateSelectionType();
        return true;
    }

    @Override
    public boolean onCabItemClicked(MenuItem item) {
        return false;
    }

    @Override
    public boolean onCabFinished(MaterialCab cab) {
        actionModeMenu = null;
        selectedChildren.clear();
        selectedParents.clear();
        return true;
    }
}
