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
import de.kuschku.quasseldroid.databinding.WidgetLibraryBinding
import de.kuschku.quasseldroid.ui.clientsettings.license.LicenseActivity
import de.kuschku.quasseldroid.util.helper.visibleIf

class LibraryAdapter(private val libraries: List<Library>) :
  RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LibraryViewHolder(
    WidgetLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
  )

  override fun getItemCount() = libraries.size

  override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
    holder.bind(libraries[position])
  }

  class LibraryViewHolder(
    private val binding: WidgetLibraryBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    private var item: Library? = null

    init {
      itemView.setOnClickListener {
        this.item?.run {
          LicenseActivity.launch(itemView.context,
                                 license_name = license.fullName,
                                 license_text = license.text)
        }
      }
    }

    fun bind(item: Library) {
      this.item = item
      binding.name.text = item.name
      binding.license.text = item.license.shortName
    }
  }
}
