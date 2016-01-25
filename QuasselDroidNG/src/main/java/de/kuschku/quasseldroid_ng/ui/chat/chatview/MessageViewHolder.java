package de.kuschku.quasseldroid_ng.ui.chat.chatview;

import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;

@UiThread
public class MessageViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.time)
    TextView time;

    @Bind(R.id.content)
    TextView content;

    public MessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
