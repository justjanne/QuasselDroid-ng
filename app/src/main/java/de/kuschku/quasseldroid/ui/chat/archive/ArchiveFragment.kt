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
import de.kuschku.quasseldroid.persistence.models.Filtered
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.presenter.BufferContextPresenter
import de.kuschku.quasseldroid.util.ui.presenter.BufferPresenter
import de.kuschku.quasseldroid.viewmodel.data.BufferHiddenState
import de.kuschku.quasseldroid.viewmodel.helper.ArchiveViewModelHelper
import javax.inject.Inject

class ArchiveFragment : ServiceBoundFragment() {
  @BindView(R.id.list)
  lateinit var list: RecyclerView

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

  private lateinit var listAdapter: ArchiveListAdapter

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
      listAdapter.unselectAll()
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.chat_archive, container, false)
    ButterKnife.bind(this, view)

    val chatlistId = arguments?.getInt("chatlist_id", -1) ?: -1
    modelHelper.archive.bufferViewConfigId.onNext(chatlistId)

    listAdapter = ArchiveListAdapter(
      messageSettings,
      modelHelper.archive.selectedBufferId,
      modelHelper.archive.temporarilyExpandedNetworks
    )
    listAdapter.setOnClickListener(::clickListener)
    listAdapter.setOnLongClickListener(::longClickListener)
    list.adapter = listAdapter
    list.layoutManager = LinearLayoutManager(list.context)
    list.itemAnimator = DefaultItemAnimator()

    val filtered = combineLatest(
      database.filtered().listenRx(accountId).toObservable().map {
        it.associateBy(Filtered::bufferId, Filtered::filtered)
      },
      accountDatabase.accounts().listenDefaultFiltered(accountId, 0).toObservable()
    )

    combineLatest(
      modelHelper.processArchiveBufferList(BufferHiddenState.HIDDEN_TEMPORARY, false, filtered),
      modelHelper.processArchiveBufferList(BufferHiddenState.HIDDEN_PERMANENT, false, filtered)
    ).map { (temporary, permanently) ->
      listOf(ArchiveListItem.Header(
        title = getString(R.string.label_temporarily_archived),
        content = getString(R.string.label_temporarily_archived_long)
      )) + temporary.map {
        ArchiveListItem.Buffer(it.copy(
          props = bufferPresenter.render(it.props)
        ))
      }.ifEmpty {
        listOf(ArchiveListItem.Placeholder(
          content = getString(R.string.label_temporarily_archived_empty)
        ))
      } + listOf(ArchiveListItem.Header(
        title = getString(R.string.label_permanently_archived),
        content = getString(R.string.label_permanently_archived_long)
      )) + permanently.map {
        ArchiveListItem.Buffer(it.copy(
          props = bufferPresenter.render(it.props)
        ))
      }.ifEmpty {
        listOf(ArchiveListItem.Placeholder(
          content = getString(R.string.label_permanently_archived_empty)
        ))
      }
    }.toLiveData().observe(this, Observer { processedList ->
      listAdapter.submitList(processedList)
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
