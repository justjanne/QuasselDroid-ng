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

package de.kuschku.quasseldroid.ui.coresettings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.quasseldroid.databinding.SettingsItemBinding

class SettingsItemAdapter<T>(private val clickListener: (T) -> Unit) :
  ListAdapter<SettingsItem<T>, SettingsItemAdapter.SettingsItemViewHolder<T>>(
    object : DiffUtil.ItemCallback<SettingsItem<T>>() {
      override fun areItemsTheSame(oldItem: SettingsItem<T>, newItem: SettingsItem<T>) =
        oldItem.id == newItem.id

      override fun areContentsTheSame(oldItem: SettingsItem<T>, newItem: SettingsItem<T>) =
        oldItem == newItem
    }
  ) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SettingsItemViewHolder(
    SettingsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    clickListener
  )

  override fun onBindViewHolder(holder: SettingsItemViewHolder<T>, position: Int) {
    holder.bind(getItem(position))
  }

  class SettingsItemViewHolder<T>(
    private val binding: SettingsItemBinding,
    clickListener: (T) -> Unit
  ) : RecyclerView.ViewHolder(binding.root) {
    var id: T? = null

    init {
      itemView.setOnClickListener {
        id?.let(clickListener::invoke)
      }
    }

    fun bind(item: SettingsItem<T>) {
      this.id = item.id
      binding.title.text = item.name
    }
  }
}
