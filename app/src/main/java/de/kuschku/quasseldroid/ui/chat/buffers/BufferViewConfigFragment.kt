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

package de.kuschku.quasseldroid.ui.chat.buffers

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.*
import android.view.*
import android.widget.AdapterView
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.flag.minus
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkEditActivity
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.helper.zip
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.viewmodel.data.BufferHiddenState
import de.kuschku.quasseldroid.viewmodel.data.BufferListItem
import de.kuschku.quasseldroid.viewmodel.data.BufferState
import javax.inject.Inject

class BufferViewConfigFragment : ServiceBoundFragment() {
  @BindView(R.id.chatListToolbar)
  lateinit var chatListToolbar: Toolbar

  @BindView(R.id.chatListSpinner)
  lateinit var chatListSpinner: AppCompatSpinner

  @BindView(R.id.chatList)
  lateinit var chatList: RecyclerView

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  private var actionMode: ActionMode? = null

  private val actionModeCallback = object : ActionMode.Callback {
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
      val selected = viewModel.selectedBuffer.value
      val info = selected?.info
      val session = viewModel.session.value?.orNull()
      val bufferSyncer = session?.bufferSyncer
      val network = session?.networks?.get(selected?.info?.networkId)
      val bufferViewConfig = viewModel.bufferViewConfig.value

      return if (info != null && session != null) {
        when (item?.itemId) {
          R.id.action_configure  -> {
            network?.let {
              NetworkEditActivity.launch(requireContext(), network = it.networkId())
            }
            actionMode?.finish()
            true
          }
          R.id.action_connect    -> {
            network?.requestConnect()
            actionMode?.finish()
            true
          }
          R.id.action_disconnect -> {
            network?.requestDisconnect()
            actionMode?.finish()
            true
          }
          R.id.action_join       -> {
            session.rpcHandler?.sendInput(info, "/join ${info.bufferName}")
            actionMode?.finish()
            true
          }
          R.id.action_part       -> {
            session.rpcHandler?.sendInput(info, "/part ${info.bufferName}")
            actionMode?.finish()
            true
          }
          R.id.action_delete     -> {
            MaterialDialog.Builder(activity!!)
              .content(R.string.buffer_delete_confirmation)
              .positiveText(R.string.label_yes)
              .negativeText(R.string.label_no)
              .negativeColorAttr(R.attr.colorTextPrimary)
              .backgroundColorAttr(R.attr.colorBackgroundCard)
              .contentColorAttr(R.attr.colorTextPrimary)
              .onPositive { _, _ ->
                selected.info?.let {
                  session.bufferSyncer?.requestRemoveBuffer(info.bufferId)
                }
              }
              .onAny { _, _ ->
                actionMode?.finish()
              }
              .build()
              .show()
            true
          }
          R.id.action_rename     -> {
            MaterialDialog.Builder(activity!!)
              .input(
                getString(R.string.label_buffer_name),
                info.bufferName,
                false
              ) { _, input ->
                selected.info?.let {
                  session.bufferSyncer?.requestRenameBuffer(info.bufferId, input.toString())
                }
              }
              .positiveText(R.string.label_save)
              .negativeText(R.string.label_cancel)
              .negativeColorAttr(R.attr.colorTextPrimary)
              .backgroundColorAttr(R.attr.colorBackgroundCard)
              .contentColorAttr(R.attr.colorTextPrimary)
              .onAny { _, _ ->
                actionMode?.finish()
              }
              .build()
              .show()
            true
          }
          R.id.action_unhide     -> {
            bufferSyncer?.let {
              bufferViewConfig?.orNull()?.insertBufferSorted(info, bufferSyncer)
            }
            actionMode?.finish()
            true
          }
          R.id.action_hide_temp  -> {
            bufferViewConfig?.orNull()?.requestRemoveBuffer(info.bufferId)
            actionMode?.finish()
            true
          }
          R.id.action_hide_perm  -> {
            bufferViewConfig?.orNull()?.requestRemoveBufferPermanently(info.bufferId)
            actionMode?.finish()
            true
          }
          else                   -> false
        }
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
    val view = inflater.inflate(R.layout.fragment_chat_list, container, false)
    ButterKnife.bind(this, view)

    val adapter = BufferViewConfigAdapter()
    viewModel.bufferViewConfigs.switchMap {
      combineLatest(it.map(BufferViewConfig::liveUpdates))
    }.toLiveData().observe(this, Observer {
      if (it != null) {
        adapter.submitList(it)
      }
    })

    chatListSpinner.adapter = adapter
    chatListSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(p0: AdapterView<*>?) {
        viewModel.bufferViewConfigId.onNext(-1)
      }

      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        viewModel.bufferViewConfigId.onNext(adapter.getItem(p2)?.bufferViewId() ?: -1)
      }
    }

