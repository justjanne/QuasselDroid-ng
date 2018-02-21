package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.LivePagedListBuilder
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
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.quassel.syncables.BufferSyncer
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.ui.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.*
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment

class MessageListFragment : ServiceBoundFragment() {
  @BindView(R.id.messages)
  lateinit var messageList: RecyclerView

  @BindView(R.id.scrollDown)
  lateinit var scrollDown: FloatingActionButton

  private lateinit var viewModel: QuasselViewModel

  private val handler = AndroidHandlerThread("Chat")

  private lateinit var database: QuasselDatabase

  private lateinit var linearLayoutManager: LinearLayoutManager
  private lateinit var adapter: MessageAdapter

  private var lastBuffer: BufferId? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    handler.onCreate()
    super.onCreate(savedInstanceState)
    viewModel = ViewModelProviders.of(activity!!)[QuasselViewModel::class.java]
    setHasOptionsMenu(true)
  }

  private val boundaryCallback = object :
    PagedList.BoundaryCallback<QuasselDatabase.DatabaseMessage>() {
    override fun onItemAtFrontLoaded(itemAtFront: QuasselDatabase.DatabaseMessage) = Unit
    override fun onItemAtEndLoaded(itemAtEnd: QuasselDatabase.DatabaseMessage) = loadMore()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_messages, container, false)
    ButterKnife.bind(this, view)

    linearLayoutManager = LinearLayoutManager(context)
    linearLayoutManager.reverseLayout = true

    adapter = MessageAdapter(context!!)
    messageList.adapter = adapter
    messageList.layoutManager = linearLayoutManager
    messageList.itemAnimator = null
    messageList.setItemViewCacheSize(20)
    messageList.addOnScrollListener(
      object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          val canScrollDown = recyclerView.canScrollVertically(1)
          val isScrollingDown = dy > 0

          scrollDown.visibility = View.VISIBLE
          scrollDown.toggle(canScrollDown && isScrollingDown)
        }
      }
    )

    database = QuasselDatabase.Creator.init(context!!.applicationContext)
    val data = viewModel.getBuffer().switchMapNotNull {
      LivePagedListBuilder(
        database.message().findByBufferIdPaged(it),
        PagedList.Config.Builder()
          .setPageSize(20)
          .setPrefetchDistance(20)
          .setInitialLoadSizeHint(20)
          .setEnablePlaceholders(true)
          .build()
      ).setBoundaryCallback(boundaryCallback).build()
    }

    handler.post {
      viewModel.sessionManager.zip(viewModel.getBuffer(), data).observe(
        this, Observer {
        val previous = lastBuffer ?: -1
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val messageId = adapter[firstVisibleItemPosition]?.messageId
        handler.post {
          val session = it?.first
          val buffer = it?.second
          val bufferSyncer = session?.bufferSyncer
          if (buffer != null && bufferSyncer != null && buffer != previous) {
            onBufferChange(previous, buffer, messageId, bufferSyncer)
            lastBuffer = buffer
          }
        }
      }
      )
    }

    viewModel.markerLine.observe(
      this, Observer {
      adapter.markerLinePosition = it
      adapter.notifyDataSetChanged()
    }
    )

    var lastBuffer = -1
    data.observe(
      this, Observer { list ->
      val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
      val buffer = viewModel.getBuffer().value ?: -1
      if (buffer != lastBuffer) {
        lastBuffer = buffer
        adapter.clearCache()
      }
      adapter.setList(list)
      if (firstVisibleItemPosition < 2) {
        activity?.runOnUiThread { messageList.scrollToPosition(0) }
        handler.postDelayed({ activity?.runOnUiThread { messageList.scrollToPosition(0) } }, 16)
      }
    }
    )
    scrollDown.hide()
    scrollDown.setOnClickListener { messageList.scrollToPosition(0) }
    return view
  }

  private fun onBufferChange(previous: BufferId?, current: BufferId, lastMessageId: MsgId?,
                             bufferSyncer: BufferSyncer) {
    if (lastMessageId != null) {
      bufferSyncer.requestMarkBufferAsRead(current)
      bufferSyncer.requestSetLastSeenMsg(current, lastMessageId)
      if (previous != null) {
        bufferSyncer.requestSetMarkerLine(previous, lastMessageId)
      }
    }
    // Try loading messages when switching to isEmpty buffer
    if (database.message().bufferSize(current) == 0) {
      loadMore()
    }
    activity?.runOnUiThread { messageList.scrollToPosition(0) }
  }

  override fun onPause() {
    val previous = lastBuffer
    val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
    val messageId = adapter[firstVisibleItemPosition]?.messageId
    val bufferSyncer = viewModel.sessionManager.value?.bufferSyncer
    if (previous != null && messageId != null) {
      bufferSyncer?.requestSetMarkerLine(previous, messageId)
    }
    super.onPause()
  }

  private fun loadMore() {
    handler.post {
      viewModel.getBuffer().let { bufferId ->
        viewModel.sessionManager()?.backlogManager?.requestBacklog(
          bufferId = bufferId,
          last = database.message().findFirstByBufferId(
            bufferId
          )?.messageId ?: -1,
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