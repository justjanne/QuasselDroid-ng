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

package de.kuschku.quasseldroid.ui.setup.core

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.coresetup.CoreSetupBackend
import de.kuschku.quasseldroid.R

class CoreBackendAdapter : RecyclerView.Adapter<CoreBackendAdapter.BackendViewHolder>() {
  private val selectionListeners = mutableSetOf<(CoreSetupBackend) -> Unit>()
  private var selectedItem: Pair<CoreSetupBackend?, CoreSetupBackend?> = Pair(null, null)

  fun selection() = selectedItem.second

  private val clickListener = { item: CoreSetupBackend ->
    selectionListener.invoke(item)
  }

  fun updateSelection(item: CoreSetupBackend) {
    selectedItem = Pair(selectedItem.second, item)
    submitList()
  }

  private val selectionListener = { item: CoreSetupBackend ->
    updateSelection(item)
    for (selectionListener in selectionListeners) {
      selectionListener.invoke(item)
    }
  }

  private var list: List<Pair<Boolean, CoreSetupBackend>> = emptyList()

  fun submitList(
    list: List<CoreSetupBackend> = this.list.map(Pair<Boolean, CoreSetupBackend>::second)) {
    val oldList = this.list

    val oldSelected = selectedItem.first
    val newSelected = selectedItem.second

    val newList = list.map {
      Pair(it == newSelected, it)
    }
    this.list = newList

    DiffUtil.calculateDiff(object : DiffUtil.Callback() {
      override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition].second
        val newItem = newList[newItemPosition].second

        return oldItem == newItem
      }

      override fun getOldListSize(): Int {
        return oldList.size
      }

      override fun getNewListSize(): Int {
        return newList.size
      }

      override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition].second
        val newItem = newList[newItemPosition].second

        return oldItem == newItem &&
               oldItem != newSelected &&
               newItem != newSelected &&
               oldItem != oldSelected &&
               newItem != oldSelected
      }
    }).dispatchUpdatesTo(this)
  }

  fun addSelectionListener(f: (CoreSetupBackend) -> Unit) {
    selectionListeners.add(f)
  }

  override fun onBindViewHolder(holder: BackendViewHolder, position: Int) {
    val item = list[position]
    holder.bind(item.second, item.first)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackendViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.widget_core_backend, parent, false)
    return BackendViewHolder(view, clickListener)
  }

  override fun getItemCount() = list.size

  class BackendViewHolder(itemView: View, clickListener: (CoreSetupBackend) -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.backend_name)
    lateinit var backendName: TextView

    @BindView(R.id.backend_description)
    lateinit var backendDescription: TextView

    @BindView(R.id.backend_select)
    lateinit var backendSelect: AppCompatRadioButton

    private var item: CoreSetupBackend? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        item?.let(clickListener)
      }
    }

    fun bind(backend: CoreSetupBackend, selected: Boolean) {
      item = backend
      backendName.text = backend.displayName
      backendDescription.text = backend.description
      backendSelect.isChecked = selected
    }

    fun clear() {
      item = null
      backendName.text = ""
      backendDescription.text = ""
      backendSelect.isChecked = false
    }
  }
}
