package de.kuschku.quasseldroid.ui.chat.input

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.ui.ColorChooserDialog
import de.kuschku.quasseldroid.util.ui.EditTextSelectionChange
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class Editor(
  // Contexts
  activity: AppCompatActivity,
  // LiveData
  private val autoCompleteData: LiveData<Pair<String, List<AutoCompleteItem>>>,
  lastWordContainer: BehaviorSubject<Observable<Pair<String, IntRange>>>,
  // Views
  val chatline: EditTextSelectionChange,
  send: AppCompatImageButton,
  tabComplete: AppCompatImageButton,
  autoCompleteLists: List<RecyclerView>,
  formattingToolbar: Toolbar,
  // Settings
  private val appearanceSettings: AppearanceSettings,
  private val autoCompleteSettings: AutoCompleteSettings,
  // Listeners
  private val sendCallback: (Sequence<Pair<CharSequence, String>>) -> Unit,
  private val panelStateCallback: (Boolean) -> Unit
) : ActionMenuView.OnMenuItemClickListener, Toolbar.OnMenuItemClickListener {
  override fun onMenuItemClick(item: MenuItem?) = when (item?.itemId) {
    R.id.action_input_history -> {
      panelStateCallback(true)
      true
    }
    else                      -> false
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
        val sequence = if (end > previous.range.start) "" else s.substring(previous.range.start,
                                                                           end)
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
      // This is still broken when mixing tab complete and UI auto complete
      formatHandler::autoComplete
    )

    autoCompleteData.observe(activity, Observer {
      val query = it?.first ?: ""
      val shouldShowResults = (autoCompleteSettings.auto && query.length >= 3) ||
                              (autoCompleteSettings.prefix && query.startsWith('@')) ||
                              (autoCompleteSettings.prefix && query.startsWith('#'))
      autocompleteAdapter.submitList(if (shouldShowResults) it?.second.orEmpty() else emptyList())
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

    lastWordContainer.onNext(lastWord)

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
        formatHandler.foregroundColor(chatline.selection)
        ?: formatHandler.defaultForegroundColor
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
        formatHandler.backgroundColor(chatline.selection)
        ?: formatHandler.defaultBackgroundColor
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

    chatline.setOnKeyListener { _, keyCode, event ->
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
          autoComplete(event.isShiftPressed)
          true
        }
        else                          -> false
      }
    }
  }

  private fun showColorChooser(
    activity: FragmentActivity, @StringRes title: Int, @ColorInt preselect: Int, f: (Int?) -> Unit
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
      .preselect(preselect)
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
      sendCallback(strippedText.lineSequence().zip(formattedText))
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

  private fun autoComplete(reverse: Boolean = false) {
    val originalWord = lastWord.value

    val previous = autocompletionState
    if (!originalWord.second.isEmpty()) {
      val autoCompletedWords = autoCompleteData.value?.second.orEmpty()
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