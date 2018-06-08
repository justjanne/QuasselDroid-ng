package de.kuschku.quasseldroid.ui.chat.info.core

import android.graphics.drawable.Drawable
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.syncables.CoreInfo
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.getVectorDrawableCompat
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.tint
import de.kuschku.quasseldroid.util.helper.visibleIf

class ClientAdapter : ListAdapter<CoreInfo.ConnectedClientData, ClientAdapter.ClientViewHolder>(
  object : DiffUtil.ItemCallback<CoreInfo.ConnectedClientData>() {
    override fun areItemsTheSame(oldItem: CoreInfo.ConnectedClientData,
                                 newItem: CoreInfo.ConnectedClientData) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CoreInfo.ConnectedClientData,
                                    newItem: CoreInfo.ConnectedClientData) = oldItem == newItem
  }
) {
  private var disconnectListener: ((Int) -> Unit)? = null
  fun setDisconnectListener(listener: ((Int) -> Unit)?) {
    this.disconnectListener = listener
  }

  fun disconnect(id: Int) {
    disconnectListener?.invoke(id)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ClientViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.widget_client, parent, false),
    ::disconnect
  )

  override fun onBindViewHolder(holder: ClientViewHolder, position: Int) =
    holder.bind(getItem(position))

  class ClientViewHolder(
    itemView: View,
    private val disconnectListener: (Int) -> Unit
  ) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.ip)
    lateinit var ip: TextView

    @BindView(R.id.version)
    lateinit var version: TextView

    @BindView(R.id.uptime)
    lateinit var uptime: TextView

    @BindView(R.id.location)
    lateinit var location: TextView

    @BindView(R.id.secure_icon)
    lateinit var secureIcon: ImageView

    @BindView(R.id.disconnect)
    lateinit var disconnect: Button

    private var id: Int? = null

    private val secure: Drawable?
    private val insecure: Drawable?

    init {
      ButterKnife.bind(this, itemView)
      disconnect.setOnClickListener {
        id?.let(disconnectListener::invoke)
      }

      secure = itemView.context.getVectorDrawableCompat(R.drawable.ic_lock)?.mutate()
      insecure = itemView.context.getVectorDrawableCompat(R.drawable.ic_lock_open)?.mutate()
      itemView.context.theme.styledAttributes(
        R.attr.colorTintSecure,
        R.attr.colorTintInsecure
      ) {
        secure?.tint(getColor(0, 0))
        insecure?.tint(getColor(1, 0))
      }
    }

    fun bind(data: CoreInfo.ConnectedClientData) {
      id = data.id

      ip.text = data.remoteAddress
      version.text = Html.fromHtml(data.clientVersion)
      uptime.text = itemView.context.getString(R.string.label_core_connected_since,
                                               data.connectedSince.toString())
      location.text = data.location
      location.visibleIf(data.location.isNotBlank())

      secureIcon.setImageDrawable(if (data.secure) secure else insecure)
      disconnect.visibleIf(data.features.hasFeature(ExtendedFeature.RemoteDisconnect))
    }
  }
}
