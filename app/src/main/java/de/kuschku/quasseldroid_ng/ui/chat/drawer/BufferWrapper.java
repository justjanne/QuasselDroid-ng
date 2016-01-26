package de.kuschku.quasseldroid_ng.ui.chat.drawer;

import android.support.annotation.NonNull;
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
    @NonNull
    private final Buffer buffer;

    public BufferWrapper(@NonNull Buffer buffer) {
        this.buffer = buffer;
    }

    @NonNull
    @Override
    public BufferWrapper withName(String name) {
        return this;
    }

    @NonNull
    @Override
    public BufferWrapper withName(int nameRes) {
        return this;
    }

    @NonNull
    @Override
    public BufferWrapper withName(StringHolder name) {
        return this;
    }

    @NonNull
    @Override
    public StringHolder getName() {
        return new StringHolder(buffer.getName());
    }

    @NonNull
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull BufferWrapper wrapper) {
            materialDrawerName.setText(wrapper.getName().getText());
        }
    }
}
