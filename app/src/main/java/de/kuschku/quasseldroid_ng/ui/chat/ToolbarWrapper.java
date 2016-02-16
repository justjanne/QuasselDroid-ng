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

package de.kuschku.quasseldroid_ng.ui.chat;

import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import de.kuschku.quasseldroid_ng.R;

public class ToolbarWrapper {
    private final TextView title;
    private final TextView subtitle;
    private final View actionArea;

    public ToolbarWrapper(Toolbar toolbar) {
        this.title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        this.subtitle = (TextView) toolbar.findViewById(R.id.toolbar_subtitle);
        this.actionArea = toolbar.findViewById(R.id.toolbar_action_area);
    }

    public void setTitle(@StringRes int id) {
        title.setText(id);
    }

    public void setTitle(CharSequence text) {
        title.setText(text);
    }

    public void setSubtitle(@StringRes int id) {
        subtitle.setText(id);
    }

    public void setSubtitle(CharSequence text) {
        subtitle.setText(text);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        actionArea.setOnClickListener(listener);
    }
}
