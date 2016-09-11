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

package de.kuschku.quasseldroid_ng.ui.coresettings.aliases;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.syncables.types.impl.AliasManager;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.aliases.helper.AliasSerializerHelper;
import de.kuschku.util.servicebound.BoundActivity;

public class AliasEditActivity extends BoundActivity {
    public static final int RESULT_DELETE = -2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.name)
    EditText name;

    @Bind(R.id.expansion)
    EditText expansion;

    private AliasManager.Alias item;
    private Bundle original;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alias_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            original = intent.getBundleExtra("alias");
            if (original != null) {
                item = AliasSerializerHelper.deserialize(original);

                name.setText(item.name);
                expansion.setText(item.expansion.replaceAll("; ?", "\n"));
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
                        .content(getString(R.string.confirmationDelete, this.item.name))
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
        intent.putExtra("original", original.getString("name"));
        setResult(RESULT_DELETE, intent);
    }

    private void save() {
        Intent intent = new Intent();
        if (original != null)
            intent.putExtra("original", original.getString("name"));
        intent.putExtra("alias", build());
        setResult(RESULT_OK, intent);
    }

    private boolean hasChanged(Bundle bundle) {
        return this.original != null && bundle != null && !this.original.equals(bundle);
    }

    private Bundle build() {
        Bundle bundle = new Bundle();
        bundle.putString("name", name.getText().toString());
        bundle.putString("expansion", expansion.getText().toString().replace("\n", "; "));
        return bundle;
    }
}
