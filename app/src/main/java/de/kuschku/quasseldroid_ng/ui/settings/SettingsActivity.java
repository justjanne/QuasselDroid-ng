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

package de.kuschku.quasseldroid_ng.ui.settings;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.chat.util.ServiceHelper;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.quasseldroid_ng.ui.theme.AppTheme;

public class SettingsActivity extends AppCompatActivity {

    AppContext context = new AppContext();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    int themeid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        themeid = ServiceHelper.initTheme(context, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setPreferenceFragment(new AppearanceFragment());

        context.settings().preferenceTheme.addChangeListener(value -> restart());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (themeid != AppTheme.resFromString(context.settings().preferenceTheme.get()))
            restart();
    }

    public void restart() {
        startActivity(getIntent());
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void setPreferenceFragment(PreferenceFragment preferenceFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_host, preferenceFragment);
        transaction.commit();
    }

    public static class AppearanceFragment extends PreferenceFragment {
        AppContext context = new AppContext();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            ServiceHelper.initTheme(context, getActivity());
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_appearance);
        }
    }
}
