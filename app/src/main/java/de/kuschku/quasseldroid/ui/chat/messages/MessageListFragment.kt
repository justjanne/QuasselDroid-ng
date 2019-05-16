/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat.messages

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.*
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.FixedPreloadSizeProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.BufferSyncer
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helper.*
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.dao.*
import de.kuschku.quasseldroid.persistence.db.AccountDatabase
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.persistence.models.MessageData
import de.kuschku.quasseldroid.service.BacklogRequester
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.BacklogSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.info.user.UserInfoActivity
import de.kuschku.quasseldroid.util.Patterns
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import de.kuschku.quasseldroid.viewmodel.ChatViewModel
import de.kuschku.quasseldroid.viewmodel.data.Avatar
import de.kuschku.quasseldroid.viewmodel.helper.ChatViewModelHelper
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt

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
  lateinit var autoCompleteSettings: AutoCompleteSettings

  @Inject
  lateinit var backlogSettings: BacklogSettings

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var database: QuasselDatabase

  @Inject
  lateinit var accountDatabase: AccountDatabase

  @Inject
  lateinit var adapter: MessageAdapter

  @Inject
  lateinit var chatViewModel: ChatViewModel

  @Inject
  lateinit var modelHelper: ChatViewModelHelper

  private lateinit var linearLayoutManager: LinearLayoutManager

  private lateinit var backlogRequester: BacklogRequester

  private var lastBuffer: BufferId? = null
  private var previousMessageId: MsgId? = null
  private var previousLoadKey: Int? = null

  private var actionMode: ActionMode? = null

  private val actionModeCallback = object : ActionMode.Callback {
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = when (item?.itemId) {
      R.id.action_user_info -> {
        modelHelper.chat.selectedMessages.value?.values?.firstOrNull()?.let { msg ->
          modelHelper.connectedSession.value?.orNull()?.bufferSyncer?.let { bufferSyncer ->
            modelHelper.bufferData.value?.info?.let(BufferInfo::networkId)?.let { networkId ->
              UserInfoActivity.launch(
                requireContext(),
                openBuffer = false,
                bufferId = bufferSyncer.find(
                  bufferName = HostmaskHelper.nick(msg.original.sender),
                  networkId = networkId,
                  type = Buffer_Type.of(Buffer_Type.QueryBuffer)
                )?.let(BufferInfo::bufferId),
                nick = HostmaskHelper.nick(msg.original.sender),
                networkId = networkId
              )
            }
          }

          true
        } ?: false
      }
      R.id.action_copy      -> {
        val builder = SpannableStringBuilder()
        modelHelper.chat.selectedMessages.value?.values.orEmpty().asSequence().sortedBy {
          it.original.messageId
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

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(null, data)
        clipboard.primaryClip = clip
        actionMode?.finish()
        true
      }
      R.id.action_share     -> {
        val builder = SpannableStringBuilder()
        modelHelper.chat.selectedMessages.value?.values.orEmpty().asSequence().sortedBy {
          it.original.messageId
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
      else                  -> false
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
      modelHelper.chat.selectedMessages.onNext(emptyMap())
    }
  }

  private val boundaryCallback = object : PagedList.BoundaryCallback<DisplayMessage>() {
    override fun onItemAtFrontLoaded(itemAtFront: DisplayMessage) = Unit
    override fun onItemAtEndLoaded(itemAtEnd: DisplayMessage) {
      loadMore()
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.chat_messages, container, false)
    ButterKnife.bind(this, view)

    linearLayoutManager = LinearLayoutManager(context)
    linearLayoutManager.reverseLayout = true

    backlogRequester = BacklogRequester(modelHelper.connectedSession, database, accountDatabase)

    adapter.setOnClickListener { msg ->
      if (actionMode != null) {
        when (modelHelper.chat.selectedMessagesToggle(msg.original.messageId, msg)) {
          0    -> actionMode?.finish()
          1    -> actionMode?.menu?.findItem(R.id.action_user_info)?.isVisible = true
          else -> actionMode?.menu?.findItem(R.id.action_user_info)?.isVisible = false
        }
      } else if (msg.hasSpoilers) {
        val value = modelHelper.chat.expandedMessages.value.orEmpty()
        modelHelper.chat.expandedMessages.onNext(
          if (value.contains(msg.original.messageId)) value - msg.original.messageId
          else value + msg.original.messageId
        )
      }
    }
    adapter.setOnLongClickListener { msg ->
      if (actionMode == null) {
        activity?.startActionMode(actionModeCallback)
      }
      when (modelHelper.chat.selectedMessagesToggle(msg.original.messageId, msg)) {
        0    -> actionMode?.finish()
        1    -> actionMode?.menu?.findItem(R.id.action_user_info)?.isVisible = true
        else -> actionMode?.menu?.findItem(R.id.action_user_info)?.isVisible = false
      }
    }
    if (autoCompleteSettings.senderDoubleClick)
      adapter.setOnDoubleClickListener { msg ->
        ChatActivity.launch(
          requireContext(),
          autoCompleteText = HostmaskHelper.nick(msg.sender),
          autoCompleteSuffix = ": "
        )
      }
    adapter.setOnSenderIconClickListener { msg ->
      modelHelper.connectedSession.value?.orNull()?.bufferSyncer?.let { bufferSyncer ->
        modelHelper.bufferData.value?.info?.let(BufferInfo::networkId)?.let { networkId ->
          UserInfoActivity.launch(
            requireContext(),
            openBuffer = false,
            bufferId = bufferSyncer.find(
              bufferName = HostmaskHelper.nick(msg.sender),
              networkId = networkId,
              type = Buffer_Type.of(Buffer_Type.QueryBuffer)
            )?.let(BufferInfo::bufferId),
            nick = HostmaskHelper.nick(msg.sender),
            networkId = networkId
          )
        }
      }
    }
    adapter.setOnUrlLongClickListener(LinkLongClickMenuHelper())

    messageList.adapter = adapter
    messageList.layoutManager = linearLayoutManager
    messageList.itemAnimator = null

    val senderColors = requireContext().theme.styledAttributes(
      R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
      R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
      R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
      R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
    ) {
      IntArray(length()) {
        getColor(it, 0)
      }
    }

    swipeRefreshLayout.setColorSchemeColors(*senderColors)
    swipeRefreshLayout.setOnRefreshListener {
      loadMore()
    }

    var isScrolling = false
    messageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val canScrollDown = recyclerView.canScrollVertically(1)
        val isScrollingDown = dy > 0

        scrollDown.toggle(canScrollDown && isScrollingDown)
      }

      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        isScrolling = when (newState) {
          RecyclerView.SCROLL_STATE_SETTLING, RecyclerView.SCROLL_STATE_IDLE -> false
          RecyclerView.SCROLL_STATE_DRAGGING                                 -> true
          else                                                               -> isScrolling
        }
      }
    })

    fun processMessages(list: List<MessageData>, selected: Set<MsgId>,
                        expanded: Set<MsgId>, markerLine: MsgId?): List<DisplayMessage> {
      var previous: MessageData? = null
      var previousDate: ZonedDateTime? = null
      return list.mapReverse {
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
          isMarkerLine = markerLine == it.messageId,
          // Do not run the emoji regex if not necessary, this saves quite a bit of performance
          isEmoji = it.type.hasFlag(Message_Type.Plain) &&
                    it.content.isNotBlank() &&
                    messageSettings.largerEmoji && Patterns.EMOJI.matches(it.content)
        )
      }
    }

    val data = combineLatest(modelHelper.chat.bufferId,
                             modelHelper.chat.selectedMessages,
                             modelHelper.chat.expandedMessages,
                             modelHelper.markerLine)
      .toLiveData().switchMapNotNull { (buffer, selected, expanded, markerLine) ->
        accountDatabase.accounts().listen(accountId).switchMap {
          database.filtered().listen(accountId,
                                     buffer,
                                     it.defaultFiltered).switchMapNotNull { filtered ->
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
            ).setBoundaryCallback(boundaryCallback)
              .setInitialLoadKey(previousLoadKey)
              .build()
          }
        }
      }

    val lastMessageId = modelHelper.chat.bufferId.toLiveData().switchMapNotNull {
      database.message().lastMsgId(it)
    }

    modelHelper.chat.bufferId.toLiveData().observe(this, Observer { bufferId ->
      swipeRefreshLayout.isEnabled = (bufferId != null || bufferId?.isValidId() == true)
    })

    modelHelper.sessionManager.mapSwitchMap(SessionManager::state).distinctUntilChanged().toLiveData().observe(
      this, Observer {
      if (it?.orNull() == ConnectionState.CONNECTED) {
        runInBackgroundDelayed(16) {
          modelHelper.chat.bufferId { bufferId ->
            val filtered = database.filtered().get(accountId,
                                                   bufferId,
                                                   accountDatabase.accounts().findById(accountId)?.defaultFiltered
                                                   ?: 0)
            // Try loading messages when switching to isEmpty bufferId
            val hasVisibleMessages = database.message().hasVisibleMessages(bufferId, filtered)
            if (!hasVisibleMessages) {
              if (bufferId.isValidId() && bufferId != BufferId.MAX_VALUE) {
                loadMore(initial = true)
              }
            }
          }
        }
      }
    })

    modelHelper.connectedSession.toLiveData().zip(lastMessageId).observe(
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

    var hasLoaded = false
    fun checkScroll() {
      if (hasLoaded) {
        if (linearLayoutManager.findFirstVisibleItemPosition() < 2 && !isScrolling) {
          messageList.scrollToPosition(0)
        }
      } else {
        savedInstanceState?.apply {
          (messageList.layoutManager as RecyclerView.LayoutManager).onRestoreInstanceState(
            getParcelable(KEY_STATE_LIST))
        }
        hasLoaded = true
      }
    }

    adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onChanged() = checkScroll()
      override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = checkScroll()
    })

    scrollDown.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
      override fun onHidden(fab: FloatingActionButton) {
        (fab as View).visibility = View.VISIBLE
      }
    })
    scrollDown.setOnClickListener { messageList.scrollToPosition(0) }

    val avatarSize = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_SP,
      messageSettings.textSize * 2.5f,
      requireContext().resources.displayMetrics
    ).roundToInt()

    val sizeProvider = FixedPreloadSizeProvider<List<Avatar>>(avatarSize, avatarSize)

    val preloadModelProvider = object : ListPreloader.PreloadModelProvider<List<Avatar>> {
      override fun getPreloadItems(position: Int) = listOfNotNull(
        adapter[position]?.content?.let { AvatarHelper.avatar(messageSettings, it, avatarSize) }
      )

      override fun getPreloadRequestBuilder(item: List<Avatar>) =
        GlideApp.with(this@MessageListFragment).loadWithFallbacks(item)?.override(avatarSize)
    }

    val preloader = RecyclerViewPreloader(Glide.with(this), preloadModelProvider, sizeProvider, 10)

    messageList.addOnScrollListener(preloader)
    messageList.addItemDecoration(MarkerLineItemDecoration(
      adapter, requireContext(), R.dimen.markerline_height, R.attr.colorMarkerLine
    ))

    savedInstanceState?.run {
      (messageList.layoutManager as RecyclerView.LayoutManager).onRestoreInstanceState(getParcelable(
        KEY_STATE_LIST))
      previousLoadKey = getInt(KEY_STATE_PAGING).nullIf { it == -1 }
      lastBuffer = BufferId(getInt(KEY_STATE_BUFFER)).nullIf { !it.isValidId() }
    }

    data.observe(this, Observer { list ->
      previousLoadKey = list?.lastKey as? Int
      val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
      val firstVisibleMessageId = adapter[firstVisibleItemPosition]?.content?.messageId
      runInBackground {
        activity?.runOnUiThread {
          list?.let(adapter::submitList)
        }

        val buffer = modelHelper.chat.bufferId.value
                     ?: BufferId(-1)
        if (buffer != lastBuffer) {
          adapter.clearCache()
          modelHelper.connectedSession.value?.orNull()?.bufferSyncer?.let { bufferSyncer ->
            onBufferChange(lastBuffer, buffer, firstVisibleMessageId, bufferSyncer)
          }
          lastBuffer = buffer
        }
      }
    })

    return view
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(KEY_STATE_LIST, messageList.layoutManager?.onSaveInstanceState())
    outState.putInt(KEY_STATE_PAGING, previousLoadKey ?: -1)
    outState.putInt(KEY_STATE_BUFFER, lastBuffer?.id ?: -1)
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
    // Try loading messages when switching to isEmpty bufferId
    val filtered = database.filtered().get(accountId,
                                           current,
                                           accountDatabase.accounts().findById(accountId)?.defaultFiltered
                                           ?: 0)
    val hasVisibleMessages = database.message().hasVisibleMessages(current, filtered)
    if (!hasVisibleMessages) {
      if (current.isValidId() && current != BufferId.MAX_VALUE) {
        loadMore(initial = true)
      }
    }
    activity?.runOnUiThread { messageList.scrollToPosition(0) }
  }

  override fun onPause() {
    val previous = lastBuffer
    val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
    val messageId = adapter[firstVisibleItemPosition]?.content?.messageId
    val bufferSyncer = modelHelper.connectedSession.value?.orNull()?.bufferSyncer
    if (previous != null && messageId != null) {
      bufferSyncer?.requestSetMarkerLine(previous, messageId)
    }
    super.onPause()
  }

  private fun loadMore(initial: Boolean = false, lastMessageId: MsgId? = null) {
    // This can be called *after* we’re already detached from the activity
    activity?.runOnUiThread {
      modelHelper.chat.bufferId { bufferId ->
        if (bufferId.isValidId() && bufferId != BufferId.MAX_VALUE) {
          if (initial) swipeRefreshLayout.isRefreshing = true
          runInBackground {
            backlogRequester.loadMore(
              accountId = accountId,
              buffer = bufferId,
              amount = if (initial) backlogSettings.initialAmount else backlogSettings.pageSize,
              pageSize = backlogSettings.pageSize,
              lastMessageId = lastMessageId
                              ?: database.message().findFirstByBufferId(bufferId)?.messageId
                              ?: MsgId(-1),
              untilAllVisible = initial
            ) {
              activity?.runOnUiThread {
                swipeRefreshLayout.isRefreshing = false
              }
            }
          }
        }
      }
    }
  }

  companion object {
    private const val KEY_STATE_LIST = "KEY_STATE_LIST"
    private const val KEY_STATE_PAGING = "KEY_STATE_PAGING"
    private const val KEY_STATE_BUFFER = "KEY_STATE_BUFFER"
  }
}
