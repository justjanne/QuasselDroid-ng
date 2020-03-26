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

package de.kuschku.quasseldroid.ui.coresettings.identity

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.quasseldroid.databinding.SettingsIdentityNickBinding
import java.util.*

class IdentityNicksAdapter(
  private val clickListener: (Int, String) -> Unit,
  private val dragListener: (IdentityNickViewHolder) -> Unit
) :
  RecyclerView.Adapter<IdentityNicksAdapter.IdentityNickViewHolder>() {
  private val data = mutableListOf<String>()
  var nicks: List<String>
    get() = data
    set(value) {
      val length = data.size
      data.clear()
      notifyItemRangeRemoved(0, length)
      data.addAll(value)
      notifyItemRangeInserted(0, nicks.size)
    }

  fun addNick(nick: String) {
    val index = data.size
    data.add(nick)
    notifyItemInserted(index)
  }

  fun replaceNick(index: Int, nick: String) {
    data[index] = nick
    notifyItemChanged(index)
  }

  fun removeNick(index: Int) {
    data.removeAt(index)
    notifyItemRemoved(index)
  }

  fun moveNick(from: Int, to: Int) {
    Collections.swap(data, from, to)
    notifyItemMoved(from, to)
  }

  override fun getItemCount() = data.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = IdentityNickViewHolder(
    SettingsIdentityNickBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    clickListener,
    dragListener
  )

  override fun onBindViewHolder(holder: IdentityNickViewHolder, position: Int) {
    holder.bind(data[position])
  }

  class IdentityNickViewHolder(
    private val binding: SettingsIdentityNickBinding,
    private val clickListener: (Int, String) -> Unit,
    dragListener: (IdentityNickViewHolder) -> Unit
  ) : RecyclerView.ViewHolder(binding.root) {
    private var item: String? = null

    init {
      itemView.setOnClickListener {
        item?.let {
          clickListener(adapterPosition, it)
        }
      }
      binding.handle.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
          dragListener.invoke(this)
        }
        false
      }
    }

    fun bind(item: String) {
      this.item = item
      binding.nick.text = item
    }
  }
}
