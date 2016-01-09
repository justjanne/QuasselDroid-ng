package de.kuschku.quasseldroid_ng;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Sets;
import com.mikepenz.fastadapter.ICollapsible;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.IProtocolHandler;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.events.StatusMessageEvent;
import de.kuschku.libquassel.exceptions.UnknownTypeException;
import de.kuschku.libquassel.functions.types.HandshakeFunction;
import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.objects.types.ClientLogin;
import de.kuschku.libquassel.primitives.types.Message;
import de.kuschku.libquassel.syncables.types.BufferViewConfig;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.quasseldroid_ng.utils.ServerAddress;
import de.kuschku.util.ObservableList;
import de.kuschku.util.Stream;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.messages)
    RecyclerView messages;

    @Bind(R.id.chatline)
    EditText chatline;

    @Bind(R.id.send)
    AppCompatImageButton send;

    Drawer drawer;
    AccountHeader header;
    MessageAdapter adapter = new MessageAdapter();

    QuasselService.LocalBinder binder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName cn, IBinder service) {
            if (service instanceof QuasselService.LocalBinder) {
                MainActivity.this.binder = (QuasselService.LocalBinder) service;
                if (binder.getBackgroundThread() != null) {
                    toolbar.setSubtitle(binder.getBackgroundThread().connection.getStatus().name());
                }
            }
        }

        public void onServiceDisconnected(ComponentName cn) {
        }
    };

    private IProtocolHandler handler;
    private int bufferId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        setTheme(R.style.AppTheme_Light);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // This fixes a horrible bug android has where opening the keyboard doesn’t resize the layout
        KeyboardUtil keyboardUtil = new KeyboardUtil(this, findViewById(R.id.layout));
        keyboardUtil.enable();

        startService(new Intent(this, QuasselService.class));
        bindService(new Intent(this, QuasselService.class), serviceConnection, BIND_AUTO_CREATE);

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.bg)
                .withSavedInstance(savedInstanceState)
                .withCompactStyle(true)
                .withProfileImagesVisible(false)
                .withOnAccountHeaderListener((view, profile, current) -> {
                    BufferViewConfig config = handler.getClient().getBufferViewManager().BufferViews.get(profile.getIdentifier());
                    ArrayList<IDrawerItem> items = new ArrayList<>();
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
                    drawer.setItems(items);
                    for (int i = 0; i < drawer.getAdapter().getItemCount(); i++) {
                        drawer.getAdapter().open(i);
                    }
                    return true;
                })
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem != null) {
                        if (position == -1) {
                            binder.stopBackgroundThread();
                            View coreview = View.inflate(this, R.layout.core_dialog, null);
                            new AlertDialog.Builder(this)
                                    .setView(coreview)
                                    .setPositiveButton("Connect", (dialog, which) -> {
                                        EditText hostname = ((EditText) coreview.findViewById(R.id.server));
                                        EditText port = ((EditText) coreview.findViewById(R.id.port));
                                        if (binder.getBackgroundThread() != null) binder.getBackgroundThread().provider.event.unregister(this);
                                        binder.stopBackgroundThread();
                                        BusProvider provider = new BusProvider();
                                        provider.event.register(this);
                                        binder.startBackgroundThread(provider, new ServerAddress(hostname.getText().toString().trim(), Integer.valueOf(port.getText().toString().trim())));
                                        handler = binder.getBackgroundThread().handler;
                                    })
                                    .setNegativeButton("Cancel", (dialog, which) -> {
                                    })
                                    .setTitle("Connect")
                                    .show();
                            return false;
                        } else {
                            if (((ICollapsible) drawerItem).getSubItems() != null) {
                                drawer.getAdapter().toggleCollapsible(position);
                            } else {
                                switchBuffer(drawer.getAdapter().getItem(position).getIdentifier());
                            }
                        }
                        return true;
                    }
                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("(Re-)Connect").withIcon(R.drawable.ic_server_light));

        messages.setAdapter(adapter);
        messages.setLayoutManager(new LinearLayoutManager(this));

        send.setOnClickListener((view) -> {
            Buffer buffer = handler.getClient().getBuffer(bufferId);
            handler.getClient().sendInput(buffer.getInfo(), chatline.getText().toString());
            chatline.setText("");
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
        ObservableList<Message> messageList = new ObservableList<>(Message.class);

        public void setMessageList(ObservableList<Message> messageList) {
            this.messageList.setCallback(null);
            this.messageList = messageList;
            this.messageList.setCallback(new ObservableList.RecyclerViewAdapterCallback(this));
            notifyDataSetChanged();
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MessageViewHolder(LayoutInflater.from(MainActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            holder.text1.setText(messageList.list.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return messageList.list.size();
        }
    };

    class MessageViewHolder extends RecyclerView.ViewHolder {
        @Bind(android.R.id.text1)
        TextView text1;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    public void onEventMainThread(ConnectionChangeEvent event) {
        switch (event.status) {
            case DISCONNECTED:
                binder.stopBackgroundThread();
                break;
            case CONNECTED:
                break;
            case LOGIN_REQUIRED:
                View loginview = View.inflate(this, R.layout.login_dialog, null);
                new AlertDialog.Builder(this)
                        .setView(loginview)
                        .setPositiveButton("Login", (dialog, which) -> {
                            binder.getBackgroundThread().provider.dispatch(new HandshakeFunction(new ClientLogin(
                                    ((EditText) loginview.findViewById(R.id.username)).getText().toString(),
                                    ((EditText) loginview.findViewById(R.id.password)).getText().toString()
                            )));
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            binder.stopBackgroundThread();
                        })
                        .setTitle("Login")
                        .show();
                break;
        }

        toolbar.setSubtitle(event.status.name());
    }

    public void onEventMainThread(BufferViewManagerChangedEvent event) {
        IProfile activeProfile = header.getActiveProfile();
        int selectedProfile = activeProfile == null ? -1 : activeProfile.getIdentifier();
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
        Collections.sort(header.getProfiles(), (x,y) -> x.getIdentifier() - y.getIdentifier());
        if (event.action == BufferViewManagerChangedEvent.Action.REMOVE && event.id == selectedProfile) {
            ArrayList<IProfile> profiles = header.getProfiles();
            if (!profiles.isEmpty())
                header.setActiveProfile(profiles.get(0), true);
        } else if (event.action == BufferViewManagerChangedEvent.Action.MODIFY && event.id == selectedProfile) {
            header.setActiveProfile(selectedProfile, true);
        }
    }

    public void onEventMainThread(StatusMessageEvent event) {
        Toast.makeText(this, String.format("%s: %s", event.scope, event.message), Toast.LENGTH_LONG).show();
    }

    public void onEventMainThread(GeneralErrorEvent event) {
        if (event.exception != null && !(event.exception instanceof UnknownTypeException)) {
            Log.e("libquassel", event.toString());
            Snackbar.make(messages, event.toString(), Snackbar.LENGTH_LONG).show();
        }
    }
}
