package de.kuschku.quasseldroid_ng.ui.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.IProtocolHandler;
import de.kuschku.libquassel.events.BacklogReceivedEvent;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.events.StatusMessageEvent;
import de.kuschku.libquassel.exceptions.UnknownTypeException;
import de.kuschku.libquassel.functions.types.HandshakeFunction;
import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.objects.types.ClientLogin;
import de.kuschku.libquassel.syncables.types.BufferViewConfig;
import de.kuschku.libquassel.syncables.types.BufferViewManager;
import de.kuschku.libquassel.events.BufferViewManagerChangedEvent;
import de.kuschku.quasseldroid_ng.BuildConfig;
import de.kuschku.quasseldroid_ng.service.QuasselService;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.chat.chatview.MessageAdapter;
import de.kuschku.quasseldroid_ng.ui.chat.drawer.BufferWrapper;
import de.kuschku.quasseldroid_ng.ui.chat.drawer.NetworkWrapper;
import de.kuschku.quasseldroid_ng.ui.coresetup.CoreSetupActivity;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.lists.IObservableList;
import de.kuschku.util.observables.callbacks.UIChildCallback;
import de.kuschku.util.observables.callbacks.UIParentCallback;
import de.kuschku.util.ui.CompatibilityUtils;
import de.kuschku.util.ServerAddress;
import de.kuschku.util.ui.parcelableUtil.ClassLoaderUtils;
import de.kuschku.util.ui.parcelableUtil.StorageBackendParcelable;
import de.kuschku.util.observables.AutoScroller;

public class MainActivity extends AppCompatActivity {
    private static final String BUFFER_ID = "BUFFER_ID";
    private static final String BUFFER_VIEW_ID = "BUFFER_VIEW_ID";

