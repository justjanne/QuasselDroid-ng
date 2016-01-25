package de.kuschku.quasseldroid_ng.ui.chat.drawer;

import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.ui.Bindable;

@UiThread
public class BufferWrapper implements Nameable<BufferWrapper> {
    private final Buffer buffer;

    public BufferWrapper(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public BufferWrapper withName(String name) {
        return this;
    }

    @Override
    public BufferWrapper withName(int nameRes) {
        return this;
    }

    @Override
    public BufferWrapper withName(StringHolder name) {
        return this;
    }

    @Override
    public StringHolder getName() {
        return new StringHolder(buffer.getName());
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public static class ViewHolder extends ChildViewHolder implements Bindable<BufferWrapper> {
        @Bind(R.id.material_drawer_icon)
        ImageView materialDrawerIcon;
        @Bind(R.id.material_drawer_name)
        TextView materialDrawerName;
        @Bind(R.id.material_drawer_description)
        TextView materialDrawerDescription;
        @Bind(R.id.material_drawer_badge_container)
        View materialDrawerBadgeContainer;
        @Bind(R.id.material_drawer_badge)
        TextView materialDrawerBadge;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(BufferWrapper wrapper) {
            materialDrawerName.setText(wrapper.getName().getText());
        }
    }
}
