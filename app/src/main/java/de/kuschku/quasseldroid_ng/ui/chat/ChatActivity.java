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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Splitter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aspm.annotations.BooleanPreference;
import aspm.annotations.IntPreference;
import aspm.annotations.PreferenceWrapper;
import aspm.annotations.StringPreference;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.events.BacklogInitEvent;
import de.kuschku.libquassel.events.BacklogReceivedEvent;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.events.InitEvent;
import de.kuschku.libquassel.events.LagChangedEvent;
import de.kuschku.libquassel.events.LoginRequireEvent;
import de.kuschku.libquassel.events.UnknownCertificateEvent;
import de.kuschku.libquassel.localtypes.BacklogFilter;
import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.localtypes.buffers.ChannelBuffer;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.interfaces.QBacklogManager;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewManager;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.quasseldroid_ng.BuildConfig;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.service.ClientBackgroundThread;
import de.kuschku.quasseldroid_ng.service.QuasselService;
import de.kuschku.quasseldroid_ng.ui.chat.chatview.MessageAdapter;
import de.kuschku.quasseldroid_ng.ui.chat.drawer.BufferItem;
import de.kuschku.quasseldroid_ng.ui.chat.drawer.BufferViewConfigItem;
import de.kuschku.quasseldroid_ng.ui.editor.AdvancedEditor;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.quasseldroid_ng.ui.theme.AppTheme;
import de.kuschku.quasseldroid_ng.ui.theme.ThemeUtil;
import de.kuschku.util.ServerAddress;
import de.kuschku.util.certificates.CertificateUtils;
import de.kuschku.util.instancestateutil.Storable;
import de.kuschku.util.instancestateutil.Store;
import de.kuschku.util.observables.AutoScroller;
import de.kuschku.util.observables.lists.ObservableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@UiThread
public class ChatActivity extends AppCompatActivity {
    @NonNull
    private final Status status = new Status();
    @NonNull
    private final ServiceInterface serviceInterface = new ServiceInterface();
    @NonNull
    private final AppContext context = new AppContext();
    // Main layout
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;
    // Input Line
    @Bind(R.id.chatline_scroller)
    ScrollView chatlineScroller;
    @Bind(R.id.chatline)
    AppCompatEditText chatline;
    @Bind(R.id.send)
    AppCompatImageButton send;
    // Input History
    @Bind(R.id.sliding_layout_history)
    SlidingUpPanelLayout slidingLayoutHistory;
    @Bind(R.id.msg_history)
    RecyclerView msgHistory;
    // Advanced Formatter
    @Bind(R.id.formatting_menu)
    ActionMenuView formattingMenu;
    @Bind(R.id.formatting_toolbar)
    Toolbar formattingToolbar;
    // Content view
    @Bind(R.id.swipe_view)
    SwipeRefreshLayout swipeView;
    @Bind(R.id.messages)
    RecyclerView messages;
    private MessageAdapter messageAdapter;
    private AccountHeader accountHeader;
    private Drawer drawerLeft;
    private Drawer drawerRight;
    private AdvancedEditor editor;
    @Nullable
    private QuasselService.LocalBinder binder;
    @Nullable
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @UiThread
        public void onServiceConnected(ComponentName cn, IBinder service) {
            assertNotNull(cn);
            assertNotNull(service);

            if (service instanceof QuasselService.LocalBinder) {
                ChatActivity.this.binder = (QuasselService.LocalBinder) service;
                if (binder != null && binder.getBackgroundThread() != null) {
                    ClientBackgroundThread backgroundThread = binder.getBackgroundThread();
                    assertNotNull(backgroundThread);

                    serviceInterface.disconnect();

                    BusProvider provider = backgroundThread.client().provider;
                    Client client = backgroundThread.client().client;
                    context.setProvider(provider);
                    context.setClient(client);
                    provider.event.register(ChatActivity.this);


                    updateSubTitle();
                    if (client.connectionStatus() == ConnectionChangeEvent.Status.CONNECTED) {
                        updateBufferViewConfigs();
                    }
                }
            }
        }

