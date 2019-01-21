/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.quasseldroid.ui.coresettings.ignorelist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.ignoreitem.IgnoreItemActivity
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.ui.settings.fragment.Changeable
import de.kuschku.quasseldroid.util.ui.settings.fragment.Savable
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment

class IgnoreListFragment : ServiceBoundSettingsFragment(), Savable,
                           Changeable {
  @BindView(R.id.list)
  lateinit var list: RecyclerView

  @BindView(R.id.add)
  lateinit var add: FloatingActionButton

  private var ignoreListManager: Pair<IgnoreListManager, IgnoreListManager>? = null

  private lateinit var helper: ItemTouchHelper

  private val adapter = IgnoreListAdapter(::itemClick, ::startDrag)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_ignorelist, container, false)
    ButterKnife.bind(this, view)

    list.adapter = adapter
    list.layoutManager = LinearLayoutManager(requireContext())
    list.itemAnimator = DefaultItemAnimator()

    val callback = DragSortItemTouchHelperCallback(adapter)
    helper = ItemTouchHelper(callback)
    helper.attachToRecyclerView(list)

    add.setOnClickListener {
      startActivityForResult(IgnoreItemActivity.intent(requireContext()), REQUEST_CREATE_RULE)
    }

    viewModel.ignoreListManager
      .filter(Optional<IgnoreListManager>::isPresent)
      .map(Optional<IgnoreListManager>::get)
      .toLiveData().observe(this, Observer {
        if (it != null) {
          if (this.ignoreListManager == null) {
            this.ignoreListManager = Pair(it, it.copy())
            this.ignoreListManager?.let { (_, data) ->
              adapter.list = data.ignoreList()
            }
          }
        }
      })

    return view
  }

  private fun itemClick(item: IgnoreListManager.IgnoreListItem) {
    startActivityForResult(IgnoreItemActivity.intent(requireContext(), item), REQUEST_UPDATE_RULE)
  }

  private fun startDrag(holder: IgnoreListAdapter.IgnoreItemViewHolder) = helper.startDrag(holder)

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK && data != null) {
      when (requestCode) {
        REQUEST_UPDATE_RULE -> {
          val oldRule = data.getSerializableExtra("old") as? IgnoreListManager.IgnoreListItem
          val newRule = data.getSerializableExtra("new") as? IgnoreListManager.IgnoreListItem

          if (oldRule != null && newRule != null) {
            adapter.replace(adapter.indexOf(oldRule.ignoreRule), newRule)
          }
        }
        REQUEST_CREATE_RULE -> {
          val newRule = data.getSerializableExtra("new") as? IgnoreListManager.IgnoreListItem

          if (newRule != null) {
            adapter.add(newRule)
          }
        }
      }
    }
  }

  override fun onSave() = ignoreListManager?.let { (it, data) ->
    applyChanges(data)
    it.requestUpdate(data.toVariantMap())
    true
  } ?: false

  override fun hasChanged() = ignoreListManager?.let { (it, data) ->
    applyChanges(data)
    !data.isEqual(it)
  } ?: false

  private fun applyChanges(data: IgnoreListManager) {
    data.setIgnoreList(adapter.list)
  }

  companion object {
    private const val REQUEST_UPDATE_RULE = 1
    private const val REQUEST_CREATE_RULE = 2
  }
}
