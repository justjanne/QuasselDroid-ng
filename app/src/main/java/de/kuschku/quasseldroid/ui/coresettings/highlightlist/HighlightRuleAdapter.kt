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

package de.kuschku.quasseldroid.ui.coresettings.highlightlist

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.HighlightRuleManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.visibleIf
import java.util.*

class HighlightRuleAdapter(
  private val clickListener: (HighlightRuleManager.HighlightRule) -> Unit,
  private val dragListener: (HighlightRuleViewHolder) -> Unit
) : RecyclerView.Adapter<HighlightRuleAdapter.HighlightRuleViewHolder>() {
  private val data = mutableListOf<HighlightRuleManager.HighlightRule>()
  var list: List<HighlightRuleManager.HighlightRule>
    get() = data
    set(value) {
      val length = data.size
      data.clear()
      notifyItemRangeRemoved(0, length)
      data.addAll(value)
      notifyItemRangeInserted(0, list.size)
    }

  fun add(item: HighlightRuleManager.HighlightRule) {
    val index = data.size
    data.add(item)
    notifyItemInserted(index)
  }

  fun replace(index: Int, item: HighlightRuleManager.HighlightRule) {
    data[index] = item
    notifyItemChanged(index)
  }

  fun indexOf(rule: String) = data.map(HighlightRuleManager.HighlightRule::name).indexOf(rule)

  fun remove(index: Int) {
    data.removeAt(index)
    notifyItemRemoved(index)
  }

  fun move(from: Int, to: Int) {
    Collections.swap(data, from, to)
    notifyItemMoved(from, to)
  }

  fun toggle(item: HighlightRuleManager.HighlightRule, isEnabled: Boolean) {
    val index = indexOf(item.name)
    data[index] = data[index].copy(isEnabled = isEnabled)
  }

  override fun getItemCount() = data.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HighlightRuleViewHolder(
    LayoutInflater.from(parent.context)
      .inflate(R.layout.settings_highlightlist_rule, parent, false),
    clickListener,
    ::toggle,
    dragListener
  )

  override fun onBindViewHolder(holder: HighlightRuleViewHolder, position: Int) {
    holder.bind(data[position])
  }

  class HighlightRuleViewHolder(
    itemView: View,
    clickListener: (HighlightRuleManager.HighlightRule) -> Unit,
    toggleListener: (HighlightRuleManager.HighlightRule, Boolean) -> Unit,
    dragListener: (HighlightRuleViewHolder) -> Unit
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.name)
    lateinit var name: TextView

    @BindView(R.id.sender)
    lateinit var sender: TextView

    @BindView(R.id.channel)
    lateinit var channel: TextView

    @BindView(R.id.toggle)
    lateinit var toggle: SwitchCompat

    @BindView(R.id.handle)
    lateinit var handle: View

    private var item: HighlightRuleManager.HighlightRule? = null

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

    fun bind(item: HighlightRuleManager.HighlightRule) {
      this.item = item
      name.text = item.name
      sender.text = item.sender
      sender.visibleIf(item.sender.isNotBlank())
      channel.text = item.channel
      channel.visibleIf(item.channel.isNotBlank())
      toggle.isChecked = item.isEnabled
    }
  }
}