        @UiThread
        public void onServiceDisconnected(ComponentName cn) {
            assertNotNull(cn);

            serviceInterface.disconnect();
            binder = null;
        }
    };

    private static void updateNoColor(@Nullable Buffer buffer, @NonNull Menu menu) {
        boolean isNoColor = isNoColor(buffer);
        MenuItem item_bold = menu.findItem(R.id.format_bold);
        if (item_bold != null)
            item_bold.setEnabled(!isNoColor);
        MenuItem item_italic = menu.findItem(R.id.format_italic);
        if (item_italic != null)
            item_italic.setEnabled(!isNoColor);
        MenuItem item_underline = menu.findItem(R.id.format_underline);
        if (item_underline != null)
            item_underline.setEnabled(!isNoColor);
        MenuItem item_paint = menu.findItem(R.id.format_paint);
        if (item_paint != null)
            item_paint.setEnabled(!isNoColor);
        MenuItem item_fill = menu.findItem(R.id.format_fill);
        if (item_fill != null)
            item_fill.setEnabled(!isNoColor);
    }

    public static boolean isNoColor(@Nullable Buffer buffer) {
        if (buffer == null)
            return false;
        if (!(buffer instanceof ChannelBuffer))
            return false;
        QIrcChannel channel = ((ChannelBuffer) buffer).getChannel();
        return channel != null && channel.hasMode('c');
    }

    private void updateSubTitle() {
        Client client = context.client();
        if (client != null) {
            ConnectionChangeEvent.Status status = client.connectionStatus();
            if (status == ConnectionChangeEvent.Status.CONNECTED) {
                if (this.status.bufferId > 0) {
                    Buffer buffer = client.bufferManager().buffer(this.status.bufferId);
                    if (buffer != null && buffer instanceof ChannelBuffer) {
                        QIrcChannel channel = ((ChannelBuffer) buffer).getChannel();
                        if (channel != null) {
                            updateSubTitle(channel.topic());
                            return;
                        }
                    }
                }
            } else if (status != null) {
                updateSubTitle(status.name());
                return;
            }
        }
        updateSubTitle("");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setupContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Intent intent = new Intent(this, QuasselService.class);
        startService(intent);

        setupHeader(savedInstanceState);

        setupDrawer(savedInstanceState);

        setupEditor();

        setupContent();

        setupHistory();

        initLoader();
    }

    @Override
    protected void onPause() {
        serviceInterface.disconnect();
        unbindService(serviceConnection);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, QuasselService.class);
        bindService(intent, serviceConnection, Context.BIND_IMPORTANT);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        assertNotNull(outState);

        super.onSaveInstanceState(outState);
        status.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        assertNotNull(savedInstanceState);

        super.onRestoreInstanceState(savedInstanceState);
        status.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // FIXME: REWRITE
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        assertNotNull(item);

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

        switch (item.getItemId()) {
            case R.id.action_hide_events: {
                if (context.client() != null) {
                    BacklogFilter backlogFilter = context.client().backlogManager().filter(status.bufferId);
                    if (backlogFilter != null) {
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupContext() {
        WrappedSettings settings = new WrappedSettings(this);
        context.setSettings(settings);
        AppTheme theme = AppTheme.themeFromString(settings.theme.get());
        setTheme(theme.themeId);
        context.setThemeUtil(new ThemeUtil(this, theme));
    }

    private void setupEditorLayout() {
        assertNotNull(slidingLayout);

        slidingLayout.setAntiDragView(R.id.card_panel);
        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelCollapsed(View panel) {
                setChatlineExpanded(false);
            }

            @Override
            public void onPanelExpanded(View panel) {
                setChatlineExpanded(true);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
        setChatlineExpanded(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    private void initLoader() {
        assertNotNull(swipeView);
        ThemeUtil themeUtil = context.themeUtil();
        assertNotNull(themeUtil);

        Client client = context.client();
        assertNotNull(client);
        QBacklogManager<? extends QBacklogManager> backlogManager = client.backlogManager();


        swipeView.setEnabled(false);
        swipeView.setColorSchemeColors(themeUtil.res.colorPrimary);
        swipeView.setOnRefreshListener(() -> {
            assertNotNull(backlogManager);
            backlogManager.requestMoreBacklog(status.bufferId, 20);
        });
    }

    private void setupHistory() {
        assertNotNull(msgHistory);

        FastAdapter<IItem> fastAdapter = new FastAdapter<>();
        ItemAdapter<IItem> itemAdapter = new ItemAdapter<>();
        itemAdapter.wrap(fastAdapter);
        itemAdapter.add(
                new PrimaryDrawerItem().withName("Entry #1"),
                new PrimaryDrawerItem().withName("Entry #2"),
                new PrimaryDrawerItem().withName("Entry #3"),
                new PrimaryDrawerItem().withName("Entry #4"),
                new PrimaryDrawerItem().withName("Entry #5"),
                new PrimaryDrawerItem().withName("Entry #6"),
                new PrimaryDrawerItem().withName("Entry #7"),
                new PrimaryDrawerItem().withName("Entry #8"),
                new PrimaryDrawerItem().withName("Entry #9"),
                new PrimaryDrawerItem().withName("Entry #10"),
                new PrimaryDrawerItem().withName("Entry #11"),
                new PrimaryDrawerItem().withName("Entry #12"),
                new PrimaryDrawerItem().withName("Entry #13"),
                new PrimaryDrawerItem().withName("Entry #14"),
                new PrimaryDrawerItem().withName("Entry #15"),
                new PrimaryDrawerItem().withName("Entry #16")
        );
        msgHistory.setAdapter(fastAdapter);
        msgHistory.setLayoutManager(new LinearLayoutManager(this));
        msgHistory.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupContent() {
        assertNotNull(messages);

        messages.setItemAnimator(new DefaultItemAnimator());
        messages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        messageAdapter = new MessageAdapter(this, context, new AutoScroller(messages));
        messages.setAdapter(messageAdapter);
    }

    private void setupEditor() {
        assertNotNull(formattingMenu);
        assertNotNull(editor);
        assertNotNull(slidingLayoutHistory);
        assertNotNull(send);
        assertNotNull(chatline);
        Client client = context.client();
        assertNotNull(client);

        getMenuInflater().inflate(R.menu.formatting, formattingMenu.getMenu());
        formattingMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.format_bold:
                    editor.toggleBold();
                    return true;
                case R.id.format_italic:
                    editor.toggleItalic();
                    return true;
                case R.id.format_underline:
                    editor.toggleUnderline();
                    return true;
                case R.id.action_history:
                    slidingLayoutHistory.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    return true;
                default:
                    return false;
            }
        });
        editor = new AdvancedEditor(context, chatline);
        send.setOnClickListener(view -> sendInput());

        setupEditorLayout();
    }

    private void setupDrawer(@Nullable Bundle savedInstanceState) {
        drawerLeft = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withSavedInstance(savedInstanceState)
                .withTranslucentStatusBar(true)
                .build();
        assertNotNull(drawerLeft);
        assertNotNull(drawerLeft.getAdapter());
        drawerLeft.addStickyFooterItem(new PrimaryDrawerItem().withIcon(R.drawable.ic_server_light).withName("(Re-)Connect").withIdentifier(-1));
        drawerLeft.addStickyFooterItem(new SecondaryDrawerItem().withName("Settings").withIdentifier(-2));
        drawerLeft.setOnDrawerItemClickListener((view, position, drawerItem) -> {
            long identifier = drawerItem.getIdentifier();
            if (identifier == -1) {
                showConnectDialog();
                return false;
            } else if (identifier == -2) {
                showThemeDialog();
                return false;
            } else {
                if (((IExpandable) drawerItem).getSubItems() != null) {
                    drawerLeft.getAdapter().toggleExpandable(position);
                    return true;
                } else if (drawerItem instanceof BufferItem) {
                    selectBuffer(((BufferItem) drawerItem).getBuffer().getInfo().id());
                    return false;
                }
                return true;
            }
        });
        drawerRight = new DrawerBuilder()
                .withActivity(this)
                .withSavedInstance(savedInstanceState)
                .withDrawerGravity(Gravity.END)
                .build();
    }

    private void setupHeader(@Nullable Bundle savedInstanceState) {
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.bg)
                .withSavedInstance(savedInstanceState)
                .withProfileImagesVisible(false)
                .withOnAccountHeaderListener((view, profile, current) -> {
                    selectBufferViewConfig((int) profile.getIdentifier());
                    return true;
                })
                .build();
    }

    public void setChatlineExpanded(boolean expanded) {
        assertNotNull(chatline);
        assertNotNull(chatline.getLayoutParams());
        ThemeUtil themeUtil = context.themeUtil();
        assertNotNull(themeUtil);

        int selectionStart = chatline.getSelectionStart();
        int selectionEnd = chatline.getSelectionEnd();

        if (expanded) {
            chatline.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            chatline.getLayoutParams().height = themeUtil.res.actionBarSize;
        }
        chatline.setSingleLine(!expanded);

        chatline.setSelection(selectionStart, selectionEnd);
    }

    // FIXME: Rewrite properly
    public void showThemeDialog() {
        String[] strings = new String[AppTheme.values().length];
        int startIndex = -1;
        for (int i = 0; i < strings.length; i++) {
            AppTheme theme = AppTheme.values()[i];
            strings[i] = theme.name();
            if (theme.name().equals(context.settings().theme.get())) startIndex = i;
        }

        new MaterialDialog.Builder(this)
                .items(strings)
                .positiveText("Select Theme")
                .negativeText("Cancel")
                .itemsCallbackSingleChoice(startIndex, (dialog, itemView, which, text) -> {
                    context.settings().theme.set(strings[dialog.getSelectedIndex()]);
                    recreate();
                    return true;
                })
                .negativeColor(context.themeUtil().res.colorForeground)
                .build()
                .show();
    }

    private void selectBufferViewConfig(@IntRange(from = -1) int bufferViewConfigId) {
        assertNotNull(drawerLeft);
        assertNotNull(accountHeader);
        Client client = context.client();
        assertNotNull(client);

        status.bufferViewConfigId = bufferViewConfigId;
        accountHeader.setActiveProfile(bufferViewConfigId, false);

        if (bufferViewConfigId == -1) {
            drawerLeft.removeAllItems();
        } else {
            drawerLeft.removeAllItems();
            QBufferViewManager bufferViewManager = client.bufferViewManager();
            assertNotNull(bufferViewManager);
            QBufferViewConfig viewConfig = bufferViewManager.bufferViewConfig(bufferViewConfigId);
            assertNotNull(viewConfig);

            new BufferViewConfigItem(drawerLeft, viewConfig, context);
        }
    }

    private void selectBuffer(@IntRange(from = -1) int bufferId) {
        context.client().backlogManager().open(bufferId);
        if (bufferId == -1) {
            status.bufferId = bufferId;
            swipeView.setEnabled(false);
            context.client().backlogManager().open(bufferId);

            messageAdapter.setMessageList(MessageAdapter.emptyList());
            toolbar.setTitle(getResources().getString(R.string.appName));
        } else {
            status.bufferId = bufferId;
            swipeView.setEnabled(true);
            context.client().backlogManager().open(bufferId);

            // Make sure we are actually connected
            ObservableSortedList<Message> list = context.client().backlogManager().filtered(bufferId);
            Buffer buffer = context.client().bufferManager().buffer(bufferId);
            // Make sure everything is properly defined
            assertNotNull("Buffer is null: " + bufferId, buffer);
            assertNotNull(list);

            messageAdapter.setMessageList(list);
            toolbar.setTitle(buffer.getName());
            updateNoColor(buffer, formattingMenu.getMenu());

            if (buffer instanceof ChannelBuffer && ((ChannelBuffer) buffer).getChannel() != null) {
                NickListWrapper nicklistwrapper = new NickListWrapper(drawerRight, ((ChannelBuffer) buffer).getChannel());
            } else {
                drawerRight.removeAllItems();
            }
        }
        updateSubTitle();
    }

    private void onConnectionEstablished() {
        assertNotNull(binder);
        assertNotNull(binder.getBackgroundThread());
        context.setClient(binder.getBackgroundThread().client().client);
        assertNotNull(context.client());
    }

    private void sendInput() {
        if (context.client() == null) return;

        int bufferId = status.bufferId;

        if (bufferId >= 0) {
            Buffer buffer = context.client().bufferManager().buffer(bufferId);
            assertNotNull(buffer);

            String text = editor.toFormatString();
            context.client().sendInput(buffer.getInfo(), text);
            chatline.setText("");
        } else {
            Snackbar.make(messages, "No buffer opened", Snackbar.LENGTH_LONG).show();
        }
        chatline.setVisibility(View.INVISIBLE);
    }

    public void onEventMainThread(@NonNull LoginRequireEvent event) {
        assertNotNull(context.client());

        if (event.failedLast)
            showLoginDialog();
        else
            context.client().login(
                    context.settings().lastUsername.or(""),
                    context.settings().lastPassword.or("")
            );
    }

    public void onEventMainThread(@NonNull ConnectionChangeEvent event) {
        updateSubTitle();

        switch (event.status) {
            case CONNECTED:
                updateBufferViewConfigs();
                break;
        }
    }

    public void onEventMainThread(@NonNull BacklogInitEvent event) {
        updateSubTitle(event.toString());
    }

    public void onEventMainThread(@NonNull InitEvent event) {
        updateSubTitle(event.toString());
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
        selectBufferViewConfig(status.bufferViewConfigId);
        selectBuffer(status.bufferId);
    }

    private void showLoginDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.labelLogin)
                .customView(R.layout.dialog_login, false)
                .onPositive((dialog1, which) -> {
                    View parent = dialog1.getCustomView();
                    assertNotNull(parent);
                    AppCompatEditText usernameField = (AppCompatEditText) parent.findViewById(R.id.username);
                    AppCompatEditText passwordField = (AppCompatEditText) parent.findViewById(R.id.password);
                    String username = usernameField.getText().toString();
                    String password = passwordField.getText().toString();
                    context.settings().lastUsername.set(username);
                    context.settings().lastPassword.set(password);
                    context.client().login(username, password);
                })
                .cancelListener(dialog1 -> {
                    if (binder != null)
                        binder.stopBackgroundThread();
                    serviceInterface.disconnect();
                })
                .negativeColor(context.themeUtil().res.colorForeground)
                .positiveText(R.string.labelLogin)
                .negativeText(R.string.labelCancel)
                .build();
        ((AppCompatEditText) dialog.getView().findViewById(R.id.username)).setText(context.settings().lastUsername.get());
        ((AppCompatEditText) dialog.getView().findViewById(R.id.password)).setText(context.settings().lastPassword.get());
        dialog.show();
    }

    public void showConnectDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.labelAddress)
                .customView(R.layout.dialog_address, false)
                .onPositive((dialog1, which) -> {
                    if (binder != null && binder.getBackgroundThread() != null)
                        binder.stopBackgroundThread();

                    View parent = dialog1.getCustomView();
                    assertNotNull(parent);
                    AppCompatEditText hostField = (AppCompatEditText) parent.findViewById(R.id.host);
                    AppCompatEditText portField = (AppCompatEditText) parent.findViewById(R.id.port);
                    String host = hostField.getText().toString().trim();
                    int port = Integer.valueOf(portField.getText().toString().trim());
                    context.settings().lastHost.set(host);
                    context.settings().lastPort.set(port);
                    serviceInterface.connect(new ServerAddress(host, port));
                })
                .negativeColor(context.themeUtil().res.colorForeground)
                .positiveText(R.string.labelConnect)
                .negativeText(R.string.labelCancel)
                .build();
        AppCompatEditText hostField = (AppCompatEditText) dialog.getView().findViewById(R.id.host);
        AppCompatEditText portField = (AppCompatEditText) dialog.getView().findViewById(R.id.port);

        hostField.setText(context.settings().lastHost.get());
        portField.setText(String.valueOf(context.settings().lastPort.get()));

        dialog.show();
    }

    public void onEventMainThread(@NonNull BacklogReceivedEvent event) {
        if (event.bufferId == status.bufferId) {
            swipeView.setRefreshing(false);
        }
    }

    public void onEventMainThread(@NonNull UnknownCertificateEvent event) {
        new MaterialDialog.Builder(this)
                .content(context.themeUtil().translations.warningCertificate + "\n" + CertificateUtils.certificateToFingerprint(event.certificate, ""))
                .title("Unknown Certificate")
                .onPositive((dialog, which) -> {
                    if (binder != null && binder.getBackgroundThread() != null) {
                        binder.getBackgroundThread().client().certificateManager.addCertificate(event.certificate, event.address);
                    }
                })
                .negativeColor(context.themeUtil().res.colorForeground)
                .positiveText("Yes")
                .negativeText("No")
                .build()
                .show();
    }

    public void onEventMainThread(@NonNull GeneralErrorEvent event) {
        assertNotNull(messages);

        Snackbar.make(messages, event.toString(), Snackbar.LENGTH_LONG).show();
        for (String line : Splitter.fixedLength(2048).split(event.toString())) {
            Log.e("ChatActivity", line);
        }
        if (event.exception != null)
            event.exception.printStackTrace();
    }

    public void onEventMainThread(LagChangedEvent event) {
        updateSubTitle();
    }

    private void updateSubTitle(@Nullable CharSequence text) {
        assertNotNull(toolbar);

        if (text != null) {
            toolbar.setSubtitle(text);
        } else {
            toolbar.setSubtitle("");
        }
    }

    private static class Status extends Storable {
        @Store
        int bufferId = -1;
        @Store
        int bufferViewConfigId = -1;
    }

    @PreferenceWrapper(BuildConfig.APPLICATION_ID)
    public static abstract class Settings {
        @StringPreference("QUASSEL_LIGHT")
        String theme;
        @BooleanPreference(false)
        boolean fullHostmask;
        @IntPreference(2)
        int textSize;
        @BooleanPreference(true)
        boolean mircColors;

        @StringPreference("")
        String lastHost;
        @IntPreference(4242)
        int lastPort;
        @StringPreference("")
        String lastUsername;
        @StringPreference("")
        String lastPassword;
    }

    private class ServiceInterface {
        private void connect(@NonNull ServerAddress address) {
            assertNotNull(binder);
            disconnect();

            context.setProvider(new BusProvider());
            context.provider().event.register(ChatActivity.this);
            binder.startBackgroundThread(context.provider(), address);
            onConnectionEstablished();
        }

        private void disconnect() {
            if (context.provider() != null) {
                context.provider().event.unregister(ChatActivity.this);
            }
            if (context.client() != null) {
                context.client().backlogManager().open(-1);
            }
            context.setProvider(null);
            context.setClient(null);
        }
    }
}
