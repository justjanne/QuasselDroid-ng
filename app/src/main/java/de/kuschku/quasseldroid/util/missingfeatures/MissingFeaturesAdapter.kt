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

package de.kuschku.quasseldroid.util.missingfeatures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R

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
    LayoutInflater.from(parent.context).inflate(R.layout.widget_missing_feature, parent, false)
  )

  override fun onBindViewHolder(holder: MissingFeatureViewHolder, position: Int) =
    holder.bind(getItem(position))

  class MissingFeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.name)
    lateinit var name: TextView

    @BindView(R.id.description)
    lateinit var description: TextView

    init {
      ButterKnife.bind(this, itemView)
    }

    fun bind(item: MissingFeature) {
      name.text = item.feature.name
      description.setText(item.description)
    }
  }
}

