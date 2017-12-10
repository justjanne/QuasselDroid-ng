package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.invoke
import de.kuschku.quasseldroid_ng.util.helper.map
import de.kuschku.quasseldroid_ng.util.helper.switchMap
import de.kuschku.quasseldroid_ng.util.helper.toggle
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment

class MessageListFragment : ServiceBoundFragment() {
  val currentBuffer: MutableLiveData<LiveData<BufferId?>?> = MutableLiveData()
  private val buffer = currentBuffer.switchMap { it }

  private val handler = AndroidHandlerThread("Chat")

  private lateinit var database: QuasselDatabase

  @BindView(R.id.messages)
  lateinit var messageList: RecyclerView

  @BindView(R.id.scrollDown)
  lateinit var scrollDown: FloatingActionButton

  private val sessionManager: LiveData<SessionManager?> = backend.map(Backend::sessionManager)

  override fun onCreate(savedInstanceState: Bundle?) {
    handler.onCreate()
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_messages, container, false)
    ButterKnife.bind(this, view)

    database = QuasselDatabase.Creator.init(context!!.applicationContext)
    val data = buffer.switchMap {
      database.message().findByBufferIdPaged(it).create(Int.MAX_VALUE,
        PagedList.Config.Builder()
          .setPageSize(50)
          .setEnablePlaceholders(false)
          .setPrefetchDistance(50)
          .build()
      )
    }

    val adapter = MessageAdapter(context!!)

    data.observe(this, Observer { list ->
      adapter.setList(list)
    })

    buffer.observe(this, Observer {
      handler.post {
        // Try loading messages when switching to empty buffer
        if (it != null && database.message().bufferSize(it) == 0) {
          loadMore()
        }
      }
    })

    var recyclerViewMeasuredHeight = 0
    val scrollDownListener = object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!recyclerView.canScrollVertically(-1)) {
          loadMore()
        }
        if (recyclerViewMeasuredHeight == 0)
          recyclerViewMeasuredHeight = recyclerView.measuredHeight
        val canScrollDown = recyclerView.canScrollVertically(1)
        val isScrollingDown = dy > 0
        val scrollOffsetFromBottom = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollOffset() - recyclerViewMeasuredHeight
        val isMoreThanOneScreenFromBottom = scrollOffsetFromBottom > recyclerViewMeasuredHeight
        val smartVisibility = scrollDown.visibility == View.VISIBLE || isMoreThanOneScreenFromBottom
        scrollDown.toggle(canScrollDown && isScrollingDown && smartVisibility)
      }
    }

    messageList.adapter = adapter
    messageList.layoutManager = LinearLayoutManager(context)
    messageList.addOnScrollListener(scrollDownListener)

    scrollDown.setOnClickListener { messageList.scrollToPosition(adapter.itemCount) }

    return view
  }

  private fun loadMore() {
    handler.post {
      buffer { bufferId ->
        backend()?.sessionManager()?.backlogManager?.requestBacklog(
          bufferId = bufferId,
          last = database.message().findFirstByBufferId(bufferId)?.messageId ?: -1,
          limit = 20
        )
      }
    }
  }

  override fun onDestroy() {
    handler.onDestroy()
    super.onDestroy()
  }
}
