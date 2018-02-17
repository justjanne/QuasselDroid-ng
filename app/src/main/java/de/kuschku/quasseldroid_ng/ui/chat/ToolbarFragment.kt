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
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.*
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment

class ToolbarFragment : ServiceBoundFragment() {
  @BindView(R.id.toolbar_title)
  lateinit var toolbarTitle: TextView

  @BindView(R.id.toolbar_subtitle)
  lateinit var toolbarSubtitle: TextView

  val currentBuffer: MutableLiveData<LiveData<BufferId?>?> = MutableLiveData()
  val buffer = currentBuffer.switchMap { it }

  private val sessionManager: LiveData<SessionManager?>
    = backend.map(Backend::sessionManager)

  private val currentBufferInfo: LiveData<BufferInfo?>
    = sessionManager.switchMapRx(SessionManager::session).switchMap { session ->
    buffer.switchMapRx {
      session.bufferSyncer?.liveBufferInfo(it)
    }
  }

  private val isSecure: LiveData<Boolean?> = sessionManager.switchMapRx(
    SessionManager::session
  ).switchMapRx { session ->
    session.state.map { state ->
      session.sslSession != null
    }
  }

  var title: CharSequence
    get() = toolbarTitle.text
    set(value) {
      toolbarTitle.text = value
    }

  var subtitle: CharSequence
    get() = toolbarTitle.text
    set(value) {
      toolbarSubtitle.text = value
      toolbarSubtitle.visibleIf(value.isNotEmpty())
    }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_toolbar, container, false)
    ButterKnife.bind(this, view)

    currentBufferInfo.zip(isSecure).observe(
      this, Observer {
      if (it != null) {
        val (info, isSecure) = it
        this.title = info?.bufferName ?: resources.getString(
          R.string.app_name
        )
      }
    }
    )

    return view
  }
}