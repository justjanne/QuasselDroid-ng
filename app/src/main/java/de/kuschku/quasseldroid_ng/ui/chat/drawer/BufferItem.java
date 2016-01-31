package de.kuschku.quasseldroid_ng.ui.chat.drawer;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.ColorHolder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.localtypes.ChannelBuffer;
import de.kuschku.libquassel.localtypes.QueryBuffer;
import de.kuschku.libquassel.localtypes.StatusBuffer;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.GeneralCallback;
import de.kuschku.util.observables.callbacks.wrappers.GeneralCallbackWrapper;
import de.kuschku.util.observables.callbacks.wrappers.GeneralUICallbackWrapper;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;
import de.kuschku.util.ui.MessageUtil;

public class BufferItem extends SecondaryDrawerItem implements IObservable<GeneralCallback>, GeneralCallback {
    @NonNull
    private final Buffer buffer;
    @NonNull
    private final AppContext context;
    @NonNull
    private final ObservableComparableSortedList<Message> notifications;

    @NonNull
    private final GeneralCallbackWrapper callback = new GeneralCallbackWrapper();

    public BufferItem(@NonNull Buffer buffer, @NonNull AppContext context) {
        this.buffer = buffer;
        this.context = context;
        notifications = context.getClient().getNotificationManager().getNotifications(buffer.getInfo().id);
        notifications.addCallback(new GeneralUICallbackWrapper() {
            @Override
            public void notifyChanged() {
                BufferItem.this.notifyChanged();
            }
        });
    }

    public void notifyChanged() {
        callback.notifyChanged();
    }

    @NonNull
    @Override
    public StringHolder getBadge() {
        return new StringHolder(String.valueOf(notifications.size()));
    }

    @Override
    public BadgeStyle getBadgeStyle() {
        if (notifications.isEmpty()) {
            return new BadgeStyle();
        } else {
            return new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700);
        }
    }

    @Override
    public StringHolder getDescription() {
        if (buffer instanceof QueryBuffer) {
            QueryBuffer queryBuffer = (QueryBuffer) buffer;
            if (queryBuffer.getUser() != null)
                return new StringHolder(queryBuffer.getUser().getRealName());
        } else if (buffer instanceof StatusBuffer) {

        } else if (buffer instanceof ChannelBuffer) {
            ChannelBuffer channelBuffer = (ChannelBuffer) buffer;
            if (channelBuffer.getChannel() != null)
                return new StringHolder(channelBuffer.getChannel().getTopic());
        }
        return super.getDescription();
    }

    @Nullable
    @Override
    public StringHolder getName() {
        if (buffer instanceof StatusBuffer)
            return new StringHolder(context.getThemeUtil().translations.titleStatusBuffer);
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
                ColorHolder.fromColor(context.getThemeUtil().res.colorAccent) :
                new ColorHolder();
    }

    @NonNull
    @Override
    public ColorHolder getDescriptionTextColor() {
        return ColorHolder.fromColor(context.getThemeUtil().res.colorForegroundSecondary);
    }

    @Override
    public void addCallback(GeneralCallback callback) {
        this.callback.addCallback(callback);
    }

    @Override
    public void removeCallback(GeneralCallback callback) {
        this.callback.removeCallback(callback);
    }

    @NonNull
    public Buffer getBuffer() {
        return buffer;
    }

    @Override
    public long getIdentifier() {
        return buffer.getInfo().id;
    }

    @Override
    public void onPostBindView(IDrawerItem drawerItem, @NonNull View view) {
        super.onPostBindView(drawerItem, view);

        if (getDescription() != null && getDescription().getText() != null)
            ((TextView) view.findViewById(R.id.material_drawer_description)).setText(MessageUtil.parseStyleCodes(
                    context.getThemeUtil(),
                    getDescription().getText(),
                    context.getSettings().mircColors.or(true)
            ));
    }
}
