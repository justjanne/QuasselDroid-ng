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

package de.kuschku.quasseldroid.ui.chat.buffers

import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.util.helper.*
import de.kuschku.quasseldroid.BuildConfig
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.db.AccountDatabase
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.chat.add.create.ChannelCreateActivity
import de.kuschku.quasseldroid.ui.chat.add.join.ChannelJoinActivity
import de.kuschku.quasseldroid.ui.chat.add.query.QueryCreateActivity
import de.kuschku.quasseldroid.ui.chat.archive.ArchiveActivity
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkEditActivity
import de.kuschku.quasseldroid.ui.info.channellist.ChannelListActivity
import de.kuschku.quasseldroid.util.ColorContext
import de.kuschku.quasseldroid.util.helper.setTooltip
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.presenter.BufferContextPresenter
import de.kuschku.quasseldroid.util.ui.presenter.BufferPresenter
import de.kuschku.quasseldroid.util.ui.view.WarningBarView
import de.kuschku.quasseldroid.viewmodel.helper.ChatViewModelHelper
import javax.inject.Inject

class BufferViewConfigFragment : ServiceBoundFragment() {
  @BindView(R.id.chatListToolbar)
  lateinit var chatListToolbar: Toolbar

  @BindView(R.id.chatListSpinner)
  lateinit var chatListSpinner: AppCompatSpinner

  @BindView(R.id.chatList)
  lateinit var chatList: RecyclerView

  @BindView(R.id.feature_context_bufferactivitysync)
  lateinit var featureContextBufferActivitySync: WarningBarView

  @BindView(R.id.buffer_search)
  lateinit var bufferSearch: EditText

  @BindView(R.id.buffer_search_clear)
  lateinit var bufferSearchClear: AppCompatImageButton

  @BindView(R.id.buffer_search_container)
  lateinit var bufferSearchContainer: ViewGroup

  @BindView(R.id.fab_chatlist)
  lateinit var fab: SpeedDialView

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var accountDatabase: AccountDatabase

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  @Inject
  lateinit var modelHelper: ChatViewModelHelper

  @Inject
  lateinit var colorContext: ColorContext

  @Inject
  lateinit var bufferPresenter: BufferPresenter

  private var actionMode: ActionMode? = null

