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

package de.kuschku.quasseldroid.ui.coresettings.ignorelist

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.visibleIf
import java.util.*

class IgnoreListAdapter(
  private val clickListener: (IgnoreListManager.IgnoreListItem) -> Unit,
  private val dragListener: (IgnoreItemViewHolder) -> Unit
) : RecyclerView.Adapter<IgnoreListAdapter.IgnoreItemViewHolder>() {
  private val data = mutableListOf<IgnoreListManager.IgnoreListItem>()
  var list: List<IgnoreListManager.IgnoreListItem>
    get() = data
    set(value) {
      val length = data.size
      data.clear()
      notifyItemRangeRemoved(0, length)
      data.addAll(value)
      notifyItemRangeInserted(0, list.size)
    }

  fun add(item: IgnoreListManager.IgnoreListItem) {
    val index = data.size
    data.add(item)
    notifyItemInserted(index)
  }

  fun replace(index: Int, item: IgnoreListManager.IgnoreListItem) {
    data[index] = item
    notifyItemChanged(index)
  }

  fun indexOf(rule: String) = data.map(IgnoreListManager.IgnoreListItem::ignoreRule).indexOf(rule)

  fun remove(index: Int) {
    data.removeAt(index)
    notifyItemRemoved(index)
  }

  fun move(from: Int, to: Int) {
    Collections.swap(data, from, to)
    notifyItemMoved(from, to)
  }

  fun toggle(item: IgnoreListManager.IgnoreListItem, isActive: Boolean) {
    val index = indexOf(item.ignoreRule)
    data[index] = data[index].copy(isActive = isActive)
  }

  override fun getItemCount() = data.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = IgnoreItemViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.settings_ignorelist_item, parent, false),
    clickListener,
    ::toggle,
    dragListener
  )

  override fun onBindViewHolder(holder: IgnoreItemViewHolder, position: Int) {
    holder.bind(data[position])
  }

  class IgnoreItemViewHolder(
    itemView: View,
    clickListener: (IgnoreListManager.IgnoreListItem) -> Unit,
    toggleListener: (IgnoreListManager.IgnoreListItem, Boolean) -> Unit,
    dragListener: (IgnoreItemViewHolder) -> Unit
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.ignore_rule)
    lateinit var ignoreRule: TextView

    @BindView(R.id.scope_rule)
    lateinit var scopeRule: TextView

    @BindView(R.id.toggle)
    lateinit var toggle: SwitchCompat

    @BindView(R.id.handle)
    lateinit var handle: View

    private var item: IgnoreListManager.IgnoreListItem? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        item?.let {
          clickListener(it)
        }
      }
      toggle.setOnCheckedChangeListener { _, isChecked ->
        item?.let {
          toggleListener.invoke(it, isChecked)
        }
      }
      handle.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
          dragListener.invoke(this)
        }
        false
      }
    }

    fun bind(item: IgnoreListManager.IgnoreListItem) {
      this.item = item
      ignoreRule.text = item.ignoreRule
      scopeRule.text = item.scopeRule
      scopeRule.visibleIf(item.scopeRule.isNotBlank() && item.scope != IgnoreListManager.ScopeType.GlobalScope)
      toggle.isChecked = item.isActive
    }
  }
}
