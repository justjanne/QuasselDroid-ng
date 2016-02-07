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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.holder.ColorHolder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.localtypes.buffers.ChannelBuffer;
import de.kuschku.libquassel.localtypes.buffers.QueryBuffer;
import de.kuschku.libquassel.localtypes.buffers.StatusBuffer;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.ui.MessageUtil;

public class BufferItem extends SecondaryDrawerItem {
    @NonNull
    private final Buffer buffer;
    @NonNull
    private final AppContext context;
    public BufferItem(@NonNull Buffer buffer, @NonNull AppContext context) {
        this.buffer = buffer;
        this.context = context;
    }

    @Override
    public StringHolder getDescription() {
        if (buffer instanceof QueryBuffer) {
            QueryBuffer queryBuffer = (QueryBuffer) buffer;
            if (queryBuffer.getUser() != null)
                return new StringHolder(queryBuffer.getUser().realName());
        } else if (buffer instanceof StatusBuffer) {

        } else if (buffer instanceof ChannelBuffer) {
            ChannelBuffer channelBuffer = (ChannelBuffer) buffer;
            if (channelBuffer.getChannel() != null)
                return new StringHolder(channelBuffer.getChannel().topic());
        }
        return super.getDescription();
    }

    @Nullable
    @Override
    public StringHolder getName() {
        if (buffer instanceof StatusBuffer)
            return new StringHolder(context.themeUtil().translations.titleStatusBuffer);
        else
            return new StringHolder(buffer.getName());
    }

    @NonNull
    @Override
    public ImageHolder getIcon() {
        if (buffer instanceof ChannelBuffer) {
            if (buffer.getStatus() != BufferInfo.BufferStatus.OFFLINE) {
                return new ImageHolder(R.drawable.ic_status_channel);
            } else {
                return new ImageHolder(R.drawable.ic_status_channel_offline);
            }
        } else if (buffer instanceof StatusBuffer) {
            if (buffer.getStatus() != BufferInfo.BufferStatus.OFFLINE) {
                return new ImageHolder(R.drawable.ic_status);
            } else {
                return new ImageHolder(R.drawable.ic_status_offline);
            }
        } else {
            if (buffer.getStatus() != BufferInfo.BufferStatus.OFFLINE) {
                return new ImageHolder(R.drawable.ic_status);
            } else {
                return new ImageHolder(R.drawable.ic_status_offline);
            }
        }
    }

    @Override
    public boolean isIconTinted() {
        return buffer.getStatus() == BufferInfo.BufferStatus.ONLINE;
    }

    @NonNull
    @Override
    public ColorHolder getIconColor() {
        return buffer.getStatus() == BufferInfo.BufferStatus.ONLINE ?
                ColorHolder.fromColor(context.themeUtil().res.colorAccent) :
                new ColorHolder();
    }

    @NonNull
    @Override
    public ColorHolder getDescriptionTextColor() {
        return ColorHolder.fromColor(context.themeUtil().res.colorForegroundSecondary);
    }

    @NonNull
    public Buffer getBuffer() {
        return buffer;
    }

    @Override
    public long getIdentifier() {
        return buffer.getInfo().id() + buffer.getInfo().networkId() << 16;
    }

    @Override
    public void onPostBindView(IDrawerItem drawerItem, @NonNull View view) {
        super.onPostBindView(drawerItem, view);

        if (getDescription() != null && getDescription().getText() != null)
            ((TextView) view.findViewById(R.id.material_drawer_description)).setText(MessageUtil.parseStyleCodes(
                    context.themeUtil(),
                    getDescription().getText(),
                    context.settings().mircColors.or(true)
            ));
    }
}
