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

package de.kuschku.quasseldroid_ng.ui.coresettings.identity.nick;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.OnStartDragListener;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.SimpleItemTouchHelperCallback;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.observables.lists.ObservableList;
import de.kuschku.util.servicebound.BoundActivity;

public class IdentityNickListActivity extends BoundActivity implements OnStartDragListener {

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.add)
    FloatingActionButton add;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    IdentityNickAdapter adapter;
    ItemTouchHelper itemTouchHelper;
    ObservableList<String> nicks;
    OnIdentityNickClickListener clickListener = nick -> {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .input("", nick, false, (dialog1, input) -> {

                })
                .positiveText("Save")
                .negativeText("Cancel")
                .neutralText("Delete")
                .positiveColor(context.themeUtil().res.colorAccent)
                .negativeColor(context.themeUtil().res.colorForeground)
                .neutralColor(context.themeUtil().res.colorForeground)
                .onPositive((dialog1, which) -> {
                    String text = dialog1.getInputEditText().getText().toString().trim();
                    nicks.set(nicks.indexOf(nick), text);
                })
                .onNeutral((dialog1, which) -> nicks.remove(nick))
                .build();
        dialog.show();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        ArrayList<String> nickList;
        if (intent != null && (nickList = intent.getStringArrayListExtra("nicks")) != null) {
            nicks = new ObservableList<>(nickList);
        } else {
            nicks = new ObservableList<>();
        }

        adapter = new IdentityNickAdapter(nicks, this);
        nicks.addCallback(new AdapterUICallbackWrapper(adapter));

        list.setAdapter(adapter);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(clickListener);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(list);

        add.setOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .input("", "", false, (dialog1, input) -> {

                    })
                    .positiveText("Save")
                    .negativeText("Cancel")
                    .positiveColor(context.themeUtil().res.colorAccent)
                    .negativeColor(context.themeUtil().res.colorForeground)
                    .onPositive((dialog1, which) -> {
                        String nick = dialog1.getInputEditText().getText().toString().trim();
                        if (!nicks.contains(nick))
                            nicks.add(nick);
                    })
                    .build();
            dialog.show();
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm: {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("nicks", nicks);
                setResult(RESULT_OK, intent);
                finish();
            } return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    interface OnIdentityNickClickListener {
        void onClick(String nick);
    }
}
