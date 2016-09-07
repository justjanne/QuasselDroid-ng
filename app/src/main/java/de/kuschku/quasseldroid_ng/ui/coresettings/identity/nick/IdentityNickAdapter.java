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

package de.kuschku.quasseldroid_ng.ui.coresettings.identity.nick;

import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.ItemTouchHelperAdapter;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.ItemTouchHelperViewHolder;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.OnStartDragListener;
import de.kuschku.util.annotationbind.AutoBinder;

public class IdentityNickAdapter
        extends RecyclerView.Adapter<IdentityNickAdapter.IdentityNickViewHolder> implements ItemTouchHelperAdapter {

    private final List<String> nicks;
    private final OnStartDragListener dragStartListener;
    private IdentityNickListActivity.OnIdentityNickClickListener onItemClickListener;

    public IdentityNickAdapter(List<String> nicks, OnStartDragListener dragStartListener) {
        this.nicks = nicks;
        this.dragStartListener = dragStartListener;
    }

    @Override
    public IdentityNickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.widget_identitynick, parent, false);
        return new IdentityNickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IdentityNickViewHolder holder, int position) {
        holder.bind(nicks.get(position));
        holder.drag_handle.setOnTouchListener((v, event) -> {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(holder);
                holder.itemView.setSelected(true);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return nicks.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(nicks, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void setOnItemClickListener(IdentityNickListActivity.OnIdentityNickClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class IdentityNickViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        @Bind(R.id.text)
        TextView text;

        @Bind(R.id.drag_handle)
        ImageView drag_handle;

        private String nick;

        public IdentityNickViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> onItemClickListener.onClick(nick));
        }

        public void bind(String nick) {
            this.nick = nick;
            text.setText(nick == null ? "" : nick);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(AutoBinder.obtainColor(R.attr.colorBackground, itemView.getContext().getTheme()));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
