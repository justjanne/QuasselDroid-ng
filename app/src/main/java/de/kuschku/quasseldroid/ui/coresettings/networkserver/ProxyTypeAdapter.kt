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

package de.kuschku.quasseldroid.ui.coresettings.networkserver

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.ThemedSpinnerAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.quasseldroid.databinding.WidgetSpinnerItemMaterialBinding
import de.kuschku.quasseldroid.util.ui.ContextThemeWrapper
import de.kuschku.quasseldroid.util.ui.RecyclerSpinnerAdapter

class ProxyTypeAdapter(val data: List<ProxyTypeItem>) :
  RecyclerSpinnerAdapter<ProxyTypeAdapter.ProxyTypeViewHolder>(),
  ThemedSpinnerAdapter {

  override fun isEmpty() = data.isEmpty()

  override fun onBindViewHolder(holder: ProxyTypeViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun onCreateViewHolder(parent: ViewGroup, dropDown: Boolean)
    : ProxyTypeViewHolder {
    val inflater = LayoutInflater.from(
      if (dropDown) ContextThemeWrapper(parent.context, dropDownViewTheme)
      else parent.context
    )
    return ProxyTypeViewHolder(
      WidgetSpinnerItemMaterialBinding.inflate(inflater, parent, false)
    )
  }

  override fun getItem(position: Int) = data[position]

  override fun getItemId(position: Int) = getItem(position).value.value.toLong()

  override fun hasStableIds() = true

  override fun getCount() = data.size

  fun indexOf(value: Int): Int? {
    for ((key, item) in data.withIndex()) {
      if (item.value.value == value) {
        return key
      }
    }
    return null
  }

  class ProxyTypeViewHolder(
    private val binding: WidgetSpinnerItemMaterialBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(activity: ProxyTypeItem?) {
      activity?.let {
        binding.text1.setText(it.name)
      }
    }
  }
}
