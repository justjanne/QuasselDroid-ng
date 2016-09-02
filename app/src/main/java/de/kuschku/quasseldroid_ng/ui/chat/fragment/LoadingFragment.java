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

package de.kuschku.quasseldroid_ng.ui.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.events.BacklogInitEvent;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.InitEvent;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.servicebound.BoundFragment;

public class LoadingFragment extends BoundFragment {
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.label)
    TextView label;

    @Bind(R.id.count)
    TextView count;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        ButterKnife.bind(this, view);


        label.setText(context.themeUtil().translations.statusConnecting);
        showProgressState(1);

        return view;
    }

    public void showProgressState(int position) {
        count.setText(String.format(Locale.US, "%d/%d", position, 5));
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(ConnectionChangeEvent event) {
        progressBar.setIndeterminate(true);

        label.setText(context.themeUtil().statusName(event.status));
        showProgressState(event.status.ordinal() + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(InitEvent event) {
        if (context.client().connectionStatus() == ConnectionChangeEvent.Status.INITIALIZING_DATA) {
            progressBar.setIndeterminate(false);
            progressBar.setMax(event.max);
            progressBar.setProgress(event.loaded);

            label.setText(context.themeUtil().translations.statusInitData);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(BacklogInitEvent event) {
        if (context.client().connectionStatus() == ConnectionChangeEvent.Status.LOADING_BACKLOG) {
            progressBar.setIndeterminate(false);
            progressBar.setMax(event.max);
            progressBar.setProgress(event.loaded);

            label.setText(context.themeUtil().translations.statusBacklog);
        }
    }
}
