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

package de.kuschku.quasseldroid_ng.ui.coresettings.identity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.syncables.types.impl.Identity;
import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.NetworkServerListActivity;
import de.kuschku.util.servicebound.BoundActivity;
import de.kuschku.util.ui.AnimationHelper;

public class IdentityCreateActivity extends BoundActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.identityName)
    EditText identityName;

    @Bind(R.id.realName)
    EditText realName;

    @Bind(R.id.ident)
    EditText ident;

    @Bind(R.id.nicks)
    Button nicks;

    @Bind(R.id.kickReason)
    EditText kickReason;

    @Bind(R.id.partReason)
    EditText partReason;

    @Bind(R.id.quitReason)
    EditText quitReason;

    @Bind(R.id.awayReason)
    EditText awayReason;

    @Bind(R.id.useAwayOnDetach)
    SwitchCompat useAwayOnDetach;

    @Bind(R.id.groupAwayOnDetach)
    ViewGroup groupAwayOnDetach;

    @Bind(R.id.awayOnDetachReason)
    EditText awayOnDetachReason;

    private ArrayList<String> nickList;
    private QIdentity identity = Identity.createDefault();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_identity_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        useAwayOnDetach.setOnCheckedChangeListener(this::updateAwayOnDetachReasonVisible);
        updateAwayOnDetachReasonVisible(null, useAwayOnDetach.isChecked());

        nicks.setOnClickListener(v -> {
            Intent intent1 = new Intent(IdentityCreateActivity.this, NetworkServerListActivity.class);
            intent1.putStringArrayListExtra("nicks", nickList);
            startActivityForResult(intent1, 0, null);
        });

        initializeDefaults();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<String> nickList = data.getStringArrayListExtra("nicks");
        if (nickList != null)
            this.nickList = nickList;
    }

    private void updateAwayOnDetachReasonVisible(CompoundButton button, boolean visible) {
        awayOnDetachReason.setEnabled(visible);

        IdentityCreateActivity.this.updateViewGroupStatus(groupAwayOnDetach, visible);
    }

    private void updateViewGroupStatus(ViewGroup group, boolean visible) {
        if (visible) {
            AnimationHelper.expand(group);
        } else {
            AnimationHelper.collapse(group);
        }
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
                if (identity != null) {
                    Identity newIdentity = Identity.createDefault();
                    newIdentity._copyFrom(identity);

                    if (!identity.identityName().equals(identityName.getText().toString()))
                        newIdentity._setIdentityName(identityName.getText().toString());

                    if (!identity.realName().equals(realName.getText().toString()))
                        newIdentity._setRealName(realName.getText().toString());

                    if (!identity.ident().equals(ident.getText().toString()))
                        newIdentity._setIdent(ident.getText().toString());

                    if (!identity.nicks().equals(nickList))
                        newIdentity._setNicks(nickList);

                    if (!identity.kickReason().equals(kickReason.getText().toString()))
                        newIdentity._setKickReason(kickReason.getText().toString());

                    if (!identity.partReason().equals(partReason.getText().toString()))
                        newIdentity._setPartReason(partReason.getText().toString());

                    if (!identity.quitReason().equals(quitReason.getText().toString()))
                        newIdentity._setQuitReason(quitReason.getText().toString());

                    if (!identity.awayReason().equals(awayReason.getText().toString()))
                        newIdentity._setAwayReason(awayReason.getText().toString());

                    if (!identity.detachAwayEnabled() == useAwayOnDetach.isChecked())
                        newIdentity.setDetachAwayEnabled(useAwayOnDetach.isChecked());

                    if (!identity.detachAwayReason().equals(awayOnDetachReason.getText().toString()))
                        newIdentity._setDetachAwayReason(awayOnDetachReason.getText().toString());

                    context.client().createIdentity(newIdentity);

                    finish();
                }
            } return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeDefaults() {
        this.identityName.setText(identity.identityName());
        this.realName.setText(identity.realName());
        this.ident.setText(identity.ident());
        this.nickList = new ArrayList<>(identity.nicks());
        this.kickReason.setText(identity.kickReason());
        this.partReason.setText(identity.partReason());
        this.quitReason.setText(identity.quitReason());
        this.awayReason.setText(identity.awayReason());
        this.useAwayOnDetach.setChecked(identity.detachAwayEnabled());
        this.awayOnDetachReason.setText(identity.detachAwayReason());
    }
}