    listAdapter = BufferListAdapter(
      viewModel.selectedBufferId,
      viewModel.collapsedNetworks
    )
    combineLatest(viewModel.bufferList, viewModel.collapsedNetworks, viewModel.selectedBuffer)
      .toLiveData().zip(database.filtered().listen(accountId))
      .observe(this, Observer { it ->
        it?.let { (data, activityList) ->
          runInBackground {
            val (info, collapsedNetworks, selected) = data
            val (config, list) = info ?: Pair(null, emptyList())
            val minimumActivity = config?.minimumActivity() ?: Buffer_Activity.NONE
            val activities = activityList.associate { it.bufferId to it.filtered }
            val processedList = list.asSequence().sortedBy { props ->
              !props.info.type.hasFlag(Buffer_Type.StatusBuffer)
            }.sortedBy { props ->
              props.network.networkName
            }.map { props ->
              val activity = props.activity - (activities[props.info.bufferId] ?: 0)
              BufferListItem(
                props.copy(
                  activity = activity,
                  description = ircFormatDeserializer.formatString(
                    props.description.toString(),
                    colorize = messageSettings.colorizeMirc
                  ),
                  bufferActivity = Buffer_Activity.of(
                    when {
                      props.highlights > 0                  -> Buffer_Activity.Highlight
                      activity.hasFlag(Message_Type.Plain) ||
                      activity.hasFlag(Message_Type.Notice) ||
                      activity.hasFlag(Message_Type.Action) -> Buffer_Activity.NewMessage
                      activity.isNotEmpty()                 -> Buffer_Activity.OtherActivity
                      else                                  -> Buffer_Activity.NoActivity
                    }
                  )
                ),
                BufferState(
                  networkExpanded = !collapsedNetworks.contains(props.network.networkId),
                  selected = selected.info?.bufferId == props.info.bufferId
                )
              )
            }.filter { (props, state) ->
              (props.info.type.hasFlag(BufferInfo.Type.StatusBuffer) || state.networkExpanded) &&
              (minimumActivity.toInt() <= props.bufferActivity.toInt() ||
               props.info.type.hasFlag(Buffer_Type.StatusBuffer))
            }.toList()

            activity?.runOnUiThread {
              listAdapter.submitList(processedList)
            }
          }
        }
      })
    listAdapter.setOnClickListener(this@BufferViewConfigFragment::clickListener)
    listAdapter.setOnLongClickListener(this@BufferViewConfigFragment::longClickListener)
    chatList.adapter = listAdapter

    viewModel.selectedBuffer.toLiveData().observe(this, Observer { buffer ->
      if (buffer != null) {
        val menu = actionMode?.menu
        if (menu != null) {
          val allActions = setOf(
            R.id.action_configure,
            R.id.action_connect,
            R.id.action_disconnect,
            R.id.action_join,
            R.id.action_part,
            R.id.action_delete,
            R.id.action_rename,
            R.id.action_unhide,
            R.id.action_hide_temp,
            R.id.action_hide_perm
          )

          val visibilityActions = when (buffer.hiddenState) {
            BufferHiddenState.VISIBLE          -> setOf(
              R.id.action_hide_temp,
              R.id.action_hide_perm
            )
            BufferHiddenState.HIDDEN_TEMPORARY -> setOf(
              R.id.action_unhide,
              R.id.action_hide_perm
            )
            BufferHiddenState.HIDDEN_PERMANENT -> setOf(
              R.id.action_unhide,
              R.id.action_hide_temp
            )
          }

          val availableActions = when (buffer.info?.type?.enabledValues()?.firstOrNull()) {
            Buffer_Type.StatusBuffer  -> {
              when (buffer.connectionState) {
                INetwork.ConnectionState.Disconnected -> setOf(
                  R.id.action_configure, R.id.action_connect
                )
                INetwork.ConnectionState.Initialized  -> setOf(
                  R.id.action_configure, R.id.action_disconnect
                )
                else                                  -> setOf(
                  R.id.action_configure, R.id.action_connect, R.id.action_disconnect
                )
              }
            }
            Buffer_Type.ChannelBuffer -> {
              if (buffer.joined) {
                setOf(R.id.action_part)
              } else {
                setOf(R.id.action_join, R.id.action_delete)
              } + visibilityActions
            }
            Buffer_Type.QueryBuffer   -> {
              setOf(R.id.action_delete, R.id.action_rename) + visibilityActions
            }
            else                      -> visibilityActions
          }

          val unavailableActions = allActions - availableActions

          for (action in availableActions) {
            menu.findItem(action)?.isVisible = true
          }
          for (action in unavailableActions) {
            menu.findItem(action)?.isVisible = false
          }
        }
      } else {
        actionMode?.finish()
      }
    })

    chatListToolbar.inflateMenu(R.menu.context_bufferlist)
    chatListToolbar.setOnMenuItemClickListener { item ->
      when (item.itemId) {
        R.id.action_show_hidden -> {
          item.isChecked = !item.isChecked
          viewModel.showHidden.onNext(item.isChecked)
          true
        }
        else                    -> false
      }
    }
    chatList.layoutManager = object : LinearLayoutManager(context) {
      override fun supportsPredictiveItemAnimations() = false
    }
    chatList.itemAnimator = DefaultItemAnimator()
    chatList.setItemViewCacheSize(10)

    savedInstanceState?.run {
      chatList.layoutManager.onRestoreInstanceState(getParcelable(KEY_STATE_LIST))
      chatListSpinner.onRestoreInstanceState(getParcelable(KEY_STATE_SPINNER))
    }

    return view
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(KEY_STATE_LIST, chatList.layoutManager.onSaveInstanceState())
    outState.putParcelable(KEY_STATE_SPINNER, chatListSpinner.onSaveInstanceState())
  }

  private fun clickListener(it: BufferId) {
    if (actionMode != null) {
      longClickListener(it)
    } else {
      viewModel.buffer.onNext(it)
    }
  }

  private fun longClickListener(it: BufferId) {
    if (actionMode == null) {
      chatListToolbar.startActionMode(actionModeCallback)
    }
    if (!listAdapter.toggleSelection(it)) {
      actionMode?.finish()
    }
  }

  companion object {
    private const val KEY_STATE_LIST = "KEY_STATE_LIST"
    private const val KEY_STATE_SPINNER = "KEY_STATE_SPINNER"
  }
}
