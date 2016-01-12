package de.kuschku.quasseldroid_ng.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.ObservableList;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private ObservableList<Message> messageList = new ObservableList<>(Message.class);
    private ChatMessageRenderer renderer;
    private LayoutInflater inflater;

    public MessageAdapter(Context ctx) {
        this.inflater = LayoutInflater.from(ctx);
        this.renderer = new ChatMessageRenderer(ctx);
    }

    public void setClient(Client client) {
        renderer.setClient(client);
    }

    public void setMessageList(ObservableList<Message> messageList) {
        this.messageList.setCallback(null);
        this.messageList = messageList;
        this.messageList.setCallback(new ObservableList.RecyclerViewAdapterCallback(this));
        notifyDataSetChanged();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(inflater.inflate(R.layout.widget_chatmessage, parent, false));
    }

    // TODO: REWRITE THIS
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message msg = messageList.list.get(position);
        renderer.onBind(holder, msg);
    }

    @Override
    public int getItemCount() {
        return messageList.list.size();
    }
}
