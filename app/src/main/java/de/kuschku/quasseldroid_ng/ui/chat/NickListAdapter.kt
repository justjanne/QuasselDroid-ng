package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.visibleIf
import de.kuschku.quasseldroid_ng.util.irc.IrcCaseMappers

class NickListAdapter(
  lifecycleOwner: LifecycleOwner,
  liveData: LiveData<List<IrcUserItem>?>,
  runInBackground: (() -> Unit) -> Any,
  runOnUiThread: (Runnable) -> Any,
  private val clickListener: ((String) -> Unit)? = null
) : RecyclerView.Adapter<NickListAdapter.NickViewHolder>() {
  var data = mutableListOf<IrcUserItem>()

  init {
    liveData.observe(
      lifecycleOwner, Observer { it: List<IrcUserItem>? ->
      runInBackground {
        val list = it ?: emptyList()
        val old: List<IrcUserItem> = data
        val new: List<IrcUserItem> = list
          .sortedBy { IrcCaseMappers[it.networkCasemapping].toLowerCase(it.nick) }
          .sortedBy { it.lowestMode }
        val result = DiffUtil.calculateDiff(
          object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
              = old[oldItemPosition].nick == new[newItemPosition].nick

            override fun getOldListSize() = old.size
            override fun getNewListSize() = new.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
              = old[oldItemPosition] == new[newItemPosition]
          }, true
        )
        runOnUiThread(
          Runnable {
            data.clear()
            data.addAll(new)
            result.dispatchUpdatesTo(this@NickListAdapter)
          }
        )
      }
    }
    )
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NickViewHolder(
    LayoutInflater.from(parent.context).inflate(
      when (viewType) {
        VIEWTYPE_AWAY -> R.layout.widget_nick_away
        else          -> R.layout.widget_nick
      }, parent, false
    ),
    clickListener = clickListener
  )

  override fun onBindViewHolder(holder: NickViewHolder, position: Int)
    = holder.bind(data[position])

  override fun getItemCount() = data.size

  override fun getItemViewType(position: Int) = if (data[position].away) {
    VIEWTYPE_AWAY
  } else {
    VIEWTYPE_ACTIVE
  }

  data class IrcUserItem(
    val nick: String,
    val modes: String,
    val lowestMode: Int,
    val realname: String,
    val away: Boolean,
    val networkCasemapping: String
  )

  class NickViewHolder(
    itemView: View,
    private val clickListener: ((String) -> Unit)? = null
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.modesContainer)
    lateinit var modesContainer: View

    @BindView(R.id.modes)
    lateinit var modes: TextView

    @BindView(R.id.nick)
    lateinit var nick: TextView

    @BindView(R.id.realname)
    lateinit var realname: TextView

    var user: String? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        val nick = user
        if (nick != null)
          clickListener?.invoke(nick)
      }
    }

    fun bind(data: IrcUserItem) {
      user = data.nick

      nick.text = data.nick
      modes.text = data.modes
      realname.text = data.realname

      modes.visibleIf(data.modes.isNotBlank())
    }
  }

  companion object {
    val VIEWTYPE_ACTIVE = 0
    val VIEWTYPE_AWAY = 1
  }
}