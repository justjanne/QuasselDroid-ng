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
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.BufferViewManager
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.map
import de.kuschku.quasseldroid_ng.util.helper.or
import de.kuschku.quasseldroid_ng.util.helper.switchMap
import de.kuschku.quasseldroid_ng.util.helper.switchMapRx
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment

class BufferViewConfigFragment : ServiceBoundFragment() {
  private val handlerThread = AndroidHandlerThread("ChatList")

  @BindView(R.id.chatListToolbar)
  lateinit var chatListToolbar: Toolbar

  @BindView(R.id.chatListSpinner)
  lateinit var chatListSpinner: AppCompatSpinner

  @BindView(R.id.chatList)
  lateinit var chatList: RecyclerView

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
      }.sortedWith(Comparator { a, b ->
        (a?.bufferViewName() ?: "").compareTo((b?.bufferViewName() ?: ""), true)
      })
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

  private val bufferList = sessionManager.switchMap { manager ->
    bufferIdList.map { ids ->
      ids.mapNotNull {
        manager.bufferSyncer?.bufferInfo(it)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    handlerThread.onCreate()
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.content_chat_list, container, false)
    ButterKnife.bind(this, view)
    chatListSpinner.adapter = adapter
    chatListSpinner.onItemSelectedListener = itemSelectedListener

    chatList.adapter = BufferListAdapter(this, bufferList, handlerThread::post,
                                         activity::runOnUiThread)
    chatList.layoutManager = LinearLayoutManager(context)
    chatList.itemAnimator = DefaultItemAnimator()
    return view
  }

  override fun onDestroy() {
    handlerThread.onDestroy()
    super.onDestroy()
  }
}
