/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.quasseldroid.ui.clientsettings.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.clientsettings.license.LicenseActivity
import de.kuschku.quasseldroid.util.helper.visibleIf

class LibraryAdapter(private val libraries: List<Library>) :
  RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LibraryViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.widget_library, parent, false)
  )

  override fun getItemCount() = libraries.size

  override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
    holder.bind(libraries[position])
  }

  class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.name)
    lateinit var name: TextView

    @BindView(R.id.version)
    lateinit var version: TextView

    @BindView(R.id.license)
    lateinit var license: TextView

    private var item: Library? = null

    init {
      ButterKnife.bind(this, itemView)
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
      this.name.text = item.name
      this.version.text = item.version
      this.version.visibleIf(!item.version.isNullOrBlank())
      this.license.text = item.license.shortName
    }
  }
}
