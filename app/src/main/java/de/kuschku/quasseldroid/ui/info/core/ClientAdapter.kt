/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.info.core

import android.graphics.drawable.Drawable
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.syncables.CoreInfo
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.databinding.WidgetClientBinding
import de.kuschku.quasseldroid.util.helper.getVectorDrawableCompat
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.tint
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.ui.BetterLinkMovementMethod
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class ClientAdapter : ListAdapter<CoreInfo.ConnectedClientData, ClientAdapter.ClientViewHolder>(
  object : DiffUtil.ItemCallback<CoreInfo.ConnectedClientData>() {
    override fun areItemsTheSame(oldItem: CoreInfo.ConnectedClientData,
                                 newItem: CoreInfo.ConnectedClientData) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CoreInfo.ConnectedClientData,
                                    newItem: CoreInfo.ConnectedClientData) = oldItem == newItem
  }
) {
  private val movementMethod = BetterLinkMovementMethod.newInstance()

  init {
    movementMethod.setOnLinkLongClickListener(LinkLongClickMenuHelper())
  }

  private var disconnectListener: ((Int) -> Unit)? = null
  fun setDisconnectListener(listener: ((Int) -> Unit)?) {
    this.disconnectListener = listener
  }

  fun disconnect(id: Int) {
    disconnectListener?.invoke(id)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ClientViewHolder(
    WidgetClientBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    dateTimeFormatter,
    dateFormatter,
    ::disconnect,
    movementMethod
  )

  private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
  private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

  override fun onBindViewHolder(holder: ClientViewHolder, position: Int) =
    holder.bind(getItem(position))

  class ClientViewHolder(
    private val binding: WidgetClientBinding,
    private val dateTimeFormatter: DateTimeFormatter,
    private val dateFormatter: DateTimeFormatter,
    private val disconnectListener: (Int) -> Unit,
    movementMethod: BetterLinkMovementMethod
  ) : RecyclerView.ViewHolder(binding.root) {

    private var id: Int? = null

    private val secure: Drawable?
    private val insecure: Drawable?

    init {
      binding.version.movementMethod = movementMethod
      binding.disconnect.setOnClickListener {
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

      binding.ip.text = data.remoteAddress
      val versionTime = data.clientVersionDate.toLongOrNull()
      val formattedVersionTime = if (versionTime != null)
        dateFormatter.format(Instant.ofEpochSecond(versionTime).atZone(ZoneId.systemDefault()))
      else
        data.clientVersionDate
      binding.version.text = Html.fromHtml(data.clientVersion + " ($formattedVersionTime)")
      val connectedSinceFormatted = dateTimeFormatter.format(data.connectedSince.atZone(ZoneId.systemDefault()))
      binding.uptime.text = itemView.context.getString(R.string.label_core_connected_since,
                                                       connectedSinceFormatted)
      binding.location.text = data.location
      binding.location.visibleIf(data.location.isNotBlank())

      binding.secureIcon.setImageDrawable(if (data.secure) secure else insecure)
      binding.disconnect.visibleIf(data.features.hasFeature(ExtendedFeature.RemoteDisconnect))
    }
  }
}
