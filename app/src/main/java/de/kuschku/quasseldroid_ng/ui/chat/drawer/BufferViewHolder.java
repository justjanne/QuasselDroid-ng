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

package de.kuschku.quasseldroid_ng.ui.chat.drawer;

import android.databinding.Observable;
import android.databinding.ObservableField;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.mikepenz.materialize.util.UIUtils;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.events.BufferChangeEvent;
import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.localtypes.buffers.ChannelBuffer;
import de.kuschku.libquassel.localtypes.buffers.QueryBuffer;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;

public class BufferViewHolder extends ChildViewHolder {

    public int id;
    @Bind(R.id.material_drawer_icon)
    ImageView icon;
    @Bind(R.id.material_drawer_badge)
    TextView badge;
    @Bind(R.id.material_drawer_badge_container)
    LinearLayout badgeContainer;
    @Bind(R.id.material_drawer_name)
    TextView name;
    @Bind(R.id.material_drawer_description)
    TextView description;
    private ObservableField<BufferInfo.BufferStatus> status;
    private Observable.OnPropertyChangedCallback callback;
    private AppContext context;

    private StateListDrawable background;

    public BufferViewHolder(AppContext context, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        context.provider().event.registerSticky(this);

        background = new StateListDrawable();
        background.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(context.themeUtil().res.colorSelected));
        background.addState(new int[0], UIUtils.getSelectableBackground(itemView.getContext()));
    }

    @LayoutRes
    public static int layout() {
        return R.layout.widget_buffer;
    }

    public void bind(OnBufferClickListener listener, Buffer buffer) {
        if (status != null)
            status.removeOnPropertyChangedCallback(callback);
        status = buffer.getStatus();
        name.setText(buffer.getName());
        setDescription(context.deserializer().formatString(getDescription(buffer)));
        setBadge(0);
        itemView.setOnClickListener(v -> listener.onClick(buffer));

        itemView.setBackground(background);

        id = buffer.getInfo().id();

        BufferInfo.Type type = buffer.getInfo().type();
        setIcon(context, type, status);
        callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                setIcon(context, type, status);
            }
        };
        status.addOnPropertyChangedCallback(callback);

        setSelected();
    }

    private void setSelected() {
        setSelected(context.client().backlogManager().open() == id);
    }

    public void setSelected(boolean selected) {
        itemView.setSelected(selected);
    }

    private void setIcon(AppContext context, BufferInfo.Type type, ObservableField<BufferInfo.BufferStatus> status) {
        icon.setImageDrawable(context.themeUtil().statusDrawables.of(type, status.get()));
    }

    private void setDescription(@Nullable CharSequence description) {
        if (description == null || description.length() == 0) {
            this.description.setText(null);
            this.description.setVisibility(View.GONE);
        } else {
            this.description.setText(description);
            this.description.setVisibility(View.VISIBLE);
        }
    }

    private void setBadge(int count) {
        if (count == 0) {
            badgeContainer.setVisibility(View.GONE);
            badge.setText("0");
        } else {
            badgeContainer.setVisibility(View.VISIBLE);
            badge.setText(String.format(Locale.US, "%d", count));
        }
    }

    @Nullable
    private String getDescription(Buffer buffer) {
        if (buffer instanceof ChannelBuffer) {
            ChannelBuffer channelBuffer = (ChannelBuffer) buffer;
            QIrcChannel channel = channelBuffer.getChannel();
            if (channel != null) {
                return channel.topic();
            }
        } else if (buffer instanceof QueryBuffer) {
            QueryBuffer queryBuffer = (QueryBuffer) buffer;
            QIrcUser user = queryBuffer.getUser();
            if (user != null) {
                return user.realName();
            }
        }
        return null;
    }

    public void onEventMainThread(BufferChangeEvent event) {
        setSelected();
    }
}
