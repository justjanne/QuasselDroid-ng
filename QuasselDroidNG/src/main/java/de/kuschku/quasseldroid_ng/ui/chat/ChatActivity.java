package de.kuschku.quasseldroid_ng.ui.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.materialize.util.UIUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.BuildConfig;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.service.ClientBackgroundThread;
import de.kuschku.quasseldroid_ng.service.QuasselService;
import de.kuschku.util.DrawerUtils;
import de.kuschku.util.instancestateutil.Storable;
import de.kuschku.util.instancestateutil.Store;

@UiThread
public class ChatActivity extends AppCompatActivity {
    @Bind(R.id.drawer_left)
    DrawerLayout drawerLeft;

    @Bind(R.id.navigation_left)
    NavigationView navigationLeft;

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

    @Bind(R.id.navigation_header_container)
    RelativeLayout navigationHeaderContainer;

    @Bind(R.id.buffer_view_spinner)
    AppCompatSpinner bufferViewSpinner;

    private QuasselService.LocalBinder binder;
    private ClientBackgroundThread backgroundThread;

    private SharedPreferences preferences;

    private Status status = new Status();
    private static class Status extends Storable {
        @Store public int bufferId = -1;
        @Store public int bufferViewConfigId = -1;
    }

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
            backgroundThread = null;
            binder = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        preferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        setTheme(R.style.Quassel);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DrawerUtils.initDrawer(this, drawerLeft, toolbar, R.string.open_drawer, R.string.close_drawer);

        ViewGroup.LayoutParams lp = navigationHeaderContainer.getLayoutParams();
        lp.height = lp.height + UIUtils.getStatusBarHeight(this);
        navigationHeaderContainer.setLayoutParams(lp);

        messages.setLayoutManager(new LinearLayoutManager(this));
        messages.setItemAnimator(new DefaultItemAnimator());
        messages.setAdapter(new FastAdapter<>());

        msgHistory.setLayoutManager(new LinearLayoutManager(this));
        msgHistory.setItemAnimator(new DefaultItemAnimator());
        msgHistory.setAdapter(new FastAdapter<>());

        bufferViewSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.md_simplelist_item, android.R.id.title, new String[]{"All Chats", "Queries", "Highlights"}));
        bufferViewSpinner.setPopupBackgroundResource(R.drawable.popup_background_material);
        bufferViewSpinner.setSelection(0);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        status.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        status.onSaveInstanceState(outState);
    }

    private void connectToThread(@NonNull ClientBackgroundThread backgroundThread) {
        if (this.backgroundThread != null) backgroundThread.provider.event.unregister(this);

        this.backgroundThread = backgroundThread;
        backgroundThread.provider.event.register(this);
        selectBuffer(status.bufferId);
        selectBufferViewConfig(status.bufferViewConfigId);
    }

    private void selectBufferViewConfig(@IntRange(from=-1) int bufferViewConfigId) {

    }

    private void selectBuffer(@IntRange(from=-1) int bufferId) {

    }
}
