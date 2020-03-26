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
import androidx.appcompat.widget.ThemedSpinnerAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.libquassel.protocol.Buffer_Activities
import de.kuschku.quasseldroid.databinding.WidgetSpinnerItemMaterialBinding
import de.kuschku.quasseldroid.util.ui.ContextThemeWrapper
import de.kuschku.quasseldroid.util.ui.RecyclerSpinnerAdapter

class MinimumActivityAdapter(val data: List<MinimumActivityItem>) :
  RecyclerSpinnerAdapter<MinimumActivityAdapter.MinimumActivityViewHolder>(),
  ThemedSpinnerAdapter {

  override fun isEmpty() = data.isEmpty()

  override fun onBindViewHolder(holder: MinimumActivityViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun onCreateViewHolder(parent: ViewGroup, dropDown: Boolean)
    : MinimumActivityViewHolder {
    val inflater = LayoutInflater.from(
      if (dropDown)
        ContextThemeWrapper(parent.context, dropDownViewTheme)
      else
        parent.context
    )
    return MinimumActivityViewHolder(
      WidgetSpinnerItemMaterialBinding.inflate(inflater, parent, false)
    )
  }

  override fun getItem(position: Int) = data[position]

  override fun getItemId(position: Int) = getItem(position).activity.toInt().toLong()

  override fun hasStableIds() = true

  override fun getCount() = data.size

  fun indexOf(activity: Buffer_Activities): Int? {
    for ((key, item) in data.withIndex()) {
      if (item.activity.toInt() == activity.toInt()) {
        return key
      }
    }
    return null
  }

  class MinimumActivityViewHolder(
    private val binding: WidgetSpinnerItemMaterialBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(activity: MinimumActivityItem?) {
      activity?.let {
        binding.text1.setText(it.name)
      }
    }
  }
}
