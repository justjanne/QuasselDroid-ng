package de.kuschku.quasseldroid.ui.chat.input

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.helper.lastWordIndices
import de.kuschku.quasseldroid.util.helper.lineSequence
import de.kuschku.quasseldroid.util.helper.retint
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class Editor(
  // Contexts
  activity: AppCompatActivity,
  // LiveData
  private val autoCompleteData: LiveData<Pair<String, List<AutoCompleteItem>>?>,
  lastWordContainer: MutableLiveData<Observable<Pair<String, IntRange>>>,
  // Views
  val chatline: AppCompatEditText,
  send: AppCompatImageButton,
  autoCompleteLists: List<RecyclerView>,
  formattingMenu: ActionMenuView,
  formattingToolbar: Toolbar,
  // Listeners
  private val sendCallback: (Sequence<Pair<CharSequence, String>>) -> Unit,
  private val panelStateCallback: (Boolean) -> Unit
) : ActionMenuView.OnMenuItemClickListener, Toolbar.OnMenuItemClickListener {
  override fun onMenuItemClick(item: MenuItem?) = when (item?.itemId) {
    R.id.input_history -> {
      panelStateCallback(true)
      true
    }
    else               -> formatHandler.onMenuItemClick(item)
  }

  private val appearanceSettings = Settings.appearance(activity)

  private val lastWord = BehaviorSubject.createDefault(Pair("", IntRange.EMPTY))
  private val textWatcher = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
      val previous = autocompletionState
      val next = if (previous != null && s != null) {
        val suffix = if (previous.range.start == 0) ": " else " "
        val end = Math.min(
          s.length, previous.range.start + previous.completion.name.length + suffix.length
        )
        val sequence = s.substring(previous.range.start, end)
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
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
  }

  val formatHandler = FormatHandler(chatline)

  private var autocompletionState: ChatActivity.AutoCompletionState? = null

  init {
    send.setOnClickListener {
      send()
    }

    chatline.setOnKeyListener { _, keyCode, event ->
      when (keyCode) {
        KeyEvent.KEYCODE_ENTER,
        KeyEvent.KEYCODE_NUMPAD_ENTER -> if (event.hasNoModifiers()) {
          send()
          true
        } else {
          false
        }
        KeyEvent.KEYCODE_TAB          -> {
          autoComplete(event.isShiftPressed)
          true
        }
        else                          -> false
      }
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

    chatline.addTextChangedListener(textWatcher)


    val autocompleteAdapter = AutoCompleteAdapter(
      // This is still broken when mixing tab complete and UI auto complete
      formatHandler::autoComplete
    )

    autoCompleteData.observe(activity, Observer {
      val query = it?.first ?: ""
      val list = if (query.length >= 3) it?.second.orEmpty() else emptyList()

      autocompleteAdapter.submitList(list)
    })

    if (appearanceSettings.showAutocomplete) {
      for (autoCompleteList in autoCompleteLists) {
        autoCompleteList.layoutManager = LinearLayoutManager(activity)
        autoCompleteList.itemAnimator = DefaultItemAnimator()
        autoCompleteList.adapter = autocompleteAdapter
      }
    }

    lastWordContainer.value = lastWord

    activity.menuInflater.inflate(formatHandler.menu, formattingMenu.menu)
    formattingMenu.menu.retint(activity)
    formattingMenu.setOnMenuItemClickListener(this)

    activity.menuInflater.inflate(R.menu.input_panel, formattingToolbar.menu)
    formattingToolbar.menu.retint(activity)
    formattingToolbar.setOnMenuItemClickListener(this)
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