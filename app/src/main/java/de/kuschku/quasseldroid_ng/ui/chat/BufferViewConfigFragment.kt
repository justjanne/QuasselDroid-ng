package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.util.and
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.map
import de.kuschku.quasseldroid_ng.util.helper.or
import de.kuschku.quasseldroid_ng.util.helper.switchMap
import de.kuschku.quasseldroid_ng.util.helper.switchMapRx
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class BufferViewConfigFragment : ServiceBoundFragment() {
  private val handlerThread = AndroidHandlerThread("ChatList")

  @BindView(R.id.chatListToolbar)
  lateinit var chatListToolbar: Toolbar

  @BindView(R.id.chatListSpinner)
  lateinit var chatListSpinner: AppCompatSpinner

  @BindView(R.id.chatList)
  lateinit var chatList: RecyclerView

  val currentBuffer: MutableLiveData<LiveData<BufferId?>?> = MutableLiveData()

  private val sessionManager: LiveData<SessionManager?>
    = backend.map(Backend::sessionManager)
  private val bufferViewManager: LiveData<BufferViewManager?>
    = sessionManager.switchMapRx(SessionManager::session).map(ISession::bufferViewManager)
  private val networks: LiveData<Map<NetworkId, Network>?>
    = sessionManager.switchMapRx(SessionManager::session).map(ISession::networks)
  private val bufferViewConfigs = bufferViewManager.switchMapRx { manager ->
    manager.live_bufferViewConfigs.map { ids ->
      ids.mapNotNull { id ->
        manager.bufferViewConfig(id)
      }.sortedWith(
        Comparator { a, b ->
          (a?.bufferViewName() ?: "").compareTo((b?.bufferViewName() ?: ""), true)
        }
      )
    }
  }.or(emptyList())

  private val selectedBufferViewConfig = MutableLiveData<BufferViewConfig>()

  private val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(p0: AdapterView<*>?) {
      selectedBufferViewConfig.value = null
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
      selectedBufferViewConfig.value = adapter.getItem(p2)
    }
  }

  private val adapter = BufferViewConfigAdapter(this, bufferViewConfigs)

  private val bufferIdList = selectedBufferViewConfig.switchMapRx(BufferViewConfig::live_buffers)

  private val bufferList: LiveData<List<BufferListAdapter.BufferProps>?> = sessionManager.switchMap { manager ->
    selectedBufferViewConfig.switchMapRx { config ->
      config.live_config.debounce(16, TimeUnit.MILLISECONDS).switchMap { currentConfig ->
        config.live_buffers.switchMap { ids ->
          val bufferSyncer = manager.bufferSyncer
          if (bufferSyncer != null) {
            bufferSyncer.live_bufferInfos.switchMap {
              Observable.combineLatest(
                ids.mapNotNull { id ->
                  bufferSyncer.bufferInfo(id)
                }.filter {
                  currentConfig.networkId() <= 0 || currentConfig.networkId() == it.networkId
                }.filter {
                  (currentConfig.allowedBufferTypes() and it.type).isNotEmpty() ||
                  it.type.hasFlag(Buffer_Type.StatusBuffer)
                }.mapNotNull {
                  val network = manager.networks[it.networkId]
                  if (network == null) {
                    null
                  } else {
                    it to network
                  }
                }.map { (info, network) ->
                  bufferSyncer.liveActivity(info.bufferId).map { activity ->
                    when {
                      activity.hasFlag(Message.MessageType.Plain) ||
                      activity.hasFlag(Message.MessageType.Notice) ||
                      activity.hasFlag(Message.MessageType.Action) -> Buffer_Activity.NewMessage
                      activity.isNotEmpty()                        -> Buffer_Activity.OtherActivity
                      else                                         -> Buffer_Activity.NoActivity
                    }
                  }.switchMap { activity ->
                    when (info.type.toInt()) {
                      BufferInfo.Type.QueryBuffer.toInt()   -> {
                        network.liveIrcUser(info.bufferName).switchMap { user ->
                          user.live_away.switchMap { away ->
                            user.live_realName.map { realName ->
                              BufferListAdapter.BufferProps(
                                info = info,
                                network = network.networkInfo(),
                                bufferStatus = when {
                                  user == IrcUser.NULL -> BufferListAdapter.BufferStatus.OFFLINE
                                  away                 -> BufferListAdapter.BufferStatus.AWAY
                                  else                 -> BufferListAdapter.BufferStatus.ONLINE
                                },
                                description = realName,
                                activity = activity
                              )
                            }
                          }
                        }
                      }
                      BufferInfo.Type.ChannelBuffer.toInt() -> {
                        network.liveIrcChannel(
                          info.bufferName
                        ).switchMap { channel ->
                          channel.live_topic.map { topic ->
                            BufferListAdapter.BufferProps(
                              info = info,
                              network = network.networkInfo(),
                              bufferStatus = when (channel) {
                                IrcChannel.NULL -> BufferListAdapter.BufferStatus.OFFLINE
                                else            -> BufferListAdapter.BufferStatus.ONLINE
                              },
                              description = topic,
                              activity = activity
                            )
                          }
                        }
                      }
                      BufferInfo.Type.StatusBuffer.toInt()  -> {
                        network.liveConnectionState.map {
                          BufferListAdapter.BufferProps(
                            info = info,
                            network = network.networkInfo(),
                            bufferStatus = BufferListAdapter.BufferStatus.OFFLINE,
                            description = "",
                            activity = activity
                          )
                        }
                      }
                      else                                  -> Observable.just(
                        BufferListAdapter.BufferProps(
                          info = info,
                          network = network.networkInfo(),
                          bufferStatus = BufferListAdapter.BufferStatus.OFFLINE,
                          description = "",
                          activity = activity
                        )
                      )
                    }
                  }
                }, { array: Array<Any> ->
                  array.toList() as List<BufferListAdapter.BufferProps>
                }
              ).map { list ->
                list.filter {
                  config.minimumActivity().value <= it.activity.bit ||
                  it.info.type.hasFlag(Buffer_Type.StatusBuffer)
                }.filter {
                  (!config.hideInactiveBuffers()) ||
                  it.bufferStatus != BufferListAdapter.BufferStatus.OFFLINE ||
                  it.info.type.hasFlag(Buffer_Type.StatusBuffer)
                }
              }
            }
          } else {
            Observable.empty()
          }
        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    handlerThread.onCreate()
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_chat_list, container, false)
    ButterKnife.bind(this, view)
    chatListSpinner.adapter = adapter
    chatListSpinner.onItemSelectedListener = itemSelectedListener

    chatList.adapter = BufferListAdapter(
      this,
      bufferList,
      handlerThread::post,
      activity!!::runOnUiThread,
      clickListener
    )
    chatList.layoutManager = LinearLayoutManager(context)
    chatList.itemAnimator = DefaultItemAnimator()
    return view
  }

  override fun onDestroy() {
    handlerThread.onDestroy()
    super.onDestroy()
  }

  val clickListeners = mutableListOf<(BufferId) -> Unit>()

  private val clickListener: ((BufferId) -> Unit)? = {
    for (clickListener in clickListeners) {
      clickListener.invoke(it)
    }
  }
}
