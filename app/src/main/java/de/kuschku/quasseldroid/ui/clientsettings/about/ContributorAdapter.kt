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

package de.kuschku.quasseldroid.ui.clientsettings.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R

class ContributorAdapter(private val contributors: List<Contributor>) :
  RecyclerView.Adapter<ContributorAdapter.ContributorViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContributorViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.widget_contributor, parent, false)
  )

  override fun getItemCount() = contributors.size

  override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
    holder.bind(contributors[position])
  }

  class ContributorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.name)
    lateinit var name: TextView

    @BindView(R.id.nickname)
    lateinit var nickName: TextView

    @BindView(R.id.description)
    lateinit var description: TextView

    init {
      ButterKnife.bind(this, itemView)
    }

    fun bind(item: Contributor) {
      this.name.text = item.name
      this.nickName.text = item.nickName
      this.description.text = item.description
    }
  }
}
