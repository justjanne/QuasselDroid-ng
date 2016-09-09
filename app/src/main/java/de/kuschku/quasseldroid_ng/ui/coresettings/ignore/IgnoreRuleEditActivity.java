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

package de.kuschku.quasseldroid_ng.ui.coresettings.ignore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.syncables.types.impl.IgnoreListManager;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.ignore.helper.IgnoreRuleSerializerHelper;
import de.kuschku.quasseldroid_ng.ui.coresettings.ignore.helper.IgnoreTypeAdapter;
import de.kuschku.quasseldroid_ng.ui.coresettings.ignore.helper.ScopeTypeAdapter;
import de.kuschku.quasseldroid_ng.ui.coresettings.ignore.helper.StrictnessTypeAdapter;
import de.kuschku.util.servicebound.BoundActivity;

public class IgnoreRuleEditActivity extends BoundActivity {
    public static final int RESULT_DELETE = -2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.strictness)
    AppCompatSpinner strictness;

    @Bind(R.id.type)
    AppCompatSpinner type;

    @Bind(R.id.ignoreRule)
    EditText ignoreRule;

    @Bind(R.id.isRegEx)
    CheckBox isRegEx;

    @Bind(R.id.scopeType)
    AppCompatSpinner scopeType;

    @Bind(R.id.scope)
    EditText scope;

    private IgnoreListManager.IgnoreListItem item;
    private Bundle original;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ignorerule_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StrictnessTypeAdapter strictnessTypeAdapter = new StrictnessTypeAdapter(context);
        strictness.setAdapter(strictnessTypeAdapter);

        IgnoreTypeAdapter ignoreTypeAdapter = new IgnoreTypeAdapter(context);
        type.setAdapter(ignoreTypeAdapter);

        ScopeTypeAdapter scopeTypeAdapter = new ScopeTypeAdapter(context);
        scopeType.setAdapter(scopeTypeAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            original = intent.getBundleExtra("rule");
            if (original != null) {
                item = IgnoreRuleSerializerHelper.deserialize(original);

                strictness.setSelection(strictnessTypeAdapter.indexOf(item.getStrictness()));
                type.setSelection(ignoreTypeAdapter.indexOf(item.getType()));
                ignoreRule.setText(item.getIgnoreRule().rule());
                isRegEx.setChecked(item.isRegEx());
                scopeType.setSelection(scopeTypeAdapter.indexOf(item.getScope()));
                scope.setText(item.getScopeRule());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (original != null) {
            getMenuInflater().inflate(R.menu.confirm_delete, menu);
        } else {
            getMenuInflater().inflate(R.menu.confirm, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (hasChanged(build())) {
            new MaterialDialog.Builder(this)
                    .content(R.string.confirmationUnsavedChanges)
                    .positiveText(R.string.actionYes)
                    .negativeText(R.string.actionNo)
                    .positiveColor(context.themeUtil().res.colorAccent)
                    .negativeColor(context.themeUtil().res.colorForeground)
                    .backgroundColorAttr(R.attr.colorBackgroundDialog)
                    .onPositive((dialog, which) -> {
                        save();
                        super.onBackPressed();
                    })
                    .onNegative((dialog, which) -> super.onBackPressed())
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete: {
                new MaterialDialog.Builder(this)
                        .content(getString(R.string.confirmationDelete, this.item.getIgnoreRule().rule()))
                        .positiveText(R.string.actionYes)
                        .negativeText(R.string.actionNo)
                        .positiveColor(context.themeUtil().res.colorAccent)
                        .negativeColor(context.themeUtil().res.colorForeground)
                        .backgroundColorAttr(R.attr.colorBackgroundDialog)
                        .onPositive((dialog, which) -> {
                            delete();
                            finish();
                        })
                        .build()
                        .show();
            }
            return true;
            case R.id.action_confirm: {
                save();
                finish();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void delete() {
        Intent intent = new Intent();
        intent.putExtra("original", original.getString("ignoreRule"));
        setResult(RESULT_DELETE, intent);
    }

    private void save() {
        Intent intent = new Intent();
        if (original != null)
            intent.putExtra("original", original.getString("ignoreRule"));
        intent.putExtra("rule", build());
        setResult(RESULT_OK, intent);
    }

    private boolean hasChanged(Bundle bundle) {
        return this.original != null && bundle != null && !this.original.equals(bundle);
    }

    private Bundle build() {
        Bundle bundle = new Bundle();
        bundle.putInt("strictness", (int) strictness.getSelectedItemId());
        bundle.putInt("type", (int) type.getSelectedItemId());
        bundle.putString("ignoreRule", ignoreRule.getText().toString());
        bundle.putBoolean("isRegEx", isRegEx.isChecked());
        bundle.putInt("scopeType", (int) scopeType.getSelectedItemId());
        bundle.putString("scope", scope.getText().toString());
        return bundle;
    }
}