  private val actionModeCallback = object : ActionMode.Callback {
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
      val selected = modelHelper.chat.selectedBufferId.value ?: BufferId(-1)
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
      mode?.tag = "BUFFERS"
      return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
      actionMode = null
      listAdapter.unselectAll()
    }
  }

  private lateinit var listAdapter: BufferListAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.chat_chatlist, container, false)
    ButterKnife.bind(this, view)

    val adapter = BufferViewConfigAdapter()
    modelHelper.bufferViewConfigs.safeSwitchMap {
      combineLatest(it.map(BufferViewConfig::liveUpdates))
    }.toLiveData().observe(this, Observer {
      if (it != null) {
        adapter.submitList(it)
      }
    })

    var hasSetBufferViewConfigId = false
    adapter.setOnUpdateFinishedListener {
      if (!hasSetBufferViewConfigId) {
        chatListSpinner.setSelection(adapter.indexOf(modelHelper.chat.bufferViewConfigId.or(-1)).nullIf { it == -1 }
                                     ?: 0)
        modelHelper.chat.bufferViewConfigId.onNext(chatListSpinner.selectedItemId.toInt())
        hasSetBufferViewConfigId = true
      }
    }
    chatListSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(adapter: AdapterView<*>?) {
        if (hasSetBufferViewConfigId)
          modelHelper.chat.bufferViewConfigId.onNext(-1)
      }

      override fun onItemSelected(adapter: AdapterView<*>?, element: View?, position: Int,
                                  id: Long) {
        if (hasSetBufferViewConfigId)
          modelHelper.chat.bufferViewConfigId.onNext(id.toInt())
      }
    }
    chatListSpinner.adapter = adapter

    listAdapter = BufferListAdapter(
      messageSettings,
      modelHelper.chat.selectedBufferId,
      modelHelper.chat.expandedNetworks
    )

    var chatListState: Parcelable? = savedInstanceState?.getParcelable(KEY_STATE_LIST)
    var hasRestoredChatListState = false
    listAdapter.setOnUpdateFinishedListener {
      if (it.isNotEmpty()) {
        chatList.layoutManager?.let {
          if (chatListState != null) {
            it.onRestoreInstanceState(chatListState)
            hasRestoredChatListState = true
          }
        }
      }
    }

    modelHelper.negotiatedFeatures.toLiveData().observe(this, Observer { (connected, features) ->
      featureContextBufferActivitySync.setMode(
        if (!connected || features.hasFeature(ExtendedFeature.BufferActivitySync)) WarningBarView.MODE_NONE
        else WarningBarView.MODE_ICON
      )
    })

    combineLatest(
      modelHelper.processedBufferList,
      database.filtered().listenRx(accountId).toObservable(),
      accountDatabase.accounts().listenDefaultFiltered(accountId, 0).toObservable()
    ).map { (buffers, filteredList, defaultFiltered) ->
      bufferPresenter.render(buffers, filteredList, defaultFiltered.toUInt())
    }.toLiveData().observe(this, Observer { processedList ->
      if (hasRestoredChatListState) {
        chatListState = chatList.layoutManager?.onSaveInstanceState()
        hasRestoredChatListState = false
      }
      listAdapter.submitList(processedList)
    })

    listAdapter.setOnClickListener(this@BufferViewConfigFragment::clickListener)
    listAdapter.setOnLongClickListener(this@BufferViewConfigFragment::longClickListener)
    chatList.adapter = listAdapter

    modelHelper.selectedBuffer.toLiveData().observe(this, Observer { buffer ->
      actionMode?.let {
        BufferContextPresenter.present(it, buffer)
      }
    })

    chatListToolbar.inflateMenu(R.menu.context_bufferlist)
    chatListToolbar.menu.findItem(R.id.action_search).isChecked = modelHelper.chat.bufferSearchTemporarilyVisible.or(
      false)
    chatListToolbar.setOnMenuItemClickListener { item ->
      when (item.itemId) {
        R.id.action_archived_chats -> {
          context?.let {
            modelHelper.chat.bufferViewConfigId.value?.let { chatlistId ->
              ArchiveActivity.launch(
                it,
                chatlistId = chatlistId
              )
            }
          }
          true
        }
        R.id.action_search         -> {
          item.isChecked = !item.isChecked
          modelHelper.chat.bufferSearchTemporarilyVisible.onNext(item.isChecked)
          true
        }
        else                       -> false
      }
    }
    chatList.layoutManager = LinearLayoutManager(context)
    chatList.itemAnimator = DefaultItemAnimator()
    chatList.setItemViewCacheSize(10)

    modelHelper.chat.stateReset.toLiveData().observe(this, Observer {
      listAdapter.submitList(emptyList())
      hasSetBufferViewConfigId = false
    })

    val bufferSearchPermanentlyVisible = modelHelper.bufferViewConfig
      .mapMap(BufferViewConfig::showSearch)
      .mapOrElse(false)

    combineLatest(modelHelper.chat.bufferSearchTemporarilyVisible.distinctUntilChanged(),
                  bufferSearchPermanentlyVisible)
      .toLiveData().observe(this, Observer { (temporarily, permanently) ->
        val visible = temporarily || permanently

        val menuItem = chatListToolbar.menu.findItem(R.id.action_search)
        menuItem.isVisible = !permanently
        if (permanently) menuItem.isChecked = false
        else menuItem.isChecked = temporarily

        bufferSearchContainer.visibleIf(visible)
        if (!visible) bufferSearch.setText("")
      })

    bufferSearch.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable) {
        modelHelper.chat.bufferSearch.onNext(s.toString())
      }

      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
    })

    bufferSearchClear.setTooltip()

    bufferSearchClear.setOnClickListener {
      bufferSearch.setText("")
    }

    @ColorInt var colorLabel = 0
    @ColorInt var colorLabelBackground = 0

    @ColorInt var fabBackground0 = 0
    @ColorInt var fabBackground1 = 0
    @ColorInt var fabBackground2 = 0
    view.context.theme.styledAttributes(
      R.attr.colorTextPrimary, R.attr.colorBackgroundCard,
      R.attr.senderColorE, R.attr.senderColorD, R.attr.senderColorC
    ) {
      colorLabel = getColor(0, 0)
      colorLabelBackground = getColor(1, 0)

      fabBackground0 = getColor(2, 0)
      fabBackground1 = getColor(3, 0)
      fabBackground2 = getColor(4, 0)
    }

    fab.addActionItem(
      SpeedDialActionItem.Builder(R.id.fab_create, R.drawable.ic_add)
        .setFabBackgroundColor(fabBackground0)
        .setFabImageTintColor(0xffffffffu.toInt())
        .setLabel(R.string.label_create_channel)
        .setLabelBackgroundColor(colorLabelBackground)
        .setLabelColor(colorLabel)
        .create()
    )

    fab.addActionItem(
      SpeedDialActionItem.Builder(R.id.fab_join, R.drawable.ic_channel)
        .setFabBackgroundColor(fabBackground1)
        .setFabImageTintColor(0xffffffffu.toInt())
        .setLabel(R.string.label_join_long)
        .setLabelBackgroundColor(colorLabelBackground)
        .setLabelColor(colorLabel)
        .create()
    )

    fab.addActionItem(
      SpeedDialActionItem.Builder(R.id.fab_query, R.drawable.ic_account)
        .setFabBackgroundColor(fabBackground2)
        .setFabImageTintColor(0xffffffffu.toInt())
        .setLabel(R.string.label_query_medium)
        .setLabelBackgroundColor(colorLabelBackground)
        .setLabelColor(colorLabel)
        .create()
    )

    fab.setOnActionSelectedListener {
      val networkId = modelHelper.bufferData.value?.network?.networkId()
      when (it.id) {
        R.id.fab_query  -> {
          context?.let {
            QueryCreateActivity.launch(it, networkId = networkId)
          }
          fab.close(false)
          true
        }
        R.id.fab_join   -> {
          context?.let {
            ChannelJoinActivity.launch(it, networkId = networkId)
          }
          fab.close(false)
          true
        }
        R.id.fab_create -> {
          context?.let {
            ChannelCreateActivity.launch(it, networkId = networkId)
          }
          fab.close(false)
          true
        }
        else            -> false
      }
    }

    fab.visibleIf(BuildConfig.DEBUG)

    return view
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(KEY_STATE_LIST, chatList.layoutManager?.onSaveInstanceState())
  }

  private fun toggleSelection(buffer: BufferId): Boolean {
    val next = if (modelHelper.chat.selectedBufferId.value == buffer) BufferId.MAX_VALUE else buffer
    modelHelper.chat.selectedBufferId.onNext(next)
    return next != BufferId.MAX_VALUE
  }

  private fun clickListener(bufferId: BufferId) {
    if (actionMode != null) {
      longClickListener(bufferId)
    } else {
      context?.let {
        modelHelper.chat.bufferSearchTemporarilyVisible.onNext(false)
        ChatActivity.launch(it, bufferId = bufferId)
      }
    }
  }

  private fun longClickListener(it: BufferId) {
    if (actionMode == null) {
      chatListToolbar.startActionMode(actionModeCallback)
    }
    if (!toggleSelection(it)) {
      actionMode?.finish()
    }
  }

  companion object {
    private const val KEY_STATE_LIST = "STATE_LIST"
  }
}
