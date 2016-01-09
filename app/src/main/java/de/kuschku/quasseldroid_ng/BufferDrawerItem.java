package de.kuschku.quasseldroid_ng;

import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.localtypes.ChannelBuffer;

public class BufferDrawerItem extends SecondaryDrawerItem {
    final Buffer buffer;

    public BufferDrawerItem(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public StringHolder getName() {
        return new StringHolder(buffer.getName());
    }

    @Override
    public ImageHolder getIcon() {
        if (buffer instanceof ChannelBuffer)
            return new ImageHolder(R.drawable.ic_status_channel);
        else
            return new ImageHolder(R.drawable.ic_status);
    }

    @Override
    public int getIdentifier() {
        return buffer.getInfo().id;
    }
}
