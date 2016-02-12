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
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.localtypes.BacklogFilter;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewManager;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.service.ClientBackgroundThread;
import de.kuschku.quasseldroid_ng.ui.chat.drawer.BufferItem;
import de.kuschku.quasseldroid_ng.ui.chat.drawer.BufferViewConfigItem;
import de.kuschku.quasseldroid_ng.ui.chat.drawer.NetworkItem;
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
    BufferViewConfigItem currentConfig;
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
        drawerLeft.setOnDrawerItemClickListener((view, position, drawerItem) -> {
            if (drawerItem instanceof NetworkItem) {
                drawerLeft.getAdapter().toggleExpandable(position);
                return true;
            } else if (drawerItem instanceof BufferItem) {
                int id = ((BufferItem) drawerItem).getBuffer().getInfo().id();
                context.client().backlogManager().open(id);
                return false;
            }
            return true;
        });

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
                displayFilterDialog();
                return true;
            case R.id.action_reauth:
                context.settings().lastAccount.set("");
                stopConnection();
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
                .withOnAccountHeaderListener((view, profile, current) -> {
                    selectBufferViewConfig((int) profile.getIdentifier());
                    return true;
                })
                .build();
    }

    public void onEventMainThread(ConnectionChangeEvent event) {
        onConnectionChange(event.status);
    }

    public void onConnectionChange(ConnectionChangeEvent.Status status) {
        if (status == ConnectionChangeEvent.Status.CONNECTED) {
            replaceFragment(new ChatFragment());
            updateBufferViewConfigs();
        }
    }

    public void onEventMainThread(GeneralErrorEvent event) {

    }

    private void selectBufferViewConfig(@IntRange(from = -1) int bufferViewConfigId) {
        assertNotNull(drawerLeft);
        assertNotNull(accountHeader);
        Client client = context.client();
        assertNotNull(client);

        status.bufferViewConfigId = bufferViewConfigId;
        accountHeader.setActiveProfile(bufferViewConfigId, false);


        drawerLeft.removeAllItems();
        if (currentConfig != null)
            currentConfig.remove();
        currentConfig = null;

        if (bufferViewConfigId != -1) {
            QBufferViewManager bufferViewManager = client.bufferViewManager();
            assertNotNull(bufferViewManager);
            QBufferViewConfig viewConfig = bufferViewManager.bufferViewConfig(bufferViewConfigId);
            assertNotNull(viewConfig);

            currentConfig = new BufferViewConfigItem(drawerLeft, viewConfig, context);
        }
    }

    private void updateBufferViewConfigs() {
        assertNotNull(context.client().bufferViewManager());
        List<QBufferViewConfig> bufferViews = context.client().bufferViewManager().bufferViewConfigs();
        accountHeader.clear();
        for (QBufferViewConfig viewConfig : bufferViews) {
            if (viewConfig != null) {
                accountHeader.addProfiles(
                        new ProfileDrawerItem()
                                .withName(viewConfig.bufferViewName())
                                .withIdentifier(viewConfig.bufferViewId())
                );
            }
        }
        accountHeader.setActiveProfile(status.bufferViewConfigId, true);
    }

    @Override
    protected void onConnectToThread(@Nullable ClientBackgroundThread thread) {
        super.onConnectToThread(thread);
        if (thread == null)
            connectToServer(manager.account(context.settings().lastAccount.get()));
    }

    public void displayFilterDialog() {
        if (context.client() != null) {
            List<Integer> filterSettings = Arrays.asList(
                    Message.Type.Join.value,
                    Message.Type.Part.value,
                    Message.Type.Quit.value,
                    Message.Type.Nick.value,
                    Message.Type.Mode.value,
                    Message.Type.Topic.value
            );
            int[] filterSettingsInts = new int[filterSettings.size()];
            for (int i = 0; i < filterSettingsInts.length; i++) {
                filterSettingsInts[i] = filterSettings.get(i);
            }

            BacklogFilter backlogFilter = context.client().backlogManager().filter(context.client().backlogManager().open());
            int oldFilters = backlogFilter.getFilters();
            List<Integer> oldFiltersList = new ArrayList<>();
            for (int type : filterSettings) {
                if ((type & oldFilters) != 0)
                    oldFiltersList.add(filterSettings.indexOf(type));
            }
            Integer[] selectedIndices = oldFiltersList.toArray(new Integer[oldFiltersList.size()]);
            new MaterialDialog.Builder(this)
                    .items(
                            "Joins",
                            "Parts",
                            "Quits",
                            "Nick Changes",
                            "Mode Changes",
                            "Topic Changes"
                    )
                    .itemsIds(filterSettingsInts)
                    .itemsCallbackMultiChoice(
                            selectedIndices,
                            (dialog, which, text) -> false
                    )
                    .positiveText("Select")
                    .negativeText("Cancel")
                    .onPositive((dialog, which) -> {
                        int filters = 0x00000000;
                        if (dialog.getSelectedIndices() != null)
                            for (int i : dialog.getSelectedIndices()) {
                                filters |= filterSettings.get(i);
                            }
                        backlogFilter.setFilters(filters);
                    })
                    .build()
                    .show();
        }
    }
}
