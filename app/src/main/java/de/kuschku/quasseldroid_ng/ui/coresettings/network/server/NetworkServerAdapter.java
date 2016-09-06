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

package de.kuschku.quasseldroid_ng.ui.coresettings.network.server;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
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
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.ItemTouchHelperAdapter;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.ItemTouchHelperViewHolder;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.OnStartDragListener;
import de.kuschku.util.annotationbind.AutoBinder;

public class NetworkServerAdapter
        extends RecyclerView.Adapter<NetworkServerAdapter.NetworkServerViewHolder> implements ItemTouchHelperAdapter {

    private final List<NetworkServer> servers;
    private final OnStartDragListener dragStartListener;
    private NetworkServerListActivity.OnNetworkServerClickListener onItemClickListener;

    public NetworkServerAdapter(List<NetworkServer> servers, OnStartDragListener dragStartListener) {
        this.servers = servers;
        this.dragStartListener = dragStartListener;
    }

    @Override
    public NetworkServerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.widget_networkserver, parent, false);
        return new NetworkServerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NetworkServerViewHolder holder, int position) {
        holder.bind(servers.get(position));
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
        return servers.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(servers, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void setOnItemClickListener(NetworkServerListActivity.OnNetworkServerClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class NetworkServerViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        @Bind(R.id.text)
        TextView text;

        @Bind(R.id.lock)
        ImageView lock;

        @Bind(R.id.drag_handle)
        ImageView drag_handle;
        private NetworkServer server;

        public NetworkServerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> onItemClickListener.onClick(server));
        }

        public void bind(NetworkServer server) {
            this.server = server;
            text.setText(server == null ? "" : String.format(Locale.US, "%s:%d", server.host, server.port));
            Drawable drawable = ResourcesCompat.getDrawable(itemView.getContext().getResources(), server != null && server.useSSL ? R.drawable.ic_lock : R.drawable.ic_lock_open, itemView.getContext().getTheme());
            lock.setImageDrawable(drawable);
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
