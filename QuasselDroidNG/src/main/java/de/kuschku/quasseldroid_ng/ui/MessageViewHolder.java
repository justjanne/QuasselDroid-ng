package de.kuschku.quasseldroid_ng.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;

class MessageViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.time)
    TextView time;

    @Bind(R.id.content)
    TextView content;

    public MessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
