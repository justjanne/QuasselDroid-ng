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

package de.kuschku.quasseldroid.ui.clientsettings.whitelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.quasseldroid.databinding.PreferencesWhitelistHostnameItemBinding
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
    PreferencesWhitelistHostnameItemBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    ),
    ::remove
  )

  override fun onBindViewHolder(holder: WhitelistItemViewHolder, position: Int) {
    holder.bind(data[position])
  }

  class WhitelistItemViewHolder(
    private val binding: PreferencesWhitelistHostnameItemBinding,
    clickListener: ((SslHostnameWhitelistEntry) -> Unit)?
  ) : RecyclerView.ViewHolder(binding.root) {
    private var item: SslHostnameWhitelistEntry? = null

    init {
      binding.actionDelete.setOnClickListener {
        item?.let {
          clickListener?.invoke(it)
        }
      }
      binding.actionDelete.setTooltip()
    }

    fun bind(item: SslHostnameWhitelistEntry) {
      this.item = item
      binding.hostname.text = item.hostname
      binding.fingerprint.text = item.fingerprint
    }
  }
}
