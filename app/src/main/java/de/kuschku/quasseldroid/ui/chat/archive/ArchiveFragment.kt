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

package de.kuschku.quasseldroid.ui.chat.archive

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.db.AccountDatabase
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.chat.buffers.BufferListAdapter
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.presenter.BufferContextPresenter
import de.kuschku.quasseldroid.util.ui.presenter.BufferPresenter
import de.kuschku.quasseldroid.viewmodel.data.BufferHiddenState
import de.kuschku.quasseldroid.viewmodel.helper.ArchiveViewModelHelper
import javax.inject.Inject

class ArchiveFragment : ServiceBoundFragment() {
  @BindView(R.id.list_temporary)
  lateinit var listTemporary: RecyclerView

  @BindView(R.id.list_permanently)
  lateinit var listPermanently: RecyclerView

  @Inject
  lateinit var modelHelper: ArchiveViewModelHelper

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var accountDatabase: AccountDatabase

  @Inject
  lateinit var bufferPresenter: BufferPresenter

  @Inject
  lateinit var messageSettings: MessageSettings

  private lateinit var listTemporaryAdapter: BufferListAdapter

  private lateinit var listPermanentlyAdapter: BufferListAdapter

  private var actionMode: ActionMode? = null

  private val actionModeCallback = object : ActionMode.Callback {
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
      val selected = modelHelper.archive.selectedBufferId.value ?: BufferId(-1)
      val session = modelHelper.connectedSession.value?.orNull()
      val bufferSyncer = session?.bufferSyncer
      val info = bufferSyncer?.bufferInfo(selected)
      val network = session?.networks?.get(info?.networkId)
      val bufferViewConfig = modelHelper.bufferViewConfig.value?.orNull()

      return if (info != null) {
        BufferContextPresenter.handleAction(
          requireContext(),
          mode,
          item,
          info,
          session,
          bufferSyncer,
          bufferViewConfig,
          network
        )
      } else {
        false
      }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
      actionMode = mode
      mode?.menuInflater?.inflate(R.menu.context_buffer, menu)
      return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
      mode?.tag = "ARCHIVE"
      return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
      actionMode = null
      listTemporaryAdapter.unselectAll()
      listPermanentlyAdapter.unselectAll()
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.chat_archive, container, false)
    ButterKnife.bind(this, view)

    val chatlistId = arguments?.getInt("chatlist_id", -1) ?: -1
    modelHelper.archive.bufferViewConfigId.onNext(chatlistId)

    listTemporaryAdapter = BufferListAdapter(
      messageSettings,
      modelHelper.archive.selectedBufferId,
      modelHelper.archive.temporarilyExpandedNetworks
    )
    listTemporaryAdapter.setOnClickListener(::clickListener)
    listTemporaryAdapter.setOnLongClickListener(::longClickListener)
    listTemporary.adapter = listTemporaryAdapter
    listTemporary.layoutManager = LinearLayoutManager(listTemporary.context)
    listTemporary.itemAnimator = DefaultItemAnimator()

    listPermanentlyAdapter = BufferListAdapter(
      messageSettings,
      modelHelper.archive.selectedBufferId,
      modelHelper.archive.permanentlyExpandedNetworks
    )
    listPermanentlyAdapter.setOnClickListener(::clickListener)
    listPermanentlyAdapter.setOnLongClickListener(::longClickListener)
    listPermanently.adapter = listPermanentlyAdapter
    listPermanently.layoutManager = LinearLayoutManager(listPermanently.context)
    listPermanently.itemAnimator = DefaultItemAnimator()

    fun processArchiveBufferList(bufferListType: BufferHiddenState, showHandle: Boolean) =
      combineLatest(
        modelHelper.processArchiveBufferList(bufferListType, showHandle),
        database.filtered().listenRx(accountId).toObservable(),
        accountDatabase.accounts().listenDefaultFiltered(accountId, 0).toObservable()
      ).map { (buffers, filteredList, defaultFiltered) ->
        bufferPresenter.render(buffers, filteredList, defaultFiltered.toUInt())
      }

    processArchiveBufferList(BufferHiddenState.HIDDEN_TEMPORARY, false)
      .toLiveData().observe(this, Observer { processedList ->
        listTemporaryAdapter.submitList(processedList)
      })

    processArchiveBufferList(BufferHiddenState.HIDDEN_PERMANENT, false)
      .toLiveData().observe(this, Observer { processedList ->
        listPermanentlyAdapter.submitList(processedList)
      })

    modelHelper.selectedBuffer.toLiveData().observe(this, Observer { buffer ->
      actionMode?.let {
        BufferContextPresenter.present(it, buffer)
      }
    })

    return view
  }

  private fun toggleSelection(buffer: BufferId): Boolean {
    val next = if (modelHelper.archive.selectedBufferId.value == buffer) BufferId.MAX_VALUE else buffer
    modelHelper.archive.selectedBufferId.onNext(next)
    return next != BufferId.MAX_VALUE
  }

  private fun clickListener(bufferId: BufferId) {
    if (actionMode != null) {
      longClickListener(bufferId)
    } else {
      context?.let {
        ChatActivity.launch(it, bufferId = bufferId)
      }
    }
  }

  private fun longClickListener(it: BufferId) {
    if (actionMode == null) {
      (activity as? AppCompatActivity)?.startActionMode(actionModeCallback)
    }
    if (!toggleSelection(it)) {
      actionMode?.finish()
    }
  }
}
