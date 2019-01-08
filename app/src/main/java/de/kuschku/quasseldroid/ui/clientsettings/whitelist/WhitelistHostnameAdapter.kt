/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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
import de.kuschku.quasseldroid.persistence.QuasselDatabase

class WhitelistHostnameAdapter :
  RecyclerView.Adapter<WhitelistHostnameAdapter.WhitelistItemViewHolder>() {
  private val data = mutableListOf<QuasselDatabase.SslHostnameWhitelistEntry>()
  var list: List<QuasselDatabase.SslHostnameWhitelistEntry>
    get() = data
    set(value) {
      val length = data.size
      data.clear()
      notifyItemRangeRemoved(0, length)
      data.addAll(value)
      notifyItemRangeInserted(0, list.size)
    }

  fun add(item: QuasselDatabase.SslHostnameWhitelistEntry) {
    val index = data.size
    data.add(item)
    notifyItemInserted(index)
  }

  fun replace(index: Int, item: QuasselDatabase.SslHostnameWhitelistEntry) {
    data[index] = item
    notifyItemChanged(index)
  }

  fun indexOf(item: QuasselDatabase.SslHostnameWhitelistEntry) = data.indexOf(item)

  fun remove(index: Int) {
    data.removeAt(index)
    notifyItemRemoved(index)
  }

  fun remove(item: QuasselDatabase.SslHostnameWhitelistEntry) = remove(indexOf(item))

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
    clickListener: ((QuasselDatabase.SslHostnameWhitelistEntry) -> Unit)?
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.hostname)
    lateinit var hostname: TextView

    @BindView(R.id.fingerprint)
    lateinit var fingerprint: TextView

    @BindView(R.id.action_delete)
    lateinit var delete: AppCompatImageButton

    private var item: QuasselDatabase.SslHostnameWhitelistEntry? = null

    init {
      ButterKnife.bind(this, itemView)
      delete.setOnClickListener {
        item?.let {
          clickListener?.invoke(it)
        }
      }
    }

    fun bind(item: QuasselDatabase.SslHostnameWhitelistEntry) {
      this.item = item
      hostname.text = item.hostname
      fingerprint.text = item.fingerprint
    }
  }
}
