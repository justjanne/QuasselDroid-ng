package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.map
import de.kuschku.quasseldroid_ng.util.helper.switchMap
import de.kuschku.quasseldroid_ng.util.helper.switchMapRx
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment
import io.reactivex.Observable
import io.reactivex.Observable.zip
import io.reactivex.functions.BiFunction

class NickListFragment : ServiceBoundFragment() {
  private val handlerThread = AndroidHandlerThread("NickList")

  @BindView(R.id.nickList)
  lateinit var nickList: RecyclerView

  val currentBuffer: MutableLiveData<LiveData<BufferId?>?> = MutableLiveData()
  val buffer = currentBuffer.switchMap { it }

  private val sessionManager: LiveData<SessionManager?>
    = backend.map(Backend::sessionManager)

  private val ircChannel: LiveData<List<NickListAdapter.IrcUserItem>?>
    = sessionManager.switchMapRx(SessionManager::session).switchMap { session ->
    buffer.switchMapRx {
      val bufferSyncer = session.bufferSyncer
      val bufferInfo = bufferSyncer?.bufferInfo(it)
      if (bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
        val network = session.networks[bufferInfo.networkId]
        val ircChannel = network?.ircChannel(bufferInfo.bufferName)
        if (ircChannel != null) {
          Observable.combineLatest(
            ircChannel.ircUsers().map { user ->
              zip(
                user.live_realName, user.live_away,
                BiFunction<String, Boolean, Pair<String, Boolean>> { a, b -> Pair(a, b) }
              ).map { (realName, away) ->
                val userModes = ircChannel.userModes(user)
                val prefixModes = network.prefixModes()

                val lowestMode = userModes.mapNotNull {
                  prefixModes.indexOf(it)
                }.min() ?: prefixModes.size

                NickListAdapter.IrcUserItem(
                  user.nick(),
                  network.modesToPrefixes(userModes),
                  lowestMode,
                  realName,
                  away
                )
              }
            }, { array: Array<Any> ->
              array.toList() as List<NickListAdapter.IrcUserItem>
            }
          )
        } else {
          Observable.just(emptyList())
        }
      } else {
        Observable.just(emptyList())
      }
    }
  }

  private val nicks: LiveData<List<NickListAdapter.IrcUserItem>?> = ircChannel

  override fun onCreate(savedInstanceState: Bundle?) {
    handlerThread.onCreate()
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_nick_list, container, false)
    ButterKnife.bind(this, view)

    nickList.adapter = NickListAdapter(
      this,
      nicks,
      handlerThread::post,
      activity!!::runOnUiThread,
      clickListener
    )

    nickList.layoutManager = LinearLayoutManager(context)
    nickList.itemAnimator = DefaultItemAnimator()

    return view
  }

  override fun onDestroy() {
    handlerThread.onDestroy()
    super.onDestroy()
  }

  val clickListeners = mutableListOf<(String) -> Unit>()

  private val clickListener: ((String) -> Unit)? = {
    for (clickListener in clickListeners) {
      clickListener.invoke(it)
    }
  }
}