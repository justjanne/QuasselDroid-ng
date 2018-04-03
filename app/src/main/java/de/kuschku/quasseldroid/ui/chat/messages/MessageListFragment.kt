package de.kuschku.quasseldroid.ui.chat.messages

import android.arch.lifecycle.Observer
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.view.*
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.FixedPreloadSizeProvider
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.quassel.syncables.BufferSyncer
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.BacklogSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import io.reactivex.BackpressureStrategy
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MessageListFragment : ServiceBoundFragment() {
  @BindView(R.id.messages)
  lateinit var messageList: RecyclerView

  @BindView(R.id.scrollDown)
  lateinit var scrollDown: FloatingActionButton

  @BindView(R.id.swipeRefreshLayout)
  lateinit var swipeRefreshLayout: SwipeRefreshLayout

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var backlogSettings: BacklogSettings

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var messageRenderer: QuasselMessageRenderer

  private lateinit var linearLayoutManager: LinearLayoutManager
  private lateinit var adapter: MessageAdapter

  private var lastBuffer: BufferId? = null
  private var previousMessageId: MsgId? = null

  private var actionMode: ActionMode? = null

  private val actionModeCallback = object : ActionMode.Callback {
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = when (item?.itemId) {
      R.id.action_copy  -> {
        val builder = SpannableStringBuilder()
        viewModel.selectedMessages.value.values.sortedBy {
          it.id
        }.map {
          if (it.name != null && it.content != null) {
            SpanFormatter.format(getString(R.string.message_format_copy_complex),
                                 it.time,
                                 it.name,
                                 it.content)
          } else {
            SpanFormatter.format(getString(R.string.message_format_copy), it.time, it.combined)
          }
        }.forEach {
          builder.append(it)
          builder.append("\n")
        }

        val data = if (builder.endsWith('\n'))
          builder.subSequence(0, builder.length - 1)
        else
          builder

        val clipboard = requireContext().systemService<ClipboardManager>()
        val clip = ClipData.newPlainText(null, data)
        clipboard.primaryClip = clip
        actionMode?.finish()
        true
      }
      R.id.action_share -> {
        val builder = SpannableStringBuilder()
        viewModel.selectedMessages.value.values.sortedBy {
          it.id
        }.map {
          if (it.name != null && it.content != null) {
            SpanFormatter.format(getString(R.string.message_format_copy_complex),
                                 it.time,
                                 it.name,
                                 it.content)
          } else {
            SpanFormatter.format(getString(R.string.message_format_copy), it.time, it.combined)
          }
        }.forEach {
          builder.append(it)
          builder.append("\n")
        }

        val data = if (builder.endsWith('\n'))
          builder.subSequence(0, builder.length - 1)
        else
          builder

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
      mode?.tag = "MESSAGES"
      return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
      actionMode = null
      viewModel.selectedMessages.onNext(emptyMap())
    }
  }

  private val boundaryCallback = object :
    PagedList.BoundaryCallback<DisplayMessage>() {
    override fun onItemAtFrontLoaded(itemAtFront: DisplayMessage) = Unit
    override fun onItemAtEndLoaded(itemAtEnd: DisplayMessage) = loadMore()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_messages, container, false)
    ButterKnife.bind(this, view)

    linearLayoutManager = LinearLayoutManager(context)
    linearLayoutManager.reverseLayout = true

    var linkMenu: PopupMenu? = null
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
      },
      null,
      { textView, url ->
        if (linkMenu == null) {
          linkMenu = PopupMenu(requireContext(), textView).also { menu ->
            linkMenu?.dismiss()
            menu.menuInflater.inflate(R.menu.context_link, menu.menu)
            menu.setOnMenuItemClickListener {
              when (it.itemId) {
                R.id.action_copy  -> {
                  val clipboard = requireContext().systemService<ClipboardManager>()
                  val clip = ClipData.newPlainText(null, url)
                  clipboard.primaryClip = clip
                  menu.dismiss()
                  linkMenu = null
                  true
                }
                R.id.action_share -> {
                  val intent = Intent(Intent.ACTION_SEND)
                  intent.type = "text/plain"
                  intent.putExtra(Intent.EXTRA_TEXT, url)
                  requireContext().startActivity(
                    Intent.createChooser(intent, requireContext().getString(R.string.label_share))
                  )
                  menu.dismiss()
                  linkMenu = null
                  true
                }
                else              -> false
              }
            }
            menu.setOnDismissListener {
              linkMenu = null
            }
            menu.show()
          }
        }
        true
      }
    )
    messageList.adapter = adapter
    messageList.layoutManager = linearLayoutManager
    messageList.itemAnimator = null
    messageList.setItemViewCacheSize(20)

    val senderColors = requireContext().theme.styledAttributes(
      R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
      R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
      R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
      R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
    ) {
      IntArray(16) {
        getColor(it, 0)
      }
    }

    swipeRefreshLayout.setColorSchemeColors(*senderColors)
    swipeRefreshLayout.setOnRefreshListener {
      loadMore()
    }

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

    fun processMessages(list: List<QuasselDatabase.DatabaseMessage>, selected: Set<MsgId>,
                        expanded: Set<MsgId>, markerLine: MsgId?): List<DisplayMessage> {
      var previous: QuasselDatabase.DatabaseMessage? = null
      var previousDate: ZonedDateTime? = null
      return list.asReversed().map {
        val date = it.time.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS)
        val isSameDay = previousDate?.isEqual(date) ?: false
        val isFollowUp = previous?.sender == it.sender && previous?.type == it.type && isSameDay
        previous = it
        previousDate = date
        DisplayMessage(
          content = it,
          hasDayChange = !isSameDay,
          isFollowUp = isFollowUp,
          isSelected = selected.contains(it.messageId),
          isExpanded = expanded.contains(it.messageId),
          isMarkerLine = markerLine == it.messageId
        )
      }.asReversed()
    }

    val data = combineLatest(viewModel.buffer,
                             viewModel.selectedMessages,
                             viewModel.expandedMessages,
                             viewModel.markerLine)
      .toLiveData().switchMapNotNull { (buffer, selected, expanded, markerLine) ->
        database.filtered().listen(accountId, buffer).switchMapNotNull { filtered ->
          LivePagedListBuilder(
            database.message().findByBufferIdPaged(buffer, filtered).mapByPage {
              processMessages(it, selected.keys, expanded, markerLine.orNull())
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

    viewModel.buffer.toLiveData().observe(this, Observer { bufferId ->
      swipeRefreshLayout.isEnabled = (bufferId != null || bufferId != -1)
    })

    var previousVisible = -1
    viewModel.buffer.toFlowable(BackpressureStrategy.LATEST).switchMap { buffer ->
      database.filtered().listenRx(accountId, buffer).switchMap { filtered ->
        database.message().firstMsgId(buffer).map {
          Pair(it, database.message().firstVisibleMsgId(buffer, filtered))
        }
      }
    }.distinctUntilChanged()
      .throttleLast(1, TimeUnit.SECONDS)
      .toLiveData().observe(this, Observer {
        runInBackground {
          val first = it?.first
          val visible = it?.second ?: -1

          if (first != null) {
            if (previousVisible == visible) {
              loadMore()
            } else {
              requireActivity().runOnUiThread {
                swipeRefreshLayout.isRefreshing = false
              }
            }

            previousVisible = visible
          }
        }
      })

    viewModel.session.toLiveData().zip(lastMessageId).observe(
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

    savedInstanceState?.run {
      messageList.layoutManager.onRestoreInstanceState(getParcelable(KEY_STATE_LIST))
    }

    val avatar_size = resources.getDimensionPixelSize(R.dimen.avatar_size)

    val sizeProvider = FixedPreloadSizeProvider<String>(avatar_size, avatar_size)

    val preloadModelProvider = object : ListPreloader.PreloadModelProvider<String> {
      override fun getPreloadItems(position: Int) = adapter[position]?.avatarUrl?.let {
        mutableListOf(it)
      } ?: mutableListOf()

      override fun getPreloadRequestBuilder(item: String) =
        GlideApp.with(this@MessageListFragment).load(item).override(avatar_size)
    }

    val preloader = RecyclerViewPreloader(Glide.with(this), preloadModelProvider, sizeProvider, 10)

    messageList.addOnScrollListener(preloader)
    messageList.addItemDecoration(DayChangeItemDecoration(adapter))
    messageList.addItemDecoration(MarkerLineItemDecoration(
      adapter, requireContext(), R.dimen.markerline_height, R.attr.colorMarkerLine
    ))

    return view
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(KEY_STATE_LIST, messageList.layoutManager.onSaveInstanceState())
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

  companion object {
    private const val KEY_STATE_LIST = "KEY_STATE_LIST"
  }
}
