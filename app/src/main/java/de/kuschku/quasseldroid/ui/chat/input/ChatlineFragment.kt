/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.quasseldroid.ui.chat.input

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helper.invoke
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.emoji.EmojiData
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.irc.format.IrcFormatSerializer
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import javax.inject.Inject

class ChatlineFragment : ServiceBoundFragment() {
  @BindView(R.id.chatline)
  lateinit var chatline: RichEditText

  @BindView(R.id.formatting_toolbar)
  lateinit var toolbar: RichToolbar

  @BindView(R.id.send)
  lateinit var send: AppCompatImageButton

  @BindView(R.id.tab_complete)
  lateinit var tabComplete: AppCompatImageButton

  @BindView(R.id.msg_history)
  lateinit var messageHistory: RecyclerView

  @BindView(R.id.autocomplete_list)
  lateinit var autoCompleteList: RecyclerView

  @BindView(R.id.close)
  lateinit var close: AppCompatImageButton

  @BindView(R.id.card_panel)
  lateinit var cardPanel: View

  @BindView(R.id.editor_container)
  lateinit var editorContainer: View

  @Inject
  lateinit var autoCompleteSettings: AutoCompleteSettings

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  @Inject
  lateinit var contentFormatter: ContentFormatter

  @Inject
  lateinit var ircFormatSerializer: IrcFormatSerializer

  @Inject
  lateinit var autoCompleteAdapter: AutoCompleteAdapter

  lateinit var editorHelper: EditorHelper

  lateinit var autoCompleteHelper: AutoCompleteHelper

  lateinit var historyBottomSheet: BottomSheetBehavior<View>

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  val panelSlideListener = object : BottomSheetBehavior.BottomSheetCallback() {
    override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

    override fun onStateChanged(bottomSheet: View, newState: Int) {
      editorHelper.setMultiLine(newState != BottomSheetBehavior.STATE_COLLAPSED)
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.chat_chatline, container, false)
    ButterKnife.bind(this, view)

    autoCompleteHelper = AutoCompleteHelper(
      requireActivity(),
      autoCompleteSettings,
      messageSettings,
      ircFormatDeserializer,
      contentFormatter,
      modelHelper
    )

    editorHelper = EditorHelper(
      requireActivity(),
      chatline,
      toolbar,
      autoCompleteHelper,
      autoCompleteSettings,
      appearanceSettings
    )

    modelHelper.editor.lastWord.onNext(editorHelper.lastWord)

    val autoCompleteBottomSheet = BottomSheetBehavior.from(autoCompleteList)
    if (autoCompleteSettings.prefix || autoCompleteSettings.auto) {
      autoCompleteAdapter.setOnClickListener(chatline::autoComplete)
      autoCompleteList.layoutManager = LinearLayoutManager(context)
      autoCompleteList.itemAnimator = DefaultItemAnimator()
      autoCompleteList.adapter = autoCompleteAdapter
      autoCompleteHelper.addDataListener {
        autoCompleteBottomSheet.state =
          if (it.isEmpty()) BottomSheetBehavior.STATE_HIDDEN
          else BottomSheetBehavior.STATE_COLLAPSED
        autoCompleteAdapter.submitList(it)
      }
    }

    historyBottomSheet = BottomSheetBehavior.from(cardPanel)
    historyBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    messageHistory.itemAnimator = DefaultItemAnimator()
    messageHistory.layoutManager = LinearLayoutManager(requireContext())
    val messageHistoryAdapter = MessageHistoryAdapter()
    messageHistoryAdapter.setOnItemClickListener { text ->
      editorHelper.replaceText(text)
      historyBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }
    close.setTooltip()
    close.setOnClickListener {
      historyBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }
    messageHistory.adapter = messageHistoryAdapter
    modelHelper.chat.recentlySentMessages.toLiveData()
      .observe(viewLifecycleOwner, Observer(messageHistoryAdapter::submitList))
    messageHistoryAdapter.setOnUpdateFinishedListener {
      messageHistory.scrollToPosition(0)
    }

    fun send() {
      val safeText =
        if (messageSettings.replaceEmoji) EmojiData.replaceShortCodes(chatline.safeText)
        else chatline.safeText

      if (safeText.isNotEmpty()) {
        val lines = safeText.lineSequence().map {
          SpannableString(it).apply {
            for (span in getSpans(0, length, Any::class.java)) {
              if (getSpanFlags(span) and Spanned.SPAN_COMPOSING != 0) {
                removeSpan(span)
              }
            }
          } to ircFormatSerializer.toEscapeCodes(SpannableString(it))
        }

        for ((stripped, _) in lines) {
          modelHelper.chat.addRecentlySentMessage(stripped)
        }
        modelHelper.connectedSession { sessionOptional ->
          val session = sessionOptional.orNull()
          modelHelper.chat.bufferId { bufferId ->
            session?.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
              val output = mutableListOf<IAliasManager.Command>()
              for ((_, formatted) in lines) {
                session.aliasManager.processInput(bufferInfo, formatted, output)
              }
              for (command in output) {
                if (command.message.startsWith("/join", ignoreCase = true)) {
                  val channel = command.message
                    .substringAfter(' ')
                    .substringBefore(' ')
                    .split(",")
                    .last()

                  modelHelper.chat.chatToJoin.onNext(Optional.of(
                    Pair(command.buffer.networkId, channel)
                  ))
                }
              }
              for (command in output) {
                session.rpcHandler.sendInput(command.buffer, command.message)
              }
            }
          }
        }
      }
      modelHelper.chat.recentMessagesIndexReset()
      chatline.setText("")
    }

    editorHelper.setOnEnterListener(::send)
    editorHelper.setOnDownListener {
      chatline.setText(modelHelper.chat.recentMessagesIndexDown(chatline.safeText))
    }
    editorHelper.setOnUpListener {
      chatline.setText(modelHelper.chat.recentMessagesIndexUp())
    }
    send.setOnClickListener { send() }
    send.setTooltip()

    tabComplete.setTooltip()
    tabComplete.visibleIf(autoCompleteSettings.button)
    tabComplete.setOnClickListener {
      autoCompleteHelper.autoComplete()
    }

    toolbar.inflateMenu(R.menu.editor)
    toolbar.menu.retint(requireActivity())
    toolbar.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.action_input_history -> {
          historyBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
          true
        }
        else                      -> false
      }
    }

    return view
  }

  fun replaceText(text: CharSequence) {
    if (chatline.safeText.isNotEmpty()) {
      chatline.safeText.lineSequence().forEach(modelHelper.chat::addRecentlySentMessage)
    }
    editorHelper.replaceText(text)
  }
}
