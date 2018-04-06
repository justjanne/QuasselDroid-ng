package de.kuschku.quasseldroid.ui.chat.input

import android.arch.lifecycle.Observer
import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.util.IrcUserUtils
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.ui.ColorChooserDialog
import de.kuschku.quasseldroid.util.ui.EditTextSelectionChange
import de.kuschku.quasseldroid.util.ui.TextDrawable
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import io.reactivex.subjects.BehaviorSubject

class Editor(
  // Contexts
  activity: AppCompatActivity,
  // LiveData
  private val viewModel: QuasselViewModel,
  // Views
  val chatline: EditTextSelectionChange,
  send: AppCompatImageButton,
  tabComplete: AppCompatImageButton,
  autoCompleteLists: List<RecyclerView>,
  formattingToolbar: Toolbar,
  // Helpers
  private val ircFormatDeserializer: IrcFormatDeserializer,
  // Settings
  private val appearanceSettings: AppearanceSettings,
  private val autoCompleteSettings: AutoCompleteSettings,
  private val messageSettings: MessageSettings
  // Listeners
) : ActionMenuView.OnMenuItemClickListener, Toolbar.OnMenuItemClickListener {
  private var sendListener: ((Sequence<Pair<CharSequence, String>>) -> Unit)? = null
  private var panelStateListener: ((Boolean) -> Unit)? = null

  fun setOnSendListener(listener: (Sequence<Pair<CharSequence, String>>) -> Unit) {
    this.sendListener = listener
  }

  fun setOnPanelStateListener(listener: (Boolean) -> Unit) {
    this.panelStateListener = listener
  }

  override fun onMenuItemClick(item: MenuItem?) = when (item?.itemId) {
    R.id.action_input_history -> {
      panelStateListener?.invoke(true)
      true
    }
    else                      -> false
  }

  private val senderColors = activity.theme.styledAttributes(
    R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
    R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
    R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
    R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
  ) {
    IntArray(16) {
      getColor(it, 0)
    }
  }

  private val lastWord = BehaviorSubject.createDefault(Pair("", IntRange.EMPTY))
  private val textWatcher = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
      val previous = autocompletionState
      val next = if (previous != null && s != null) {
        val suffix = if (previous.range.start == 0) ": " else " "
        val end = Math.min(
          s.length, previous.range.start + previous.completion.name.length + suffix.length
        )
        val sequence = if (end < previous.range.start) ""
        else s.substring(previous.range.start, end)
        if (sequence == previous.completion.name + suffix) {
          previous.originalWord to (previous.range.start until end)
        } else {
          autocompletionState = null
          s.lastWordIndices(chatline.selectionStart, onlyBeforeCursor = true)?.let { indices ->
            s.substring(indices) to indices
          }
        }
      } else {
        s?.lastWordIndices(chatline.selectionStart, onlyBeforeCursor = true)?.let { indices ->
          s.substring(indices) to indices
        }
      }

      lastWord.onNext(next ?: Pair("", IntRange.EMPTY))

      updateButtons(chatline.selection)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
  }

  val formatHandler = FormatHandler(chatline)

  private var autocompletionState: ChatActivity.AutoCompletionState? = null

  @BindView(R.id.action_format_bold)
  lateinit var boldButton: View

  @BindView(R.id.action_format_italic)
  lateinit var italicButton: View

  @BindView(R.id.action_format_underline)
  lateinit var underlineButton: View

  @BindView(R.id.action_format_strikethrough)
  lateinit var strikethroughButton: View

  @BindView(R.id.action_format_monospace)
  lateinit var monospaceButton: View

  @BindView(R.id.action_format_foreground)
  lateinit var foregroundButton: View

  @BindView(R.id.action_format_foreground_preview)
  lateinit var foregroundButtonPreview: View

  @BindView(R.id.action_format_background)
  lateinit var backgroundButton: View

  @BindView(R.id.action_format_background_preview)
  lateinit var backgroundButtonPreview: View

  @BindView(R.id.action_format_clear)
  lateinit var clearButton: View

  init {
    send.setOnClickListener {
      send()
    }

    chatline.imeOptions = when (appearanceSettings.inputEnter) {
      AppearanceSettings.InputEnterMode.EMOJI -> listOf(
        EditorInfo.IME_ACTION_NONE,
        EditorInfo.IME_FLAG_NO_EXTRACT_UI
      )
      AppearanceSettings.InputEnterMode.SEND  -> listOf(
        EditorInfo.IME_ACTION_SEND,
        EditorInfo.IME_FLAG_NO_EXTRACT_UI
      )
    }.fold(0, Int::or)

    val autocompleteAdapter = AutoCompleteAdapter(
      messageSettings,
      // This is still broken when mixing tab complete and UI auto complete
      formatHandler::autoComplete
    )

    viewModel.autoCompleteData.toLiveData().observe(activity, Observer {
      val query = it?.first ?: ""
      val shouldShowResults = (autoCompleteSettings.auto && query.length >= 3) ||
                              (autoCompleteSettings.prefix && query.startsWith('@')) ||
                              (autoCompleteSettings.prefix && query.startsWith('#'))
      val list = if (shouldShowResults) it?.second.orEmpty() else emptyList()
      autocompleteAdapter.submitList(list.map {
        if (it is AutoCompleteItem.UserItem) {
          val nickName = it.nick
          val senderColorIndex = IrcUserUtils.senderColor(nickName)
          val rawInitial = nickName.trimStart('-',
                                              '_',
                                              '[',
                                              ']',
                                              '{',
                                              '}',
                                              '|',
                                              '`',
                                              '^',
                                              '.',
                                              '\\')
                             .firstOrNull() ?: nickName.firstOrNull()
          val initial = rawInitial?.toUpperCase().toString()
          val senderColor = senderColors[senderColorIndex]

          fun formatNick(nick: CharSequence): CharSequence {
            val spannableString = SpannableString(nick)
            spannableString.setSpan(
              ForegroundColorSpan(senderColor),
              0,
              nick.length,
              SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
              StyleSpan(Typeface.BOLD),
              0,
              nick.length,
              SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
            )
            return spannableString
          }

          it.copy(
            displayNick = formatNick(it.nick),
            fallbackDrawable = TextDrawable.builder().buildRound(initial, senderColor),
            modes = when (messageSettings.showPrefix) {
              MessageSettings.ShowPrefixMode.ALL ->
                it.modes
              else                               ->
                it.modes.substring(0, Math.min(it.modes.length, 1))
            },
            realname = ircFormatDeserializer.formatString(
              activity, it.realname.toString(), messageSettings.colorizeMirc
            )
          )
        } else {
          it
        }
      })
    })

    if (autoCompleteSettings.prefix || autoCompleteSettings.auto) {
      for (autoCompleteList in autoCompleteLists) {
        autoCompleteList.layoutManager = LinearLayoutManager(activity)
        autoCompleteList.itemAnimator = DefaultItemAnimator()
        autoCompleteList.adapter = autocompleteAdapter
      }
    }

    if (autoCompleteSettings.doubleTap) {
      val gestureDetector = GestureDetector(
        chatline.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
          autoComplete()
          return true
        }

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
          return true
        }
      })
      chatline.setOnTouchListener { _, event ->
        gestureDetector.onTouchEvent(event)
      }
    }

    tabComplete.visibleIf(autoCompleteSettings.button)
    tabComplete.setOnClickListener {
      autoComplete()
    }

    viewModel.lastWord.onNext(lastWord)

    activity.menuInflater.inflate(R.menu.editor, formattingToolbar.menu)
    formattingToolbar.menu.retint(activity)
    formattingToolbar.setOnMenuItemClickListener(this)

    ButterKnife.bind(this, formattingToolbar)

    boldButton.setOnClickListener {
      formatHandler.toggleBold(chatline.selection)
      updateButtons(chatline.selection)
    }
    TooltipCompat.setTooltipText(boldButton, boldButton.contentDescription)

    italicButton.setOnClickListener {
      formatHandler.toggleItalic(chatline.selection)
      updateButtons(chatline.selection)
    }
    TooltipCompat.setTooltipText(italicButton, italicButton.contentDescription)

    underlineButton.setOnClickListener {
      formatHandler.toggleUnderline(chatline.selection)
      updateButtons(chatline.selection)
    }
    TooltipCompat.setTooltipText(underlineButton, underlineButton.contentDescription)

    strikethroughButton.setOnClickListener {
      formatHandler.toggleStrikethrough(chatline.selection)
      updateButtons(chatline.selection)
    }
    TooltipCompat.setTooltipText(strikethroughButton, strikethroughButton.contentDescription)

    monospaceButton.setOnClickListener {
      formatHandler.toggleMonospace(chatline.selection)
      updateButtons(chatline.selection)
    }
    TooltipCompat.setTooltipText(monospaceButton, monospaceButton.contentDescription)

    foregroundButton.setOnClickListener {
      showColorChooser(
        activity,
        R.string.label_foreground,
        formatHandler.foregroundColor(chatline.selection),
        formatHandler.defaultForegroundColor
      ) { color ->
        formatHandler.toggleForeground(chatline.selection, color,
                                       formatHandler.mircColorMap[color])
        updateButtons(chatline.selection)
      }
    }
    TooltipCompat.setTooltipText(foregroundButton, foregroundButton.contentDescription)

    backgroundButton.setOnClickListener {
      showColorChooser(
        activity,
        R.string.label_background,
        formatHandler.backgroundColor(chatline.selection),
        formatHandler.defaultBackgroundColor
      ) { color ->
        formatHandler.toggleBackground(chatline.selection, color,
                                       formatHandler.mircColorMap[color])
        updateButtons(chatline.selection)
      }
    }
    TooltipCompat.setTooltipText(backgroundButton, backgroundButton.contentDescription)

    clearButton.setOnClickListener {
      formatHandler.clearFormatting(chatline.selection)
      updateButtons(chatline.selection)
    }
    TooltipCompat.setTooltipText(clearButton, clearButton.contentDescription)

    chatline.setOnEditorActionListener { _, actionId, event: KeyEvent? ->
      when (actionId) {
        EditorInfo.IME_ACTION_SEND,
        EditorInfo.IME_ACTION_DONE -> {
          if (event?.action == KeyEvent.ACTION_DOWN) send()
          true
        }
        else                       -> false
      }
    }

    chatline.setOnKeyListener { _, keyCode, event: KeyEvent? ->
      if (event?.action == KeyEvent.ACTION_DOWN) {
        if (event.isCtrlPressed && !event.isAltPressed) when (keyCode) {
          KeyEvent.KEYCODE_B -> {
            formatHandler.toggleBold(chatline.selection)
            updateButtons(chatline.selection)
            true
          }
          KeyEvent.KEYCODE_I -> {
            formatHandler.toggleItalic(chatline.selection)
            updateButtons(chatline.selection)
            true
          }
          KeyEvent.KEYCODE_U -> {
            formatHandler.toggleUnderline(chatline.selection)
            updateButtons(chatline.selection)
            true
          }
          else               -> false
        } else when (keyCode) {
          KeyEvent.KEYCODE_ENTER,
          KeyEvent.KEYCODE_NUMPAD_ENTER -> if (event.isShiftPressed) {
            false
          } else {
            send()
            true
          }
          KeyEvent.KEYCODE_TAB          -> {
            if (!event.isAltPressed && !event.isCtrlPressed) {
              autoComplete(event.isShiftPressed)
              true
            } else {
              false
            }
          }
          else                          -> false
        }
      } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
        !(event?.isShiftPressed ?: false)
      } else {
        false
      }
    }
  }

  private fun showColorChooser(
    activity: FragmentActivity,
    @StringRes title: Int,
    @ColorInt preselect: Int?,
    @ColorInt default: Int,
    f: (Int?) -> Unit
  ) {
    var selectedColor: Int? = preselect
    ColorChooserDialog.Builder(chatline.context, title)
      .customColors(intArrayOf(
        formatHandler.mircColors[0],
        formatHandler.mircColors[1],
        formatHandler.mircColors[2],
        formatHandler.mircColors[3],
        formatHandler.mircColors[4],
        formatHandler.mircColors[5],
        formatHandler.mircColors[6],
        formatHandler.mircColors[7],
        formatHandler.mircColors[8],
        formatHandler.mircColors[9],
        formatHandler.mircColors[10],
        formatHandler.mircColors[11],
        formatHandler.mircColors[12],
        formatHandler.mircColors[13],
        formatHandler.mircColors[14],
        formatHandler.mircColors[15]
      ), null)
      .doneButton(R.string.label_select)
      .cancelButton(R.string.label_reset)
      .backButton(R.string.label_back)
      .customButton(R.string.label_colors_custom)
      .presetsButton(R.string.label_colors_mirc)
      .preselect(preselect ?: default)
      .dynamicButtonColor(false)
      .allowUserColorInputAlpha(false)
      .callback(object : ColorChooserDialog.ColorCallback {
        override fun onColorReset(dialog: ColorChooserDialog) {
          selectedColor = null
        }

        override fun onColorSelection(dialog: ColorChooserDialog, color: Int) {
          selectedColor = color
        }

        override fun onColorChooserDismissed(dialog: ColorChooserDialog) {
          f(selectedColor)
        }
      })
      .show(activity)
  }

  fun updateButtons(selection: IntRange) {
    boldButton.isSelected = formatHandler.isBold(selection)
    italicButton.isSelected = formatHandler.isItalic(selection)
    underlineButton.isSelected = formatHandler.isUnderline(selection)
    strikethroughButton.isSelected = formatHandler.isStrikethrough(selection)
    monospaceButton.isSelected = formatHandler.isMonospace(selection)
    foregroundButtonPreview.setBackgroundColor(formatHandler.foregroundColor(selection)
                                               ?: formatHandler.defaultForegroundColor)
    backgroundButtonPreview.setBackgroundColor(formatHandler.backgroundColor(selection)
                                               ?: formatHandler.defaultBackgroundColor)
  }

  fun onStart() {
    chatline.addTextChangedListener(textWatcher)
    chatline.setSelectionChangeListener(::updateButtons)
  }

  fun onStop() {
    chatline.removeTextChangedListener(textWatcher)
    chatline.removeSelectionChangeListener()
  }

  private fun send() {
    if (rawText.isNotBlank()) {
      sendListener?.invoke(strippedText.lineSequence().zip(formattedText))
    }
    chatline.setText("")
  }

  fun setMultiLine(enabled: Boolean) {
    val selectionStart = chatline.selectionStart
    val selectionEnd = chatline.selectionEnd

    if (enabled) {
      chatline.inputType = chatline.inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE.inv()
    } else {
      chatline.inputType = chatline.inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    }

    chatline.setSelection(selectionStart, selectionEnd)
  }

  private fun autoCompleteDataFull(): List<AutoCompleteItem> {
    return viewModel.rawAutoCompleteData.value?.let { (sessionOptional, id, lastWord) ->
      val session = sessionOptional.orNull()
      val bufferInfo = session?.bufferSyncer?.bufferInfo(id)
      session?.networks?.let { networks ->
        session.bufferSyncer?.bufferInfos()?.let { infos ->
          if (bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
            val network = networks[bufferInfo.networkId]
            network?.ircChannel(
              bufferInfo.bufferName
            )?.let { ircChannel ->
              val users = ircChannel.ircUsers()
              val buffers = infos
                .filter {
                  it.type.toInt() == Buffer_Type.ChannelBuffer.toInt()
                }.mapNotNull { info ->
                  networks[info.networkId]?.let { info to it }
                }.map { (info, network) ->
                  val channel = network.ircChannel(info.bufferName) ?: IrcChannel.NULL
                  AutoCompleteItem.ChannelItem(
                    info = info,
                    network = network.networkInfo(),
                    bufferStatus = when (channel) {
                      IrcChannel.NULL -> BufferStatus.OFFLINE
                      else            -> BufferStatus.ONLINE
                    },
                    description = channel.topic()
                  )
                }
              val nicks = users.map { user ->
                val userModes = ircChannel.userModes(user)
                val prefixModes = network.prefixModes()

                val lowestMode = userModes.mapNotNull(prefixModes::indexOf).min()
                                 ?: prefixModes.size

                AutoCompleteItem.UserItem(
                  user.nick(),
                  network.modesToPrefixes(userModes),
                  lowestMode,
                  user.realName(),
                  user.isAway(),
                  network.support("CASEMAPPING"),
                  Regex("[us]id(\\d+)").matchEntire(user.user())?.groupValues?.lastOrNull()?.let {
                    "https://www.irccloud.com/avatar-redirect/$it"
                  }
                )
              }

              val ignoredStartingCharacters = charArrayOf(
                '-', '_', '[', ']', '{', '}', '|', '`', '^', '.', '\\', '@'
              )

              (nicks + buffers).filter {
                it.name.trimStart(*ignoredStartingCharacters)
                  .startsWith(
                    lastWord.first.trimStart(*ignoredStartingCharacters),
                    ignoreCase = true
                  )
              }.sorted()
            }
          } else null
        }
      }
    } ?: emptyList()
  }

  private fun autoComplete(reverse: Boolean = false) {
    val originalWord = lastWord.value

    val previous = autocompletionState
    if (!originalWord.second.isEmpty()) {
      val autoCompletedWords = autoCompleteDataFull()
      if (previous != null && lastWord.value.first == previous.originalWord && lastWord.value.second.start == previous.range.start) {
        val previousIndex = autoCompletedWords.indexOf(previous.completion)
        val autoCompletedWord = if (previousIndex != -1) {
          val change = if (reverse) -1 else +1
          val newIndex = (previousIndex + change + autoCompletedWords.size) % autoCompletedWords.size

          autoCompletedWords[newIndex]
        } else {
          autoCompletedWords.firstOrNull()
        }
        if (autoCompletedWord != null) {
          val newState = ChatActivity.AutoCompletionState(
            previous.originalWord,
            originalWord.second,
            previous.completion,
            autoCompletedWord
          )
          autocompletionState = newState
          formatHandler.autoComplete(newState)
        } else {
          autocompletionState = null
        }
      } else {
        val autoCompletedWord = autoCompletedWords.firstOrNull()
        if (autoCompletedWord != null) {
          val newState = ChatActivity.AutoCompletionState(
            originalWord.first,
            originalWord.second,
            null,
            autoCompletedWord
          )
          autocompletionState = newState
          formatHandler.autoComplete(newState)
        } else {
          autocompletionState = null
        }
      }
    }
  }

  val formattedText: Sequence<String>
    get() = formatHandler.formattedText
  val rawText: CharSequence
    get() = formatHandler.rawText
  val strippedText: CharSequence
    get() = formatHandler.strippedText
}
