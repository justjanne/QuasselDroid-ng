package de.kuschku.quasseldroid.ui.chat.messages

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
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.BacklogSettings
import de.kuschku.quasseldroid.util.helper.invoke
import de.kuschku.quasseldroid.util.helper.switchMapNotNull
import de.kuschku.quasseldroid.util.helper.toggle
import de.kuschku.quasseldroid.util.helper.zip
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import javax.inject.Inject

class MessageListFragment : ServiceBoundFragment() {
  @BindView(R.id.messages)
  lateinit var messageList: RecyclerView

  @BindView(R.id.scrollDown)
  lateinit var scrollDown: FloatingActionButton

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var backlogSettings: BacklogSettings

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var messageRenderer: QuasselMessageRenderer

  private lateinit var viewModel: QuasselViewModel

  private lateinit var linearLayoutManager: LinearLayoutManager
  private lateinit var adapter: MessageAdapter

  private var lastBuffer: BufferId? = null
  private var previousMessageId: MsgId? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel = ViewModelProviders.of(activity!!)[QuasselViewModel::class.java]
  }

  private val boundaryCallback = object :
    PagedList.BoundaryCallback<QuasselDatabase.DatabaseMessage>() {
    override fun onItemAtFrontLoaded(itemAtFront: QuasselDatabase.DatabaseMessage) = Unit
    override fun onItemAtEndLoaded(itemAtEnd: QuasselDatabase.DatabaseMessage) = loadMore()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_messages, container, false)
    ButterKnife.bind(this, view)

    linearLayoutManager = LinearLayoutManager(context)
    linearLayoutManager.reverseLayout = true

    adapter = MessageAdapter(messageRenderer)
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
      })

    val data = viewModel.buffer_liveData.switchMapNotNull { buffer ->
      database.filtered().listen(accountId, buffer).switchMapNotNull { filtered ->
        LivePagedListBuilder(
          database.message().findByBufferIdPaged(buffer, filtered),
          PagedList.Config.Builder()
            .setPageSize(backlogSettings.dynamicAmount)
            .setPrefetchDistance(backlogSettings.dynamicAmount)
            .setInitialLoadSizeHint(backlogSettings.dynamicAmount)
            .setEnablePlaceholders(true)
            .build()
        ).setBoundaryCallback(boundaryCallback).build()
      }
    }

    val lastMessageId = viewModel.buffer_liveData.switchMapNotNull {
      database.message().lastMsgId(it)
    }

    viewModel.sessionManager_liveData.zip(lastMessageId).observe(
      this, Observer {
      runInBackground {
        val session = it?.first?.orNull()
        val message = it?.second
        val bufferSyncer = session?.bufferSyncer
        if (message != null && bufferSyncer != null && previousMessageId != message.messageId) {
          markAsRead(bufferSyncer, message.bufferId, message.messageId)
          previousMessageId = message.messageId
        }
      }
    })

    viewModel.markerLine_liveData.observe(this, Observer {
      it?.ifPresent {
        adapter.markerLinePosition = it
        adapter.notifyDataSetChanged()
      }
    })

    var lastBuffer = -1
    data.observe(this, Observer { list ->
      val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
      val firstVisibleMessageId = adapter[firstVisibleItemPosition]?.messageId
      runInBackground {
        adapter.submitList(list)

        if (firstVisibleItemPosition < 2) {
          activity?.runOnUiThread { messageList.scrollToPosition(0) }
          runInBackgroundDelayed(16) {
            activity?.runOnUiThread {
              messageList.scrollToPosition(0)
            }
          }
        }

        val buffer = viewModel.buffer.value ?: -1
        if (buffer != lastBuffer) {
          backend.value.orNull()?.sessionManager()?.bufferSyncer?.let { bufferSyncer ->
            onBufferChange(lastBuffer, buffer, firstVisibleMessageId, bufferSyncer)
          }
          lastBuffer = buffer
          adapter.clearCache()
        }
      }
    })
    scrollDown.hide()
    scrollDown.setOnClickListener { messageList.scrollToPosition(0) }
    return view
  }

  private fun markAsRead(bufferSyncer: BufferSyncer, buffer: BufferId, lastMessageId: MsgId?) {
    bufferSyncer.requestMarkBufferAsRead(buffer)
    if (lastMessageId != null)
      bufferSyncer.requestSetLastSeenMsg(buffer, lastMessageId)
  }

  private fun onBufferChange(
    previous: BufferId?, current: BufferId, lastMessageId: MsgId?,
    bufferSyncer: BufferSyncer
  ) {
    if (previous != null && lastMessageId != null) {
      bufferSyncer.requestSetMarkerLine(previous, lastMessageId)
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
    val bufferSyncer = viewModel.session.value?.orNull()?.bufferSyncer
    if (previous != null && messageId != null) {
      bufferSyncer?.requestSetMarkerLine(previous, messageId)
    }
    super.onPause()
  }

  private fun loadMore() {
    runInBackground {
      viewModel.buffer { bufferId ->
        viewModel.session {
          it.orNull()?.backlogManager?.requestBacklog(
            bufferId = bufferId,
            last = database.message().findFirstByBufferId(
              bufferId
            )?.messageId ?: -1,
            limit = backlogSettings.dynamicAmount
          )
        }
      }
    }
  }
}