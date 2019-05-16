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

package de.kuschku.quasseldroid.util.ui

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.ThemedSpinnerAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerSpinnerAdapter<VH : RecyclerView.ViewHolder> : BaseAdapter(),
                                                                      ThemedSpinnerAdapter {
  private var dropDownViewTheme: Resources.Theme? = null
  override fun getDropDownViewTheme() = dropDownViewTheme
  override fun setDropDownViewTheme(theme: Resources.Theme?) {
    dropDownViewTheme = theme
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    val tag = convertView?.tag
    val holder: VH = tag as? VH ?: onCreateViewHolder(parent, true)
    holder.itemView.tag = holder
    onBindViewHolder(holder, position)
    return holder.itemView
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val tag = convertView?.tag
    val holder = tag as? VH ?: onCreateViewHolder(parent, false)
    holder.itemView.tag = holder
    onBindViewHolder(holder, position)
    return holder.itemView
  }

  protected abstract fun onBindViewHolder(holder: VH, position: Int)
  protected abstract fun onCreateViewHolder(parent: ViewGroup, dropDown: Boolean): VH
}
