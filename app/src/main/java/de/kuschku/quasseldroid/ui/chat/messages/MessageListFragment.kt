package de.kuschku.quasseldroid.ui.chat.messages

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
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
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.SpanFormatter
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

  private var actionMode: ActionMode? = null

  private val actionModeCallback = object : ActionMode.Callback {
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = when (item?.itemId) {
      R.id.action_copy  -> {
        val data = viewModel.selectedMessages.value.values.sortedBy {
          it.id
        }.joinToString("\n") {
          SpanFormatter.format(
            getString(R.string.message_format_copy),
            it.time,
            it.content
          )
        }

        val clipboard = requireActivity().systemService<ClipboardManager>()
        val clip = ClipData.newPlainText(null, data)
        clipboard.primaryClip = clip
        actionMode?.finish()
        true
      }
      R.id.action_share -> {
        val data = viewModel.selectedMessages.value.values.sortedBy {
          it.id
        }.joinToString("\n") {
          SpanFormatter.format(
            getString(R.string.message_format_copy),
            it.time,
            it.content
          )
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, data)
        requireContext().startActivity(
          Intent.createChooser(
            intent,
            requireContext().getString(R.string.label_share)
          )
        )
        actionMode?.finish()
        true
      }
      else              -> false
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
      actionMode = mode
      mode?.menuInflater?.inflate(R.menu.context_messages, menu)
      return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
      return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
      actionMode = null
      viewModel.selectedMessages.onNext(emptyMap())
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel = ViewModelProviders.of(activity!!)[QuasselViewModel::class.java]
  }

  private val boundaryCallback = object :
    PagedList.BoundaryCallback<DisplayMessage>() {
    override fun onItemAtFrontLoaded(itemAtFront: DisplayMessage) = Unit
    override fun onItemAtEndLoaded(itemAtEnd: DisplayMessage) =
      loadMore(lastMessageId = itemAtEnd.content.messageId)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_messages, container, false)
    ButterKnife.bind(this, view)

    linearLayoutManager = LinearLayoutManager(context)
    linearLayoutManager.reverseLayout = true

    adapter = MessageAdapter(
      messageRenderer,
      { msg ->
        if (actionMode != null) {
          if (!viewModel.selectedMessagesToggle(msg.id, msg)) {
            actionMode?.finish()
          }
        }
      },
      { msg ->
        if (actionMode == null) {
          activity?.startActionMode(actionModeCallback)
        }
        if (!viewModel.selectedMessagesToggle(msg.id, msg)) {
          actionMode?.finish()
        }
      }
    )
    messageList.adapter = adapter
    messageList.layoutManager = linearLayoutManager
    messageList.itemAnimator = null
    messageList.setItemViewCacheSize(20)

    var isScrolling = false
    messageList.addOnScrollListener(
      object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          val canScrollDown = recyclerView.canScrollVertically(1)
          val isScrollingDown = dy > 0

          scrollDown.visibility = View.VISIBLE
          scrollDown.toggle(canScrollDown && isScrollingDown)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
          isScrolling = when (newState) {
            RecyclerView.SCROLL_STATE_SETTLING, RecyclerView.SCROLL_STATE_IDLE -> false
            RecyclerView.SCROLL_STATE_DRAGGING                                 -> true
            else                                                               -> isScrolling
          }
        }
      })

    val data = combineLatest(viewModel.buffer,
                             viewModel.selectedMessages,
                             viewModel.expandedMessages,
                             viewModel.markerLine)
      .toLiveData().switchMapNotNull { (buffer, selected, expanded, markerLine) ->
        database.filtered().listen(accountId, buffer).switchMapNotNull { filtered ->
          LivePagedListBuilder(
            database.message().findByBufferIdPaged(buffer, filtered).map {
              DisplayMessage(
                content = it,
                isSelected = selected.contains(it.messageId),
                isExpanded = expanded.contains(it.messageId),
                isMarkerLine = markerLine.orNull() == it.messageId
              )
            },
            PagedList.Config.Builder()
              .setPageSize(backlogSettings.pageSize)
              .setPrefetchDistance(backlogSettings.pageSize)
              .setInitialLoadSizeHint(backlogSettings.pageSize)
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

    fun checkScroll() {
      if (linearLayoutManager.findFirstVisibleItemPosition() < 2 && !isScrolling) {
        messageList.scrollToPosition(0)
      }
    }

    adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onChanged() = checkScroll()
      override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = checkScroll()
    })

    var lastBuffer = -1
    data.observe(this, Observer { list ->
      val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
      val firstVisibleMessageId = adapter[firstVisibleItemPosition]?.content?.messageId
      runInBackground {
        activity?.runOnUiThread {
          list?.let(adapter::submitList)
        }

        val buffer = viewModel.buffer.value ?: -1
        if (buffer != lastBuffer) {
          adapter.clearCache()
          viewModel.session.value?.orNull()?.bufferSyncer?.let { bufferSyncer ->
            onBufferChange(lastBuffer, buffer, firstVisibleMessageId, bufferSyncer)
          }
          lastBuffer = buffer
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
      loadMore(initial = true)
    }
    activity?.runOnUiThread { messageList.scrollToPosition(0) }
  }

  override fun onPause() {
    val previous = lastBuffer
    val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
    val messageId = adapter[firstVisibleItemPosition]?.content?.messageId
    val bufferSyncer = viewModel.session.value?.orNull()?.bufferSyncer
    if (previous != null && messageId != null) {
      bufferSyncer?.requestSetMarkerLine(previous, messageId)
    }
    super.onPause()
  }

  private fun loadMore(initial: Boolean = false, lastMessageId: MsgId? = null) {
    runInBackground {
      viewModel.buffer { bufferId ->
        viewModel.session {
          it.orNull()?.backlogManager?.requestBacklog(
            bufferId = bufferId,
            last = lastMessageId ?: database.message().findFirstByBufferId(
              bufferId
            )?.messageId ?: -1,
            limit = if (initial) backlogSettings.initialAmount else backlogSettings.pageSize
          )
        }
      }
    }
  }

}