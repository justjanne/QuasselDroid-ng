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

package de.kuschku.quasseldroid.ui.coresettings.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.ThemedSpinnerAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.quasseldroid.databinding.WidgetSpinnerItemMaterialBinding
import de.kuschku.quasseldroid.util.ui.ContextThemeWrapper
import de.kuschku.quasseldroid.util.ui.RecyclerSpinnerAdapter

class NetworkAdapter(@StringRes private val fallbackName: Int) :
  RecyclerSpinnerAdapter<NetworkAdapter.NetworkViewHolder>(),
  ThemedSpinnerAdapter {
  var data = listOf<INetwork.NetworkInfo?>(null)

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
    return NetworkViewHolder(fallbackName,
                             WidgetSpinnerItemMaterialBinding.inflate(inflater, parent, false))
  }

  fun indexOf(id: NetworkId): Int? {
    for ((key, item) in data.withIndex()) {
      if (item?.networkId ?: 0 == id) {
        return key
      }
    }
    return null
  }

  override fun getItem(position: Int): INetwork.NetworkInfo? = data[position]
  override fun getItemId(position: Int) = getItem(position)?.networkId?.id?.toLong() ?: 0
  override fun hasStableIds() = true
  override fun getCount() = data.size
  class NetworkViewHolder(
    @StringRes private val fallbackName: Int,
    private val binding: WidgetSpinnerItemMaterialBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(network: INetwork.NetworkInfo?) {
      binding.text1.text = network?.networkName ?: itemView.context.getString(fallbackName)
    }
  }
}
