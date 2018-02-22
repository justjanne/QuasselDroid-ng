package de.kuschku.quasseldroid_ng.ui.chat.buffers

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.libquassel.util.minus
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.ui.settings.Settings
import de.kuschku.quasseldroid_ng.ui.settings.data.AppearanceSettings
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

    chatList.adapter = BufferListAdapter(
      this,
      viewModel.bufferList.zip(database.filtered().listen(accountId)).map {
        val (list, activityList) = it
        val activities = activityList.map { it.bufferId to it.filtered }.toMap()
        list
          ?.map {
            val activity = it.activity - (activities[it.info.bufferId] ?: 0)
            it.bufferActivity to it.copy(
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
          }?.filter { (minimumActivity, props) ->
            minimumActivity.toInt() <= props.bufferActivity.toInt() ||
            props.info.type.hasFlag(Buffer_Type.StatusBuffer)
          }?.map { (_, props) ->
            props
          }
      },
      handlerThread::post,
      activity!!::runOnUiThread,
      clickListener
    )
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
    viewModel.setBuffer(it)
  }
}
