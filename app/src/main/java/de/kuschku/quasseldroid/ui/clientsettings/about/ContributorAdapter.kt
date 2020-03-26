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

package de.kuschku.quasseldroid.ui.clientsettings.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.quasseldroid.databinding.WidgetContributorBinding

class ContributorAdapter(private val contributors: List<Contributor>) :
  RecyclerView.Adapter<ContributorAdapter.ContributorViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContributorViewHolder(
    WidgetContributorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
  )

  override fun getItemCount() = contributors.size

  override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
    holder.bind(contributors[position])
  }

  class ContributorViewHolder(
    private val binding: WidgetContributorBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Contributor) {
      binding.name.text = item.name
      binding.nickname.text = item.nickName
      binding.description.text = item.description
    }
  }
}
