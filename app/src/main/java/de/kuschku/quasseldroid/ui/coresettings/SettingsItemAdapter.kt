/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.coresettings

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.quasseldroid.R

class SettingsItemAdapter(private val clickListener: (Int) -> Unit) :
  ListAdapter<SettingsItem, SettingsItemAdapter.SettingsItemViewHolder>(
    object : DiffUtil.ItemCallback<SettingsItem>() {
      override fun areItemsTheSame(oldItem: SettingsItem, newItem: SettingsItem) =
        oldItem.id == newItem.id

      override fun areContentsTheSame(oldItem: SettingsItem, newItem: SettingsItem) =
        oldItem == newItem
    }
  ) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SettingsItemViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.settings_item, parent, false),
    clickListener
  )

  override fun onBindViewHolder(holder: SettingsItemViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  class SettingsItemViewHolder(itemView: View, clickListener: (Int) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.title)
    lateinit var title: TextView

    var id: IdentityId? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        id?.let(clickListener::invoke)
      }
    }

    fun bind(item: SettingsItem) {
      this.id = item.id
      this.title.text = item.name
    }
  }
}
