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

package de.kuschku.quasseldroid_ng.ui.chat.util;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.chat.MainActivity;

public class LayoutHelperTabletImpl implements ILayoutHelper {
    private final MainActivity activity;

    public LayoutHelperTabletImpl(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public Drawer buildDrawer(Bundle savedInstanceState, AccountHeader accountHeader, Toolbar toolbar) {
        Drawer drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withTranslucentStatusBar(false)
                .withSavedInstance(savedInstanceState)
                .buildView();
        FrameLayout drawerHost = (FrameLayout) activity.findViewById(R.id.drawer_host);
        drawerHost.addView(drawer.getSlider());
        return drawer;
    }
}
