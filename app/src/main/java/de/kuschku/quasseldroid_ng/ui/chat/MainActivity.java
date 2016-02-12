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

package de.kuschku.quasseldroid_ng.ui.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.service.ClientBackgroundThread;
import de.kuschku.quasseldroid_ng.ui.chat.fragment.ChatFragment;
import de.kuschku.quasseldroid_ng.ui.chat.fragment.LoadingFragment;
import de.kuschku.quasseldroid_ng.ui.chat.util.ActivityImplFactory;
import de.kuschku.quasseldroid_ng.ui.chat.util.ILayoutHelper;
import de.kuschku.quasseldroid_ng.ui.chat.util.Status;
import de.kuschku.quasseldroid_ng.util.BoundActivity;
import de.kuschku.quasseldroid_ng.util.accounts.AccountManager;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class MainActivity extends BoundActivity {

    /**
     * A helper to handle the different layout implementations
     */
    ILayoutHelper layoutHelper;

    /**
     * Host layout for content fragment, for example showing a loader or the chat
     */
    @Bind(R.id.content_host)
    FrameLayout contentHost;

    /**
     * Main ActionBar
     */
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    /**
     * The left material drawer of this activity, depending on layout either in the layout hierarchy
     * or at the left as pull-out menu
     */
    Drawer drawerLeft;

    /**
     * AccountHeader field for the bufferviewconfig header
     */
    AccountHeader accountHeader;

    /**
     * This object encapsulates the current status of the activity – opened bufferview, for example
     */
    Status status = new Status();

    private AccountManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        layoutHelper = ActivityImplFactory.of(getResources().getBoolean(R.bool.isTablet), this);
        accountHeader = buildAccountHeader();
        drawerLeft = layoutHelper.buildDrawer(savedInstanceState, accountHeader, toolbar);

        replaceFragment(new LoadingFragment());

        manager = new AccountManager(this);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_host, fragment);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        assertNotNull(outState);

        super.onSaveInstanceState(outState);
        status.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        assertNotNull(savedInstanceState);

        super.onRestoreInstanceState(savedInstanceState);
        status.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_hide_events:
                return true;
            case R.id.action_reauth:
                context.settings().lastAccount.set("");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AccountHeader buildAccountHeader() {
        return new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.bg1)
                .withProfileImagesVisible(false)
                .build();
    }

    public void onEventMainThread(ConnectionChangeEvent event) {
        if (event.status == ConnectionChangeEvent.Status.CONNECTED) {
            replaceFragment(new ChatFragment());
        }
    }

    public void onConnectionChange(ConnectionChangeEvent.Status status) {
        if (status == ConnectionChangeEvent.Status.CONNECTED) {
            replaceFragment(new ChatFragment());
        }
    }

    public void onEventMainThread(GeneralErrorEvent event) {

    }

    @Override
    protected void onConnectToThread(@Nullable ClientBackgroundThread thread) {
        super.onConnectToThread(thread);
        if (thread == null)
            connectToServer(manager.account(context.settings().lastAccount.get()));
    }
}
