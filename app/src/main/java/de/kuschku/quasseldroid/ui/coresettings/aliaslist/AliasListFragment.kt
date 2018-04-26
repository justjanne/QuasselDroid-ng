/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
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

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.AliasManager
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.ui.coresettings.aliasitem.AliasItemActivity
import de.kuschku.quasseldroid.util.helper.toLiveData
import javax.inject.Inject

class AliasListFragment : SettingsFragment(), SettingsFragment.Savable,
                          SettingsFragment.Changeable {
  @BindView(R.id.list)
  lateinit var list: RecyclerView

  @BindView(R.id.add)
  lateinit var add: FloatingActionButton

  @Inject
  lateinit var adapter: AliasListAdapter

  private var aliasManager: Pair<AliasManager, AliasManager>? = null

  private lateinit var helper: ItemTouchHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_ignorelist, container, false)
    ButterKnife.bind(this, view)

    adapter.setOnClickListener(::itemClick)
    adapter.setOnDragListener(::startDrag)

    list.adapter = adapter
    list.layoutManager = LinearLayoutManager(requireContext())
    list.itemAnimator = DefaultItemAnimator()
    list.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

    val callback = DragSortItemTouchHelperCallback(adapter)
    helper = ItemTouchHelper(callback)
    helper.attachToRecyclerView(list)

    add.setOnClickListener {
      startActivityForResult(AliasItemActivity.intent(requireContext()), REQUEST_CREATE_ITEM)
    }

    viewModel.aliasManager
      .filter(Optional<AliasManager>::isPresent)
      .map(Optional<AliasManager>::get)
      .toLiveData().observe(this, Observer {
        if (it != null) {
          if (this.aliasManager == null) {
            this.aliasManager = Pair(it, it.copy())
            this.aliasManager?.let { (_, data) ->
              adapter.list = data.aliasList()
            }
          }
        }
      })

    return view
  }

  private fun itemClick(item: IAliasManager.Alias) {
    startActivityForResult(AliasItemActivity.intent(requireContext(), item), REQUEST_UPDATE_ITEM)
  }

  private fun startDrag(holder: AliasListAdapter.AliasItemViewHolder) = helper.startDrag(holder)

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK && data != null) {
      when (requestCode) {
        REQUEST_UPDATE_ITEM -> {
          val oldRule = data.getSerializableExtra("old") as? IAliasManager.Alias
          val newRule = data.getSerializableExtra("new") as? IAliasManager.Alias

          if (oldRule != null && newRule != null) {
            adapter.replace(adapter.indexOf(oldRule.name), newRule)
          }
        }
        REQUEST_CREATE_ITEM -> {
          val newRule = data.getSerializableExtra("new") as? IAliasManager.Alias

          if (newRule != null) {
            adapter.add(newRule)
          }
        }
      }
    }
  }

  override fun onSave() = aliasManager?.let { (it, data) ->
    applyChanges(data)
    it.requestUpdate(data.toVariantMap())
    true
  } ?: false

  override fun hasChanged() = aliasManager?.let { (it, data) ->
    applyChanges(data)
    !data.isEqual(it)
  } ?: false

  private fun applyChanges(data: AliasManager) {
    data.setAliasList(adapter.list)
  }

  companion object {
    private const val REQUEST_UPDATE_ITEM = 1
    private const val REQUEST_CREATE_ITEM = 2
  }
}
