/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.quasseldroid.ui.clientsettings.whitelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.models.SslHostnameWhitelistEntry
import de.kuschku.quasseldroid.util.helper.setTooltip

class WhitelistHostnameAdapter :
  RecyclerView.Adapter<WhitelistHostnameAdapter.WhitelistItemViewHolder>() {
  private var updateListener: ((List<SslHostnameWhitelistEntry>) -> Unit)? = null

  fun setOnUpdateListener(listener: ((List<SslHostnameWhitelistEntry>) -> Unit)?) {
    updateListener = listener
  }

  private val data = mutableListOf<SslHostnameWhitelistEntry>()
  var list: List<SslHostnameWhitelistEntry>
    get() = data
    set(value) {
      val length = data.size
      data.clear()
      notifyItemRangeRemoved(0, length)
      data.addAll(value)
      notifyItemRangeInserted(0, list.size)
      updateListener?.invoke(list)
    }

  fun add(item: SslHostnameWhitelistEntry) {
    val index = data.size
    data.add(item)
    notifyItemInserted(index)
    updateListener?.invoke(list)
  }

  fun replace(index: Int, item: SslHostnameWhitelistEntry) {
    data[index] = item
    notifyItemChanged(index)
    updateListener?.invoke(list)
  }

  fun indexOf(item: SslHostnameWhitelistEntry) = data.indexOf(item)

  fun remove(index: Int) {
    data.removeAt(index)
    notifyItemRemoved(index)
    updateListener?.invoke(list)
  }

  fun remove(item: SslHostnameWhitelistEntry) = remove(indexOf(item))

  override fun getItemCount() = data.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WhitelistItemViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.preferences_whitelist_hostname_item,
                                                parent,
                                                false),
    ::remove
  )

  override fun onBindViewHolder(holder: WhitelistItemViewHolder, position: Int) {
    holder.bind(data[position])
  }

  class WhitelistItemViewHolder(
    itemView: View,
    clickListener: ((SslHostnameWhitelistEntry) -> Unit)?
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.hostname)
    lateinit var hostname: TextView

    @BindView(R.id.fingerprint)
    lateinit var fingerprint: TextView

    @BindView(R.id.action_delete)
    lateinit var delete: AppCompatImageButton

    private var item: SslHostnameWhitelistEntry? = null

    init {
      ButterKnife.bind(this, itemView)
      delete.setOnClickListener {
        item?.let {
          clickListener?.invoke(it)
        }
      }
      delete.setTooltip()
    }

    fun bind(item: SslHostnameWhitelistEntry) {
      this.item = item
      hostname.text = item.hostname
      fingerprint.text = item.fingerprint
    }
  }
}
