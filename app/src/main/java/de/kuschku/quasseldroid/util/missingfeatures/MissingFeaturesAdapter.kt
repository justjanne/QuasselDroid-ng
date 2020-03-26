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

package de.kuschku.quasseldroid.util.missingfeatures

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.quasseldroid.databinding.WidgetMissingFeatureBinding

class MissingFeaturesAdapter :
  ListAdapter<MissingFeature, MissingFeaturesAdapter.MissingFeatureViewHolder>(
    object : DiffUtil.ItemCallback<MissingFeature>() {
      override fun areItemsTheSame(oldItem: MissingFeature, newItem: MissingFeature) =
        oldItem.feature == newItem.feature

      override fun areContentsTheSame(oldItem: MissingFeature, newItem: MissingFeature) =
        oldItem == newItem
    }
  ) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MissingFeatureViewHolder(
    WidgetMissingFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
  )

  override fun onBindViewHolder(holder: MissingFeatureViewHolder, position: Int) =
    holder.bind(getItem(position))

  class MissingFeatureViewHolder(
    private val binding: WidgetMissingFeatureBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MissingFeature) {
      binding.name.text = item.feature.name
      binding.description.setText(item.description)
    }
  }
}

