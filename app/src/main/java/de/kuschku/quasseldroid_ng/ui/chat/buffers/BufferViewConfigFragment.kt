package de.kuschku.quasseldroid_ng.ui.chat.buffers

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.libquassel.util.minus
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.ui.settings.data.AppearanceSettings
import de.kuschku.quasseldroid_ng.ui.settings.data.Settings
import de.kuschku.quasseldroid_ng.ui.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.map
import de.kuschku.quasseldroid_ng.util.helper.zip
import de.kuschku.quasseldroid_ng.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment

class BufferViewConfigFragment : ServiceBoundFragment() {
  private val handlerThread = AndroidHandlerThread("ChatList")

  @BindView(R.id.chatListToolbar)
  lateinit var chatListToolbar: Toolbar

  @BindView(R.id.chatListSpinner)
  lateinit var chatListSpinner: AppCompatSpinner

  @BindView(R.id.chatList)
  lateinit var chatList: RecyclerView

  private lateinit var viewModel: QuasselViewModel
  private lateinit var database: QuasselDatabase

  private var ircFormatDeserializer: IrcFormatDeserializer? = null
  private lateinit var appearanceSettings: AppearanceSettings

  private var actionMode: ActionMode? = null

  private val actionModeCallback = object : ActionMode.Callback {
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
      val selected = viewModel.selectedBuffer.value
      val info = selected?.info
      val session = viewModel.session.value
      val network = session?.networks?.get(selected?.info?.networkId)

      return if (info != null && session != null) {
        when (item?.itemId) {
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
                session.bufferSyncer?.requestRemoveBuffer(selected.info.bufferId)
              }
              .build()
              .show()
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
      return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
      actionMode = null
      listAdapter.unselectAll()
    }
  }

  private lateinit var listAdapter: BufferListAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    handlerThread.onCreate()
    super.onCreate(savedInstanceState)

    viewModel = ViewModelProviders.of(activity!!)[QuasselViewModel::class.java]
    database = QuasselDatabase.Creator.init(activity!!)
    appearanceSettings = Settings.appearance(activity!!)

    if (ircFormatDeserializer == null) {
      ircFormatDeserializer = IrcFormatDeserializer(context!!)
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_chat_list, container, false)
    ButterKnife.bind(this, view)

    val adapter = BufferViewConfigAdapter(this, viewModel.bufferViewConfigs)

    chatListSpinner.adapter = adapter
    chatListSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(p0: AdapterView<*>?) {
        viewModel.setBufferViewConfig(null)
      }

      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        viewModel.setBufferViewConfig(adapter.getItem(p2))
      }
    }

    listAdapter = BufferListAdapter(
      this,
      viewModel.bufferList.zip(database.filtered().listen(accountId)).map {
        val (data, activityList) = it
        val (config, list) = data ?: Pair(null, emptyList())
        val minimumActivity = config?.minimumActivity() ?: Buffer_Activity.NONE
        val activities = activityList.map { it.bufferId to it.filtered }.toMap()
        list.map {
          val activity = it.activity - (activities[it.info.bufferId] ?: 0)
          it.copy(
            description = ircFormatDeserializer?.formatString(
              it.description.toString(), appearanceSettings.colorizeMirc
            ) ?: it.description,
            activity = activity,
            bufferActivity = Buffer_Activity.of(
              when {
                it.highlights > 0                     -> Buffer_Activity.Highlight
                activity.hasFlag(Message_Type.Plain) ||
                activity.hasFlag(Message_Type.Notice) ||
                activity.hasFlag(Message_Type.Action) -> Buffer_Activity.NewMessage
                activity.isNotEmpty()                 -> Buffer_Activity.OtherActivity
                else                                  -> Buffer_Activity.NoActivity
              }
            )
          )
        }.filter { props ->
            minimumActivity.toInt() <= props.bufferActivity.toInt() ||
            props.info.type.hasFlag(Buffer_Type.StatusBuffer)
          }
      },
      viewModel.selectedBufferId,
      handlerThread::post,
      activity!!::runOnUiThread,
      clickListener,
      longClickListener
    )
    chatList.adapter = listAdapter

    viewModel.selectedBuffer.observe(this, Observer { buffer ->
      if (buffer != null) {
        val menu = actionMode?.menu
        if (menu != null) {
          val allActions = setOf(
            R.id.action_connect,
            R.id.action_disconnect,
            R.id.action_join,
            R.id.action_part,
            R.id.action_delete
          )

          val availableActions = when (buffer.info?.type?.enabledValues()?.firstOrNull()) {
            Buffer_Type.StatusBuffer  -> {
              when (buffer.connectionState) {
                INetwork.ConnectionState.Disconnected -> setOf(R.id.action_connect)
                INetwork.ConnectionState.Initialized  -> setOf(R.id.action_disconnect)
                else                                  -> setOf(
                  R.id.action_connect, R.id.action_disconnect
                )
              }
            }
            Buffer_Type.ChannelBuffer -> {
              if (buffer.joined) {
                setOf(R.id.action_part)
              } else {
                setOf(R.id.action_join, R.id.action_delete)
              }
            }
            Buffer_Type.QueryBuffer   -> {
              setOf(R.id.action_delete)
            }
            else                      -> emptySet()
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

    chatListToolbar.startActionMode(actionModeCallback)
    chatList.layoutManager = LinearLayoutManager(context)
    chatList.itemAnimator = DefaultItemAnimator()
    chatList.setItemViewCacheSize(10)
    return view
  }

  override fun onDestroy() {
    handlerThread.onDestroy()
    super.onDestroy()
  }

  private val clickListener: ((BufferId) -> Unit)? = {
    if (actionMode != null) {
      longClickListener?.invoke(it)
    } else {
      viewModel.setBuffer(it)
    }
  }

  private val longClickListener: ((BufferId) -> Unit)? = {
    if (actionMode == null) {
      chatListToolbar.startActionMode(actionModeCallback)
    }
    listAdapter.toggleSelection(it)
  }

  data class SelectedItem(
    val info: BufferInfo? = null,
    val connectionState: INetwork.ConnectionState = INetwork.ConnectionState.Disconnected,
    val joined: Boolean = false
  )
}
