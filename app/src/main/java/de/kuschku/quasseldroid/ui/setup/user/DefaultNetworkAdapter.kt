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

package de.kuschku.quasseldroid.ui.setup.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.ThemedSpinnerAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.databinding.WidgetSpinnerItemMaterialBinding
import de.kuschku.quasseldroid.defaults.DefaultNetwork
import de.kuschku.quasseldroid.defaults.DefaultNetworks
import de.kuschku.quasseldroid.util.ui.ContextThemeWrapper
import de.kuschku.quasseldroid.util.ui.RecyclerSpinnerAdapter
import javax.inject.Inject

class DefaultNetworkAdapter @Inject constructor(defaultNetworks: DefaultNetworks) :
  RecyclerSpinnerAdapter<DefaultNetworkAdapter.DefaultNetworkViewHolder>(), ThemedSpinnerAdapter {
  private val data: List<DefaultNetwork?> = defaultNetworks.networks + (null as DefaultNetwork?)

  override fun isEmpty() = data.isEmpty()

  override fun onBindViewHolder(holder: DefaultNetworkViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun onCreateViewHolder(parent: ViewGroup, dropDown: Boolean): DefaultNetworkViewHolder {
    val inflater = LayoutInflater.from(
      if (dropDown)
        ContextThemeWrapper(parent.context, dropDownViewTheme)
      else
        parent.context
    )
    return DefaultNetworkViewHolder(
      WidgetSpinnerItemMaterialBinding.inflate(inflater, parent, false)
    )
  }

  override fun getItem(position: Int) = data[position]

  override fun getCount() = data.size

  fun default() = data.filterNotNull().firstOrNull(DefaultNetwork::default)

  fun indexOf(value: DefaultNetwork?) = data.indexOf(value)

  override fun getItemId(position: Int) = position.toLong()

  class DefaultNetworkViewHolder(
    private val binding: WidgetSpinnerItemMaterialBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(network: DefaultNetwork?) {
      network?.let {
        binding.text1.text = it.name
      } ?: binding.text1.setText(R.string.label_network_custom)
    }
  }
}
