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
import de.kuschku.quasseldroid.databinding.WidgetTranslatorBinding

class TranslatorAdapter(private val translators: List<Translator>) :
  RecyclerView.Adapter<TranslatorAdapter.TranslatorViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TranslatorViewHolder(
    WidgetTranslatorBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    )
  )

  override fun getItemCount() = translators.size

  override fun onBindViewHolder(holder: TranslatorViewHolder, position: Int) {
    holder.bind(translators[position])
  }

  class TranslatorViewHolder(
    private val binding: WidgetTranslatorBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Translator) {
      binding.name.text = item.name
      binding.language.text = itemView.resources.getString(item.language)
    }
  }
}