    private static final String KEY_HOST = "beta_hostname";
    private static final String KEY_PORT = "beta_port";
    private static final String KEY_USER = "beta_username";
    private static final String KEY_PASS = "beta_password";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.messages)
    RecyclerView messages;

    @Bind(R.id.chatline)
    EditText chatline;

    @Bind(R.id.send)
    AppCompatImageButton send;

    @Bind(R.id.swipe_view)
    SwipeRefreshLayout swipeView;

    //@Bind(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;

    @Bind(R.id.msg_history)
    RecyclerView msgHistory;

    Drawer drawer;
    AccountHeader header;
    MessageAdapter adapter;

    SharedPreferences pref;
    QuasselService.LocalBinder binder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName cn, IBinder service) {
            if (service instanceof QuasselService.LocalBinder) {
                MainActivity.this.binder = (QuasselService.LocalBinder) service;
                if (binder.getBackgroundThread() != null) {
                    handler = binder.getBackgroundThread().handler;
                    provider = binder.getBackgroundThread().provider;
                    provider.event.register(MainActivity.this);

                    toolbar.setSubtitle(binder.getBackgroundThread().connection.getStatus().name());
                    if (bufferId != -1) switchBuffer(bufferId);
                    if (bufferViewId != -1) switchBufferView(bufferViewId);

                    // Horrible hack to load bufferviews back, should use ObservableSortedList
                    Client client = handler == null ? null : handler.getClient();
                    BufferViewManager bufferViewManager = client == null ? null : client.getBufferViewManager();
                    Map<Integer, BufferViewConfig> bufferViews = bufferViewManager == null ? null : bufferViewManager.BufferViews;
                    if (bufferViews != null)
                    for (int id : bufferViews.keySet()) {
                        onEventMainThread(new BufferViewManagerChangedEvent(id, BufferViewManagerChangedEvent.Action.ADD));
                    }
                }
            }
        }

        public void onServiceDisconnected(ComponentName cn) {
        }
    };

    private IProtocolHandler handler;
    private BusProvider provider;

    private int bufferId;
    private int bufferViewId;

    private ItemAdapter<IDrawerItem> hackyHistoryAdapter = new ItemAdapter<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

        // TODO: ADD THEME SELECTION
        setTheme(R.style.Quassel);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // This fixes a horrible bug android has where opening the keyboard doesn’t resize the layout
        KeyboardUtil keyboardUtil = new KeyboardUtil(this, slidingLayout);
        keyboardUtil.enable();

        initServive();

        initDrawer(savedInstanceState);

        initMessageList();

        initSendCallbacks();

        initMsgHistory();
    }

    private void initServive() {
        startService(new Intent(this, QuasselService.class));
        bindService(new Intent(this, QuasselService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    private void initDrawer(Bundle savedInstanceState) {
        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.bg)
                .withSavedInstance(savedInstanceState)
                .withCompactStyle(true)
                .withProfileImagesVisible(false)
                .withOnAccountHeaderListener((view, profile, current) -> {
                    switchBufferView(profile.getIdentifier());
                    return true;
                })
                .build();

        RecyclerView rView = new RecyclerView(this);
        rView.setLayoutManager(new LinearLayoutManager(this));
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setAdapter(new MyAdapter(handler.getClient().getNetworkList()));

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .withCustomView(rView)
                .build();

        // TODO: REWRITE THIS
        if (CompatibilityUtils.deviceSupportsKeepAlive()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }
        }

        drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("(Re-)Connect").withIcon(R.drawable.ic_server_light));
    }

    private void reconnect() {
        binder.stopBackgroundThread();
        View coreview = View.inflate(this, R.layout.core_dialog, null);
        EditText hostname = ((EditText) coreview.findViewById(R.id.server));
        EditText port = ((EditText) coreview.findViewById(R.id.port));

        hostname.setText(pref.getString(KEY_HOST, ""));
        port.setText(String.valueOf(pref.getInt(KEY_PORT, 4242)));
        new AlertDialog.Builder(this)
                .setView(coreview)
                .setPositiveButton("Connect", (dialog, which) -> {
                    if (provider != null) provider.event.unregister(this);
                    binder.stopBackgroundThread();
                    provider = new BusProvider();
                    provider.event.register(this);

                    String value_hostname = hostname.getText().toString().trim();
                    Integer value_port = Integer.valueOf(port.getText().toString().trim());

                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(KEY_HOST, value_hostname);
                    edit.putInt(KEY_PORT, value_port);
                    edit.commit();

                    binder.startBackgroundThread(provider, new ServerAddress(value_hostname, value_port));
                    handler = binder.getBackgroundThread().handler;
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .setTitle("Connect")
                .show();
    }

    private void initSendCallbacks() {
        send.setOnClickListener(view -> sendInput());
        chatline.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))
                sendInput();

            return false;
        });
    }

    private void initMsgHistory() {
        slidingLayout.setScrollableView(msgHistory);
        FastAdapter<IDrawerItem> adapter = new FastAdapter<>();
        hackyHistoryAdapter.wrap(adapter);
        msgHistory.setAdapter(adapter);
        msgHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initMessageList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        adapter = new MessageAdapter(this, new AutoScroller(messages, layoutManager));
        messages.setAdapter(adapter);
        messages.setLayoutManager(layoutManager);
        swipeView.setOnRefreshListener(() -> {
            if (handler != null) handler.getClient().getBacklogManager().requestMoreBacklog(bufferId, 20);
            else swipeView.setRefreshing(false);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUFFER_ID, bufferId);
        outState.putInt(BUFFER_VIEW_ID, bufferViewId);
        drawer.saveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            bufferId = savedInstanceState.getInt(BUFFER_ID, -1);
            bufferViewId = savedInstanceState.getInt(BUFFER_VIEW_ID, -1);
        }
    }

    // TODO: USE OBSERVABLELIST FOR THIS
    private void switchBufferView(int bufferviewId) {
        /*
        this.bufferViewId = bufferviewId;
        adapter.setClient(handler.getClient());
        BufferViewConfig config = handler.getClient().getBufferViewManager().BufferViews.get(bufferviewId);
        ArrayList<IDrawerItem> items = new ArrayList<>();
        if (config != null) {
            if (config.getNetworkId() == 0) {
                items.addAll(
                        new Stream<>(handler.getClient().getNetworks())
                                .map(network -> new NetworkDrawerItem(network,
                                        Sets.intersection(network.getBuffers(), new HashSet<>(
                                                new Stream<>(config.getBufferList())
                                                        .map(handler.getClient()::getBuffer)
                                                        .list()
                                        ))))
                                .list()
                );
            } else {
                Network network = handler.getClient().getNetwork(config.getNetworkId());
                items.add(new NetworkDrawerItem(network,
                        Sets.intersection(network.getBuffers(), new HashSet<>(
                                new Stream<>(config.getBufferList())
                                        .map(handler.getClient()::getBuffer)
                                        .list()
                        ))
                ));
            }
        }
        drawer.setItems(items);
        for (int i = 0; i < drawer.getAdapter().getItemCount(); i++) {
            drawer.getAdapter().open(i);
        }
        */
    }


    // TODO: REWRITE THIS
    private void switchBuffer(int bufferId) {
        this.bufferId = bufferId;

        Buffer buffer = handler.getClient().getBuffer(this.bufferId);
        adapter.setMessageList(handler.getClient().getBacklogManager().get(this.bufferId));
        if (buffer == null) {
            toolbar.setTitle(R.string.app_name);
        } else {
            toolbar.setTitle(buffer.getName());
        }

        drawer.setSelection(this.bufferId, false);
        drawer.closeDrawer();
    }

    // TODO: REWRITE THIS
    private void sendInput() {
        Buffer buffer = null;
        Client client = null;
        if (handler != null) client = handler.getClient();
        if (client != null) buffer = client.getBuffer(bufferId);

        String str = chatline.getText().toString();
        hackyHistoryAdapter.add(new PrimaryDrawerItem().withName(str));
        if (buffer != null && !str.isEmpty())  handler.getClient().sendInput(buffer.getInfo(), str);
        chatline.setText("");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    // TODO: REWRITE THIS
    public void onEventMainThread(ConnectionChangeEvent event) {
        switch (event.status) {
            case DISCONNECTED:
                binder.stopBackgroundThread();
                break;
            case CONNECTED:
                // TODO: COMMENT THIS
                System.gc();
                if (bufferViewId == -1 && header.getProfiles().size() > 0)
                    switchBufferView(header.getProfiles().get(0).getIdentifier());
                break;
            case LOGIN_REQUIRED:
                showLoginDialog();
                break;
            case CORE_SETUP_REQUIRED:
                showCoreSetupDialog();
                break;
        }

        toolbar.setSubtitle(event.status.name());
    }

    private void showCoreSetupDialog() {
        ClassLoaderUtils.loader = Thread.currentThread().getContextClassLoader();
        Intent doCoreSetup = new Intent(this, CoreSetupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("backends", StorageBackendParcelable.wrap(handler.getClient().getCore().StorageBackends));
        doCoreSetup.putExtra("bundle", bundle);
        startActivityForResult(doCoreSetup, 0);
    }

    private void showLoginDialog() {
        View loginview = View.inflate(this, R.layout.login_dialog, null);
        EditText username = ((EditText) loginview.findViewById(R.id.username));
        EditText password = ((EditText) loginview.findViewById(R.id.password));
        username.setText(pref.getString(KEY_USER, ""));
        password.setText(pref.getString(KEY_PASS, ""));
        new AlertDialog.Builder(this)
                .setView(loginview)
                .setPositiveButton("Login", (dialog, which) -> {
                    String value_user = username.getText().toString();
                    String value_pass = password.getText().toString();

                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(KEY_USER, value_user);
                    edit.putString(KEY_PASS, value_pass);
                    edit.commit();

                    binder.getBackgroundThread().provider.dispatch(new HandshakeFunction(new ClientLogin(
                            value_user,
                            value_pass
                    )));
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    binder.stopBackgroundThread();
                })
                .setTitle("Login")
                .show();
    }

    // TODO: USE OBSERVABLE LIST FOR THIS SHIT
    public void onEventMainThread(BufferViewManagerChangedEvent event) {
        switch (event.action) {
            case ADD:
                BufferViewConfig add = handler.getClient().getBufferViewManager().BufferViews.get(event.id);
                header.addProfiles(new ProfileDrawerItem()
                        .withName(add.getBufferViewName())
                        .withIdentifier(event.id)
                );
                break;
            case REMOVE:
                header.removeProfileByIdentifier(event.id);
                break;
            case MODIFY:
                BufferViewConfig modify = handler.getClient().getBufferViewManager().BufferViews.get(event.id);
                header.removeProfileByIdentifier(event.id);
                header.addProfiles(new ProfileDrawerItem()
                        .withName(modify.getBufferViewName())
                        .withIdentifier(event.id)
                );
                break;
        }
        Collections.sort(header.getProfiles(), (x, y) -> x.getIdentifier() - y.getIdentifier());
        if (event.action == BufferViewManagerChangedEvent.Action.REMOVE && event.id == bufferViewId) {
            ArrayList<IProfile> profiles = header.getProfiles();
            if (!profiles.isEmpty())
                header.setActiveProfile(profiles.get(0), true);
        } else if (event.action == BufferViewManagerChangedEvent.Action.MODIFY && event.id == bufferViewId) {
            header.setActiveProfile(bufferViewId, true);
        }
    }

    public void onEventMainThread(BacklogReceivedEvent event) {
        if (event.bufferId == bufferId) swipeView.setRefreshing(false);
    }

    // TODO: REWRITE THIS
    public void onEventMainThread(StatusMessageEvent event) {
        Toast.makeText(this, String.format("%s: %s", event.scope, event.message), Toast.LENGTH_LONG).show();
    }

    // TODO: REWRITE THIS
    public void onEventMainThread(GeneralErrorEvent event) {
        if (event.exception != null && !(event.exception instanceof UnknownTypeException)) {
            event.exception.printStackTrace();
            Snackbar.make(messages, event.toString(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class MyAdapter extends ExpandableRecyclerAdapter<NetworkWrapper.ViewHolder, BufferWrapper.ViewHolder> implements UIParentCallback, UIChildCallback {
        int primaryRes = new PrimaryDrawerItem().getLayoutRes();
        int secondaryRes = new SecondaryDrawerItem().getLayoutRes();

        public MyAdapter(@NonNull IObservableList<UICallback, NetworkWrapper> wrapperList) {
            super(wrapperList);
        }

        @Override
        public NetworkWrapper.ViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
            return new NetworkWrapper.ViewHolder(LayoutInflater.from(parentViewGroup.getContext()).inflate(primaryRes, parentViewGroup, false));
        }

        @Override
        public BufferWrapper.ViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
            return new BufferWrapper.ViewHolder(LayoutInflater.from(childViewGroup.getContext()).inflate(secondaryRes, childViewGroup, false));
        }

        @Override
        public void onBindParentViewHolder(NetworkWrapper.ViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
            parentViewHolder.bind((NetworkWrapper) parentListItem);
        }

        @Override
        public void onBindChildViewHolder(BufferWrapper.ViewHolder childViewHolder, int position, Object childListItem) {
            childViewHolder.bind((BufferWrapper) childListItem);
        }
    }
}
