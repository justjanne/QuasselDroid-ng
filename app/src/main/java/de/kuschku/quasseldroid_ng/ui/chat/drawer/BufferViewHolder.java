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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.google.common.base.Function;
import com.mikepenz.materialize.util.UIUtils;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.localtypes.buffers.ChannelBuffer;
import de.kuschku.libquassel.localtypes.buffers.QueryBuffer;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.ViewIntBinder;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;

public class BufferViewHolder extends ChildViewHolder {

    private final AppContext context;
    private final StateListDrawable background;
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
    private ViewIntBinder viewIntBinder;
    private boolean selected = false;
    private boolean checked = false;

    public BufferViewHolder(AppContext context, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;

        background = new StateListDrawable();
        background.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(context.themeUtil().res.colorSelected));
        background.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(context.themeUtil().res.colorSelected));
        background.addState(new int[]{android.R.attr.state_checked, android.R.attr.state_selected}, new ColorDrawable(context.themeUtil().res.colorSelected));
        background.addState(new int[0], UIUtils.getSelectableBackground(itemView.getContext()));
    }

    @LayoutRes
    public static int layout() {
        return R.layout.widget_buffer;
    }

    public void bind(OnBufferClickListener listener, OnBufferLongClickListener longClickListener, Buffer buffer) {
        if (status != null)
            status.removeOnPropertyChangedCallback(callback);
        status = buffer.getStatus();
        name.setText(buffer.getName());
        if (viewIntBinder != null) viewIntBinder.unbind();
        viewIntBinder = new ViewIntBinder(context.client().bufferSyncer().activity(buffer.getInfo().id));
        viewIntBinder.bindTextColor(name, colorFromActivityStatus(buffer));
        setDescription(context.deserializer().formatString(getDescription(buffer)));
        setBadge(0);

        itemView.setOnClickListener(v -> listener.onClick(buffer));
        itemView.setOnLongClickListener(v -> longClickListener.onLongClick(buffer));

        itemView.setBackground(background);

        id = buffer.getInfo().id;

        BufferInfo.Type type = buffer.getInfo().type;
        setIcon(context, type, status);
        callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                setIcon(context, type, status);
            }
        };
        status.addOnPropertyChangedCallback(callback);
    }

    @NonNull
    private Function<Integer, Integer> colorFromActivityStatus(Buffer buffer) {
        return activities -> {
            int filters = context.client().bufferSyncer().getFilters(buffer.getInfo().id);
            activities = activities & ~filters;
            if (0 != ((activities & Message.Type.Plain.value) | (activities & Message.Type.Notice.value) | (activities & Message.Type.Action.value)))
                return context.themeUtil().res.colorTintMessage;
            if (0 != activities)
                return context.themeUtil().res.colorTintActivity;
            else
                return context.themeUtil().res.colorForeground;
        };
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateSelectionState();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        updateSelectionState();
    }

    private void updateSelectionState() {
        itemView.setSelected(selected || checked);
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
}
