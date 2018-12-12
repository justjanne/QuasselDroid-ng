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

package de.kuschku.quasseldroid.ui.coresettings.aliaslist

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import java.util.*
import javax.inject.Inject

class AliasListAdapter @Inject constructor(
  private val formatDeserializer: IrcFormatDeserializer
) : RecyclerView.Adapter<AliasListAdapter.AliasItemViewHolder>() {
  private var clickListener: ((IAliasManager.Alias) -> Unit)? = null
  private var dragListener: ((AliasItemViewHolder) -> Unit)? = null

  fun setOnClickListener(listener: ((IAliasManager.Alias) -> Unit)?) {
    clickListener = listener
  }

  fun setOnDragListener(listener: ((AliasItemViewHolder) -> Unit)?) {
    dragListener = listener
  }

  private val data = mutableListOf<IAliasManager.Alias>()
  var list: List<IAliasManager.Alias>
    get() = data
    set(value) {
      val length = data.size
      data.clear()
      notifyItemRangeRemoved(0, length)
      data.addAll(value)
      notifyItemRangeInserted(0, list.size)
    }

  fun add(item: IAliasManager.Alias) {
    val index = data.size
    data.add(item)
    notifyItemInserted(index)
  }

  fun replace(index: Int, item: IAliasManager.Alias) {
    data[index] = item
    notifyItemChanged(index)
  }

  fun indexOf(name: String?) = data.map(IAliasManager.Alias::name).indexOf(name)

  fun remove(index: Int) {
    data.removeAt(index)
    notifyItemRemoved(index)
  }

  fun move(from: Int, to: Int) {
    Collections.swap(data, from, to)
    notifyItemMoved(from, to)
  }

  override fun getItemCount() = data.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AliasItemViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.settings_aliaslist_item, parent, false),
    formatDeserializer,
    clickListener,
    dragListener
  )

  override fun onBindViewHolder(holder: AliasItemViewHolder, position: Int) {
    holder.bind(data[position])
  }

  class AliasItemViewHolder(
    itemView: View,
    private val formatDeserializer: IrcFormatDeserializer,
    clickListener: ((IAliasManager.Alias) -> Unit)?,
    dragListener: ((AliasItemViewHolder) -> Unit)?
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.name)
    lateinit var name: TextView

    @BindView(R.id.expansion)
    lateinit var expansion: TextView

    @BindView(R.id.handle)
    lateinit var handle: View

    private var item: IAliasManager.Alias? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        item?.let {
          clickListener?.invoke(it)
        }
      }
      handle.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
          dragListener?.invoke(this)
        }
        false
      }
    }

    fun bind(item: IAliasManager.Alias) {
      this.item = item
      name.text = item.name
      expansion.text = formatDeserializer.formatString(item.expansion, true)
    }
  }
}
