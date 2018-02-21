package de.kuschku.quasseldroid_ng.ui.chat.messages

import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R

class QuasselMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
  @BindView(R.id.time)
  lateinit var time: TextView

  @BindView(R.id.content)
  lateinit var content: TextView

  @BindView(R.id.markerline)
  lateinit var markerline: View

  init {
    ButterKnife.bind(this, itemView)
    content.movementMethod = LinkMovementMethod.getInstance()
  }
}