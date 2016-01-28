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
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Splitter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Map;

import aspm.annotations.PreferenceWrapper;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.events.BacklogReceivedEvent;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.events.LagChangedEvent;
import de.kuschku.libquassel.exceptions.UnknownTypeException;
import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.BufferViewConfig;
import de.kuschku.libquassel.syncables.types.BufferViewManager;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.quasseldroid_ng.BuildConfig;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.service.ClientBackgroundThread;
import de.kuschku.quasseldroid_ng.service.QuasselService;
import de.kuschku.quasseldroid_ng.ui.AppContext;
import de.kuschku.quasseldroid_ng.ui.AppTheme;
import de.kuschku.quasseldroid_ng.ui.chat.chatview.MessageAdapter;
import de.kuschku.quasseldroid_ng.ui.chat.drawer.NetworkItem;
import de.kuschku.util.keyboardutils.DialogKeyboardUtil;
import de.kuschku.util.ServerAddress;
import de.kuschku.util.instancestateutil.Storable;
import de.kuschku.util.instancestateutil.Store;
import de.kuschku.util.observables.AutoScroller;
import de.kuschku.util.observables.lists.ObservableSortedList;
import de.kuschku.util.ui.SpanFormatter;
import de.kuschku.util.ui.ThemeUtil;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@UiThread
public class ChatActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;

    @Bind(R.id.chatline)
    AppCompatEditText chatline;
    @Bind(R.id.send)
    AppCompatImageButton send;

    @Bind(R.id.msg_history)
    RecyclerView msgHistory;

    @Bind(R.id.swipe_view)
    SwipeRefreshLayout swipeView;
    @Bind(R.id.messages)
    RecyclerView messages;


    @PreferenceWrapper(BuildConfig.APPLICATION_ID)
    public static abstract class Settings {
        String theme;
        boolean fullHostmask;
        int textSize;
        boolean mircColors;

        String lastHost;
        int lastPort;
        String lastUsername;
        String lastPassword;
    }

    private AppContext context = new AppContext();

    @NonNull
    private final Status status = new Status();
    private static class Status extends Storable {
        @Store int bufferId = -1;
        @Store int bufferViewConfigId = -1;
    }

    private ServiceInterface serviceInterface = new ServiceInterface();
    private class ServiceInterface {
        private void connect(@NonNull ServerAddress address) {
            assertNotNull(binder);
            disconnect();

            BusProvider provider = new BusProvider();
            provider.event.register(ChatActivity.this);
            binder.startBackgroundThread(provider, address);
            onConnectionEstablished();
        }

        private void disconnect() {
            if (binder != null) binder.stopBackgroundThread();
            if (backgroundThread != null) backgroundThread.provider.event.unregister(this);
            backgroundThread = null;
        }
    }

    private QuasselService.LocalBinder binder;
    private ClientBackgroundThread backgroundThread;

    private MessageAdapter messageAdapter;

    private AccountHeader accountHeader;
    private Drawer drawerLeft;
    private BufferViewConfigWrapper wrapper;
    private CharSequence subtitle;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @UiThread
        public void onServiceConnected(@NonNull ComponentName cn, @NonNull IBinder service) {
            if (service instanceof QuasselService.LocalBinder) {
                ChatActivity.this.binder = (QuasselService.LocalBinder) service;
                if (binder.getBackgroundThread() != null) {
                    connectToThread(binder.getBackgroundThread());
                }
            }
        }

        @UiThread
        public void onServiceDisconnected(@NonNull ComponentName cn) {
            serviceInterface.disconnect();
            binder = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context.setSettings(new WrappedSettings(this));
        AppTheme theme = AppTheme.QUASSEL;
        setTheme(theme.themeId);
        context.setThemeUtil(new ThemeUtil(this, theme));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        connectToService();

        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.bg)
                .withSavedInstance(savedInstanceState)
                .withProfileImagesVisible(false)
                .withOnAccountHeaderListener((view, profile, current) -> {
                    if (!current) {
                        selectBufferViewConfig((int) profile.getIdentifier());
                    }
                    return true;
                })
                .build();

        drawerLeft = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withSavedInstance(savedInstanceState)
                .withTranslucentStatusBar(true)
                .build();
        drawerLeft.addStickyFooterItem(new PrimaryDrawerItem().withIcon(R.drawable.ic_server_light).withName("(Re-)Connect").withIdentifier(-1));
        drawerLeft.setOnDrawerItemClickListener((view, position, drawerItem) -> {
            long identifier = drawerItem.getIdentifier();
            if (identifier == -1) {
                showConnectDialog();
                return false;
            } else {
                if (((IExpandable) drawerItem).getSubItems() != null) {
                    drawerLeft.getAdapter().toggleExpandable(position);
                    return true;
                } else {
                    selectBuffer((int) drawerItem.getIdentifier());
                    return false;
                }
            }
        });

        messages.setItemAnimator(new DefaultItemAnimator());
        messages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        messageAdapter = new MessageAdapter(this, context, new AutoScroller(messages));
        messages.setAdapter(messageAdapter);

        msgHistory.setAdapter(new FastAdapter<>());
        msgHistory.setLayoutManager(new LinearLayoutManager(this));
        msgHistory.setItemAnimator(new DefaultItemAnimator());

        swipeView.setOnRefreshListener(() -> {
            assertNotNull(context.getClient());
            context.getClient().getBacklogManager().requestMoreBacklog(status.bufferId, 20);
        });

        send.setOnClickListener(view -> sendInput());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        assertNotNull(savedInstanceState);

        super.onRestoreInstanceState(savedInstanceState);
        status.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        assertNotNull(outState);

        super.onSaveInstanceState(outState);
        status.onSaveInstanceState(outState);
    }

    private void connectToThread(@NonNull ClientBackgroundThread backgroundThread) {
        assertNotNull(backgroundThread);

        serviceInterface.disconnect();

        this.backgroundThread = backgroundThread;
        backgroundThread.provider.event.register(this);
        selectBuffer(status.bufferId);
        selectBufferViewConfig(status.bufferViewConfigId);
    }

    private void selectBufferViewConfig(@IntRange(from = -1) int bufferViewConfigId) {
        if (wrapper != null) wrapper.setDrawer(null);
        drawerLeft.removeAllItems();
        if (bufferViewConfigId == -1) {
            drawerLeft.removeAllItems();
        } else {
            drawerLeft.removeAllItems();
            BufferViewManager bufferViewManager = context.getClient().getBufferViewManager();
            assertNotNull(bufferViewManager);
            BufferViewConfig viewConfig = bufferViewManager.BufferViews.get(bufferViewConfigId);
            assertNotNull(viewConfig);

            wrapper = new BufferViewConfigWrapper(context, viewConfig, drawerLeft);
            wrapper.updateDrawerItems();
        }
    }

    private void selectBuffer(@IntRange(from = -1) int bufferId) {
        if (bufferId == -1) {
            messageAdapter.setMessageList(MessageAdapter.emptyList());
            toolbar.setTitle(getResources().getString(R.string.app_name));
        } else {
            status.bufferId = bufferId;
            // Make sure we are actually connected
            ObservableSortedList<Message> list = context.getClient().getBacklogManager().getFiltered(status.bufferId);
            Buffer buffer = context.getClient().getBuffer(status.bufferId);
            // Make sure everything is properly defined
            assertNotNull("Buffer is null: " + bufferId, buffer);
            assertNotNull(list);

            messageAdapter.setMessageList(list);
            toolbar.setTitle(buffer.getName());
        }
    }

    private void connectToService() {
        Intent intent = new Intent(this, QuasselService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void onConnectionEstablished() {
        assertNotNull(binder);
        this.backgroundThread = binder.getBackgroundThread();
        assertNotNull(this.backgroundThread);
        context.setClient(this.backgroundThread.handler.client);
        assertNotNull(context.getClient());
    }

    private void sendInput() {
        if (context.getClient() == null) return;
        Buffer buffer = context.getClient().getBuffer(status.bufferId);
        assertNotNull(buffer);

        CharSequence text = chatline.getText();
        context.getClient().sendInput(buffer.getInfo(), text.toString());
    }

    public void onEventMainThread(ConnectionChangeEvent event) {
        setSubtitle(event.status.name());

        switch (event.status) {
            case HANDSHAKE:
                break;
            case CORE_SETUP_REQUIRED:
                break;
            case LOGIN_REQUIRED:
                assertNotNull(context.getClient());

                showLoginDialog();
                break;
            case USER_SETUP_REQUIRED:
                break;
            case CONNECTED:
                Log.e("TIME", String.valueOf(System.currentTimeMillis()));
                updateBufferViewConfigs();
                break;
        }
    }

    private void updateBufferViewConfigs() {
        Map<Integer, BufferViewConfig> bufferViews = context.getClient().getBufferViewManager().BufferViews;
        accountHeader.clear();
        for (Map.Entry<Integer, BufferViewConfig> entry : bufferViews.entrySet()) {
            if (entry.getValue() != null) {
                accountHeader.addProfiles(
                        new ProfileDrawerItem()
                                .withName(entry.getValue().getBufferViewName())
                                .withIdentifier(entry.getKey())
                );
            }
        }
        selectBufferViewConfig(status.bufferViewConfigId);
        selectBuffer(status.bufferId);
    }

    private void showLoginDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Address")
                .customView(R.layout.dialog_login, false)
                .onPositive((dialog1, which) -> {
                    View parent = dialog1.getCustomView();
                    AppCompatEditText usernameField = (AppCompatEditText) parent.findViewById(R.id.username);
                    AppCompatEditText passwordField = (AppCompatEditText) parent.findViewById(R.id.password);
                    String username = usernameField.getText().toString();
                    String password = passwordField.getText().toString();
                    context.getSettings().lastUsername.set(username);
                    context.getSettings().lastPassword.set(password);
                    context.getClient().login(username, password);

                    Log.e("TIME", String.valueOf(System.currentTimeMillis()));
                })
                .positiveText("Login")
                .neutralText("Cancel")
                .build();
        dialog.setOnKeyListener(new DialogKeyboardUtil(dialog));
        ((AppCompatEditText) dialog.getView().findViewById(R.id.username)).setText(context.getSettings().lastUsername.or(""));
        ((AppCompatEditText) dialog.getView().findViewById(R.id.password)).setText(context.getSettings().lastPassword.or(""));
        dialog.show();
    }

    public void showConnectDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Address")
                .customView(R.layout.dialog_address, false)
                .onPositive((dialog1, which) -> {
                    View parent = dialog1.getCustomView();
                    AppCompatEditText hostField = (AppCompatEditText) parent.findViewById(R.id.host);
                    AppCompatEditText portField = (AppCompatEditText) parent.findViewById(R.id.port);
                    String host = hostField.getText().toString().trim();
                    int port = Integer.valueOf(portField.getText().toString().trim());
                    context.getSettings().lastHost.set(host);
                    context.getSettings().lastPort.set(port);
                    serviceInterface.connect(new ServerAddress(host, port));
                })
                .positiveText("Connect")
                .neutralText("Cancel")
                .build();
        AppCompatEditText hostField = (AppCompatEditText) dialog.getView().findViewById(R.id.host);
        AppCompatEditText portField = (AppCompatEditText) dialog.getView().findViewById(R.id.port);

        dialog.setOnKeyListener(new DialogKeyboardUtil(dialog));
        hostField.setText(context.getSettings().lastHost.or(""));
        portField.setText(String.valueOf(context.getSettings().lastPort.or(4242)));

        dialog.show();
    }

    public void onEventMainThread(BacklogReceivedEvent event) {
        if (event.bufferId == status.bufferId) {
            swipeView.setRefreshing(false);
        }
    }

    public void onEventMainThread(GeneralErrorEvent event) {
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

    protected void setSubtitle(CharSequence subtitle) {
        this.subtitle = subtitle;
        updateSubTitle();
    }

    private void updateSubTitle() {
        if (context.getClient() != null) {
            toolbar.setSubtitle(SpanFormatter.format("Lag: %.2f, %s", context.getClient().getLag() / 1000.0F, subtitle));
        } else {
            toolbar.setSubtitle(subtitle);
        }
    }
}
