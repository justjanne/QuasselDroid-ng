package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.ui.settings.data.DisplaySettings
import de.kuschku.quasseldroid_ng.util.helper.*
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment
import io.reactivex.Observable

class ToolbarFragment : ServiceBoundFragment() {
  @BindView(R.id.toolbar_title)
  lateinit var toolbarTitle: TextView

  @BindView(R.id.toolbar_subtitle)
  lateinit var toolbarSubtitle: TextView

  val currentBuffer: MutableLiveData<LiveData<BufferId?>?> = MutableLiveData()
  val buffer = currentBuffer.switchMap { it }

  private val sessionManager: LiveData<SessionManager?>
    = backend.map(Backend::sessionManager)

  private val lag: LiveData<Long?>
    = sessionManager.switchMapRx { it.session.switchMap { it.lag } }

  private val displaySettings = DisplaySettings(
    showLag = true
  )

  private val bufferData: LiveData<BufferData?> = sessionManager.switchMap { manager ->
    buffer.switchMapRx { id ->
      manager.session.switchMap {
        val bufferSyncer = it.bufferSyncer
        if (bufferSyncer != null) {
          bufferSyncer.live_bufferInfos.switchMap {
            val info = bufferSyncer.bufferInfo(id)
            val network = manager.networks[info?.networkId]
            if (info == null) {
              Observable.just(
                BufferData(
                  description = "Info was null"
                )
              )
            } else if (network == null) {
              Observable.just(
                BufferData(
                  description = "Network was null"
                )
              )
            } else {
              when (info.type.toInt()) {
                BufferInfo.Type.QueryBuffer.toInt()   -> {
                  network.liveIrcUser(info.bufferName).switchMap { user ->
                    user.live_realName.map { realName ->
                      BufferData(
                        info = info,
                        network = network.networkInfo(),
                        description = realName
                      )
                    }
                  }
                }
                BufferInfo.Type.ChannelBuffer.toInt() -> {
                  network.liveIrcChannel(
                    info.bufferName
                  ).switchMap { channel ->
                    channel.live_topic.map { topic ->
                      BufferData(
                        info = info,
                        network = network.networkInfo(),
                        description = topic
                      )
                    }
                  }
                }
                BufferInfo.Type.StatusBuffer.toInt()  -> {
                  network.liveConnectionState.map {
                    BufferData(
                      info = info,
                      network = network.networkInfo()
                    )
                  }
                }
                else                                  -> Observable.just(
                  BufferData(
                    description = "type is unknown: ${info.type.toInt()}"
                  )
                )
              }
            }
          }
        } else {
          Observable.just(
            BufferData(
              description = "buffersyncer was null"
            )
          )
        }
      }
    }
  }

  private val isSecure: LiveData<Boolean?> = sessionManager.switchMapRx(
    SessionManager::session
  ).switchMapRx { session ->
    session.state.map { state ->
      session.sslSession != null
    }
  }

  var title: CharSequence?
    get() = toolbarTitle.text
    set(value) {
      if (value != null)
        toolbarTitle.text = value
      else
        toolbarTitle.setText(R.string.app_name)
    }

  var subtitle: CharSequence?
    get() = toolbarTitle.text
    set(value) {
      toolbarSubtitle.text = value ?: ""
      toolbarSubtitle.visibleIf(value?.isNotEmpty() == true)
    }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_toolbar, container, false)
    ButterKnife.bind(this, view)

    bufferData.zip(isSecure, lag).observe(
      this, Observer {
      if (it != null) {
        val (data, isSecure, lag) = it
        if (data?.info?.type?.hasFlag(Buffer_Type.StatusBuffer) == true) {
          this.title = data.network?.networkName
        } else {
          this.title = data?.info?.bufferName
        }

        if (lag == 0L || !displaySettings.showLag) {
          this.subtitle = data?.description
        } else {
          val description = data?.description
          if (description.isNullOrBlank()) {
            this.subtitle = "Lag: ${lag}ms"
          } else {
            this.subtitle = "Lag: ${lag}ms ${description}"
          }
        }
      }
    }
    )

    return view
  }

  data class BufferData(
    val info: BufferInfo? = null,
    val network: INetwork.NetworkInfo? = null,
    val description: String? = null
  )

}