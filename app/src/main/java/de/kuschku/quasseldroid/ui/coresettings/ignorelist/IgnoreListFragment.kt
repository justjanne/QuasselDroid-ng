package de.kuschku.quasseldroid.ui.coresettings.ignorelist

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.ui.coresettings.ignoreitem.IgnoreItemActivity
import de.kuschku.quasseldroid.util.helper.toLiveData

class IgnoreListFragment : SettingsFragment() {
  @BindView(R.id.list)
  lateinit var list: RecyclerView

  @BindView(R.id.add)
  lateinit var add: FloatingActionButton

  private var ignoreListManager: Pair<IgnoreListManager, IgnoreListManager>? = null

  lateinit var helper: ItemTouchHelper

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
          this.ignoreListManager = Pair(it, it.copy())
          this.ignoreListManager?.let { (_, data) ->
            if (adapter.list.isEmpty()) adapter.list = data.ignoreList()
          }
        }
      })

    return view
  }

  fun itemClick(item: IgnoreListManager.IgnoreListItem) {
    startActivityForResult(IgnoreItemActivity.intent(requireContext(), item), REQUEST_UPDATE_RULE)
  }

  fun startDrag(holder: IgnoreListAdapter.IgnoreItemViewHolder) = helper.startDrag(holder)

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK && data != null) {
      when (requestCode) {
        REQUEST_UPDATE_RULE -> {
          val oldRule = data.getSerializableExtra("old") as? IgnoreListManager.IgnoreListItem
          val newRule = data.getSerializableExtra("new") as? IgnoreListManager.IgnoreListItem

          if (oldRule != null && newRule != null) {
            val index = adapter.indexOf(oldRule.ignoreRule)
            adapter.replace(index, newRule)
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
    data.setIgnoreList(adapter.list)
    it.requestUpdate(data.toVariantMap())
    true
  } ?: false

  companion object {
    private const val REQUEST_UPDATE_RULE = 1
    private const val REQUEST_CREATE_RULE = 2
  }
}
