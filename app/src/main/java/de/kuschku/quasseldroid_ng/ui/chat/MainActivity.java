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

import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.events.BufferChangeEvent;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.CoreSetupRequiredEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.events.LoginRequireEvent;
import de.kuschku.libquassel.events.UnknownCertificateEvent;
import de.kuschku.libquassel.functions.types.HandshakeFunction;
import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.localtypes.buffers.ChannelBuffer;
import de.kuschku.libquassel.localtypes.buffers.QueryBuffer;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.objects.types.CoreSetupData;
import de.kuschku.libquassel.objects.types.SetupData;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.interfaces.QBacklogManager;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.service.ClientBackgroundThread;
import de.kuschku.quasseldroid_ng.ui.chat.dialogs.CoreInfoDialogBuilder;
import de.kuschku.quasseldroid_ng.ui.chat.drawer.ActionModeHandler;
import de.kuschku.quasseldroid_ng.ui.chat.drawer.BufferViewConfigAdapter;
import de.kuschku.quasseldroid_ng.ui.chat.fragment.ChatFragment;
import de.kuschku.quasseldroid_ng.ui.chat.fragment.LoadingFragment;
import de.kuschku.quasseldroid_ng.ui.chat.nicklist.NickListAdapter;
import de.kuschku.quasseldroid_ng.ui.chat.util.Status;
import de.kuschku.quasseldroid_ng.ui.coresettings.aliases.AliasListActivity;
import de.kuschku.quasseldroid_ng.ui.coresettings.chatlist.ChatListListActivity;
import de.kuschku.quasseldroid_ng.ui.coresettings.identity.IdentityListActivity;
import de.kuschku.quasseldroid_ng.ui.coresettings.ignore.IgnoreListActivity;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.NetworkListActivity;
import de.kuschku.quasseldroid_ng.ui.settings.SettingsActivity;
import de.kuschku.quasseldroid_ng.ui.setup.CoreSetupActivity;
import de.kuschku.util.CompatibilityUtils;
import de.kuschku.util.accounts.Account;
import de.kuschku.util.accounts.AccountManager;
import de.kuschku.util.annotationbind.AutoBinder;
import de.kuschku.util.certificates.CertificateUtils;
import de.kuschku.util.certificates.SQLiteCertificateManager;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.servicebound.BoundActivity;
import de.kuschku.util.ui.DividerItemDecoration;
import de.kuschku.util.ui.MenuTint;
import rx.android.schedulers.AndroidSchedulers;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class MainActivity extends BoundActivity {
    public static final int REQUEST_CODE_CORESETUP = 1;
    /**
     * This object encapsulates the current status of the activity – opened bufferview, for example
     */
    private final Status status = new Status();

    /**
     * Host layout for content fragment, for example showing a loader or the chat
     */
    @Bind(R.id.chatList)
    RecyclerView chatList;

    @Bind(R.id.nickList)
    RecyclerView nickList;

    @Bind(R.id.chatListSpinner)
    AppCompatSpinner chatListSpinner;

    @Bind(R.id.chatListToolbar)
    Toolbar chatListToolbar;

    @Nullable
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    /**
     * Main ActionBar
     */
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private AccountManager manager;

    private ToolbarWrapper toolbarWrapper;

    private BufferViewConfigAdapter chatListAdapter;
    private Fragment currentFragment;

    private Bundle coreSetupResult;
    private boolean coreSetupCancelled;
    private CoreInfoDialogBuilder coreInfoDialogBuilder;
    private NickListAdapter nickListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbarWrapper = new ToolbarWrapper(toolbar);
        toolbarWrapper.setOnClickListener(v -> {
            if (context.client() != null) {
                int id = context.client().backlogManager().open();
                Buffer buffer = context.client().bufferManager().buffer(id);
                if (buffer instanceof ChannelBuffer) {
                    Intent intent = new Intent(this, ChannelDetailActivity.class);
                    intent.putExtra("buffer", id);
                    startActivity(intent);
                }
            }
        });
        setSupportActionBar(toolbar);
        chatListAdapter = BufferViewConfigAdapter.of(context);
        chatListAdapter.setBufferClickListener(buffer -> {
            if (context.client() != null) {
                context.client().backlogManager().open(buffer.getInfo().id);
                if (drawerLayout != null)
                    drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        chatListAdapter.setActionModeHandler(new ActionModeHandler(this, R.id.cab_stub));
        chatListAdapter.setRecyclerView(chatList);
        chatList.setItemAnimator(new DefaultItemAnimator());
        chatList.setLayoutManager(new LinearLayoutManager(this));
        chatList.setAdapter(chatListAdapter);

        coreInfoDialogBuilder = new CoreInfoDialogBuilder(this);

        chatListToolbar.inflateMenu(R.menu.chatlist);
        MenuTint.colorIcons(chatListToolbar.getMenu(), AutoBinder.obtainColor(R.attr.colorFill, chatListToolbar.getContext().getTheme()));
        chatListToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_show_all: {
                    item.setChecked(chatListAdapter.toggleShowAll());
                }
                break;
                case R.id.action_manage_chat_lists: {
                    startActivity(new Intent(this, ChatListListActivity.class));
                }
            }
            return false;
        });

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.material_drawer_open, R.string.material_drawer_close);
            toggle.syncState();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        replaceFragment(new LoadingFragment());

        if (savedInstanceState != null)
            status.onRestoreInstanceState(savedInstanceState);

        manager = new AccountManager(this);

        nickListAdapter = new NickListAdapter(context);
        nickList.setAdapter(nickListAdapter);
        nickList.setLayoutManager(new LinearLayoutManager(this));
        nickList.setItemAnimator(new DefaultItemAnimator());
        nickList.addItemDecoration(new DividerItemDecoration(this));

        setStatusBar();
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

            nickList.setPadding(0, CompatibilityUtils.getStatusBarHeight(this), 0, 0);

            View viewById = findViewById(R.id.statusBarBackground);
            ViewGroup.LayoutParams layoutParams = viewById.getLayoutParams();
            layoutParams.height = CompatibilityUtils.getStatusBarHeight(this);
            viewById.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (context.client() != null) {
            context.client().backlogManager().setOpen(-1);
            context.client().backlogStorage().markBufferUnused(context.client().backlogManager().open());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (context.client() != null)
            context.client().backlogManager().open(status.bufferId);
    }

    private void replaceFragment(Fragment fragment) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            this.currentFragment = fragment;
            transaction.replace(R.id.content_host, fragment);
            transaction.commit();
        } catch (IllegalStateException ignored) {
        }
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
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        RxSearchView.queryTextChanges(searchView)
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if (context.client() != null && context.client().connectionStatus() == ConnectionChangeEvent.Status.CONNECTED)
                        context.client().backlogStorage().getFilter(context.client().backlogManager().open()).setQuery(charSequence);
                });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        chatListAdapter.selectConfig(-1);
        toolbarWrapper.setOnClickListener(null);
        chatListAdapter.setBufferClickListener(null);
        chatListSpinner.setOnItemSelectedListener(null);
        chatListToolbar.setOnMenuItemClickListener(null);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_hide_events:
                displayFilterDialog();
                return true;
            case R.id.action_reauth:
                reauth();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_identities:
                startActivity(new Intent(this, IdentityListActivity.class));
                return true;
            case R.id.action_networks:
                startActivity(new Intent(this, NetworkListActivity.class));
                return true;
            case R.id.action_ignorelist:
                startActivity(new Intent(this, IgnoreListActivity.class));
                return true;
            case R.id.action_aliaslist:
                startActivity(new Intent(this, AliasListActivity.class));
                return true;
            case R.id.action_coreinfo:
                if (context.client() != null && context.client().coreInfo() != null)
                    coreInfoDialogBuilder.build(manager.account(context.settings().preferenceLastAccount.get()), context.client().coreInfo(), context.client().certificateChain()).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reauth() {
        context.settings().preferenceLastAccount.set("");
        stopConnection();
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(ConnectionChangeEvent event) {
        onConnectionChange(event.status);
    }

    public void onConnectionChange(ConnectionChangeEvent.Status status) {
        if (status == ConnectionChangeEvent.Status.CONNECTED) {
            replaceFragment(new ChatFragment());
            connected();
        } else if (status == ConnectionChangeEvent.Status.DISCONNECTED) {
            Toast.makeText(getApplication(), context.themeUtil().translations.statusDisconnected, Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GeneralErrorEvent event) {
        Toast.makeText(getApplication(), event.exception.getClass().getSimpleName() + ": " + event.debugInfo, Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(BufferChangeEvent event) {
        Client client = context.client();
        if (client != null) {
            QBacklogManager backlogManager = client.backlogManager();
            int id = backlogManager.open();
            status.bufferId = id;
            updateBuffer(id);
            chatListAdapter.setOpen(id);
        }
    }

    private void updateBuffer(int id) {
        nickListAdapter.setChannel(null);
        updateTitleAndSubtitle(id);
    }

    private void updateTitleAndSubtitle(int id) {
        Client client = context.client();
        if (client != null) {
            Buffer buffer = client.bufferManager().buffer(id);
            if (buffer != null) {
                toolbarWrapper.setTitle(buffer.getName());
                if (buffer instanceof QueryBuffer) {
                    QIrcUser user = ((QueryBuffer) buffer).getUser();
                    if (user == null) {
                        toolbarWrapper.setSubtitle(null);
                    } else {
                        toolbarWrapper.setSubtitle(user.realName());
                    }
                } else if (buffer instanceof ChannelBuffer) {
                    QIrcChannel channel = ((ChannelBuffer) buffer).getChannel();
                    nickListAdapter.setChannel(channel);
                    if (channel == null) {
                        toolbarWrapper.setSubtitle(null);
                    } else {
                        toolbarWrapper.setSubtitle(context.deserializer().formatString(channel.topic()));
                    }
                } else {
                    toolbarWrapper.setSubtitle(null);
                }
            }
        }
    }

    /*
    private void updateBufferViewConfigs() {
        assertNotNull(context.client().bufferViewManager());
        List<QBufferViewConfig> bufferViews = context.client().bufferViewManager().bufferViewConfigs();
        accountHeader.clear();
        for (QBufferViewConfig viewConfig : bufferViews) {
            if (viewConfig != null) {
                if (status.bufferViewConfigId == -1) {
                    status.bufferViewConfigId = viewConfig.bufferViewId();
                }
                accountHeader.addProfiles(
                        new ProfileDrawerItem()
                                .withName(viewConfig.bufferViewName())
                                .withIdentifier(viewConfig.bufferViewId())
                );
            }
        }
        accountHeader.setActiveProfile(status.bufferViewConfigId, true);
    }
    */

    @Override
    public void onBackPressed() {
        if ((currentFragment instanceof ChatFragment && ((ChatFragment) currentFragment).onBackPressed())) {
            return;
        }
        super.onBackPressed();
    }

    protected void reconnect() {
        binder.stopBackgroundThread();
        connectToServer(manager.account(context.settings().preferenceLastAccount.get()));
    }

    @Override
    protected void onConnectToThread(@Nullable ClientBackgroundThread thread) {
        super.onConnectToThread(thread);
        if (thread == null)
            connectToServer(manager.account(context.settings().preferenceLastAccount.get()));
        else {
            if (context.client() != null && context.client().connectionStatus() == ConnectionChangeEvent.Status.CONNECTED) {
                connected();
            }
        }
    }

    private void connected() {
        if (drawerLayout != null)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        context.client().bufferManager().bufferIds().addCallback(new ElementCallback<Integer>() {
            @Override
            public void notifyItemInserted(Integer element) {

            }

            @Override
            public void notifyItemRemoved(Integer element) {

            }

            @Override
            public void notifyItemChanged(Integer element) {
                if (element == context.client().backlogManager().open())
                    updateTitleAndSubtitle(element);
            }
        });
        context.client().backlogManager().open(status.bufferId);
        if (context.client().bufferViewManager() != null) {
            chatListSpinner.setAdapter(new BufferViewConfigSpinnerAdapter(context.client().bufferViewManager()));
            chatListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chatListAdapter.selectConfig((int) id);
                    status.bufferViewConfigId = (int) id;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    chatListAdapter.selectConfig(-1);
                }
            });
        }
        updateBuffer(context.client().backlogManager().open());
        chatListSpinner.setSelection(chatListAdapter.indexOf(status.bufferViewConfigId));
    }

    // FIXME: Fix this ugly hack
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

            int bufferId = context.client().backlogManager().open();
            int oldFilters = context.client().bufferSyncer().getFilters(bufferId);
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
                                int settingsid = filterSettings.get(i);
                                filters |= settingsid;
                                if (settingsid == Message.Type.Quit.value)
                                    filters |= Message.Type.NetsplitQuit.value;
                                else if (settingsid == Message.Type.Join.value)
                                    filters |= Message.Type.NetsplitJoin.value;
                            }
                        context.client().bufferSyncer().setFilters(bufferId, filters);
                    })
                    .negativeColor(context.themeUtil().res.colorForeground)
                    .backgroundColor(context.themeUtil().res.colorBackgroundCard)
                    .contentColor(context.themeUtil().res.colorForeground)
                    .build()
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CORESETUP: {
                if (resultCode == RESULT_OK) {
                    coreSetupResult = data.getExtras();
                } else {
                    coreSetupCancelled = true;
                }
            }
            break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(@NonNull UnknownCertificateEvent event) {
        new MaterialDialog.Builder(this)
                .content(context.themeUtil().translations.warningCertificate + "\n" + CertificateUtils.certificateToFingerprint(event.certificate, ""))
                .title("Unknown Certificate")
                .onPositive((dialog, which) -> {
                    new SQLiteCertificateManager(this).addCertificate(event.certificate, event.address);
                    reconnect();
                })
                .negativeColor(context.themeUtil().res.colorForeground)
                .positiveText("Yes")
                .negativeText("No")
                .backgroundColor(context.themeUtil().res.colorBackgroundCard)
                .contentColor(context.themeUtil().res.colorForeground)
                .build()
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(CoreSetupRequiredEvent event) {
        if (coreSetupCancelled) {
            finish();
        } else if (coreSetupResult != null) {
            context.provider().event.removeStickyEvent(CoreSetupRequiredEvent.class);

            Account account = manager.account(context.settings().preferenceLastAccount.get());
            Bundle config = coreSetupResult.getBundle("config");
            Map<String, QVariant> configData = new HashMap<>();
            for (String key : config.keySet()) {
                configData.put(key, new QVariant<>(config.get(key)));
            }
            context.provider().dispatch(new HandshakeFunction(new CoreSetupData(new SetupData(
                    account.user,
                    account.pass,
                    coreSetupResult.getString("selectedBackend"),
                    configData
            ))));
        } else {
            Intent intent = new Intent(getApplicationContext(), CoreSetupActivity.class);
            intent.putExtra("storageBackends", context.client().core().getStorageBackendsAsBundle());
            startActivityForResult(intent, REQUEST_CODE_CORESETUP);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(LoginRequireEvent event) {
        if (event.failedLast) {
            new MaterialDialog.Builder(this)
                    .title(R.string.titleLogin)
                    .customView(R.layout.dialog_login, false)
                    .onPositive((dialog1, which) -> {
                        View parent = dialog1.getCustomView();
                        assertNotNull(parent);
                        AppCompatEditText usernameField = (AppCompatEditText) parent.findViewById(R.id.username);
                        AppCompatEditText passwordField = (AppCompatEditText) parent.findViewById(R.id.password);
                        String username = usernameField.getText().toString();
                        String password = passwordField.getText().toString();

                        Account account = manager.account(context.settings().preferenceLastAccount.get());
                        manager.update(account.withLoginData(username, password));
                    })
                    .cancelListener(dialog1 -> finish())
                    .negativeColor(context.themeUtil().res.colorForeground)
                    .positiveText(R.string.actionLogin)
                    .negativeText(R.string.actionCancel)
                    .backgroundColor(context.themeUtil().res.colorBackgroundCard)
                    .contentColor(context.themeUtil().res.colorForeground)
                    .build().show();
        }
    }
}
