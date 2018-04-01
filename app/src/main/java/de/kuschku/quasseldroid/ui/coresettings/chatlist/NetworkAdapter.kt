package de.kuschku.quasseldroid.ui.coresettings.chatlist

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ThemedSpinnerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.ui.ContextThemeWrapper
import de.kuschku.quasseldroid.util.ui.RecyclerSpinnerAdapter

class NetworkAdapter : RecyclerSpinnerAdapter<NetworkAdapter.NetworkViewHolder>(),
                       ThemedSpinnerAdapter {
  var data = emptyList<INetwork.NetworkInfo?>()

  fun submitList(list: List<INetwork.NetworkInfo?>) {
    data = list
    notifyDataSetChanged()
  }

  override fun isEmpty() = data.isEmpty()

  override fun onBindViewHolder(holder: NetworkViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun onCreateViewHolder(parent: ViewGroup, dropDown: Boolean)
    : NetworkViewHolder {
    val inflater = LayoutInflater.from(
      if (dropDown)
        ContextThemeWrapper(parent.context, dropDownViewTheme)
      else
        parent.context
    )
    val view = inflater.inflate(R.layout.widget_spinner_item_toolbar, parent, false)
    return NetworkViewHolder(
      view
    )
  }

  fun indexOf(id: NetworkId): Int? {
    for ((key, item) in data.withIndex()) {
      if (item?.networkId ?: -1 == id) {
        return key
      }
    }
    return null
  }

  override fun getItem(position: Int): INetwork.NetworkInfo? = data[position]

  override fun getItemId(position: Int) = getItem(position)?.networkId?.toLong() ?: -1

  override fun hasStableIds() = true

  override fun getCount() = data.size

  class NetworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(android.R.id.text1)
    lateinit var text: TextView

    init {
      ButterKnife.bind(this, itemView)
    }

    fun bind(network: INetwork.NetworkInfo?) {
      text.text = network?.networkName ?: "All"
    }
  }
}
