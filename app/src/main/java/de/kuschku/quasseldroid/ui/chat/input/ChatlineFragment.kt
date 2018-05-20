/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat.input

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.irc.format.IrcFormatSerializer
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.viewmodel.EditorViewModel
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
  lateinit var ircFormatSerializer: IrcFormatSerializer

  @Inject
  lateinit var autoCompleteAdapter: AutoCompleteAdapter

  @Inject
  lateinit var editorViewModel: EditorViewModel

  lateinit var editorHelper: EditorHelper

  lateinit var autoCompleteHelper: AutoCompleteHelper

  lateinit var historyBottomSheet: BottomSheetBehavior<View>

  val panelSlideListener = object : BottomSheetBehavior.BottomSheetCallback() {
    override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

    override fun onStateChanged(bottomSheet: View, newState: Int) {
      editorHelper.setMultiLine(newState != BottomSheetBehavior.STATE_COLLAPSED)
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_chatline, container, false)
    ButterKnife.bind(this, view)

    editorViewModel.quasselViewModel.onNext(viewModel)

    autoCompleteHelper = AutoCompleteHelper(
      requireActivity(),
      autoCompleteSettings,
      messageSettings,
      ircFormatDeserializer,
      editorViewModel
    )

    editorHelper = EditorHelper(
      requireActivity(),
      chatline,
      toolbar,
      autoCompleteHelper,
      autoCompleteSettings,
      appearanceSettings
    )

    editorViewModel.lastWord.onNext(editorHelper.lastWord)

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
    close.setOnClickListener {
      historyBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }
    messageHistory.adapter = messageHistoryAdapter
    viewModel.recentlySentMessages.toLiveData()
      .observe(this, Observer(messageHistoryAdapter::submitList))

    fun send() {
      if (chatline.text.isNotBlank()) {
        val lines = chatline.text.lineSequence().map {
          SpannableString(it).apply {
            for (span in getSpans(0, length, Any::class.java)) {
              if (getSpanFlags(span) and Spanned.SPAN_COMPOSING != 0) {
                removeSpan(span)
              }
            }
          } to ircFormatSerializer.toEscapeCodes(SpannableString(it))
        }

        for ((stripped, _) in lines) {
          viewModel.addRecentlySentMessage(stripped)
        }
        viewModel.session { sessionOptional ->
          val session = sessionOptional.orNull()
          viewModel.buffer { bufferId ->
            session?.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
              val output = mutableListOf<IAliasManager.Command>()
              for ((_, formatted) in lines) {
                session.aliasManager?.processInput(bufferInfo, formatted, output)
              }
              for (command in output) {
                session.rpcHandler?.sendInput(command.buffer, command.message)
              }
            }
          }
        }
      }
      chatline.setText("")
    }

    editorHelper.setOnEnterListener(::send)
    send.setOnClickListener { send() }

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
    if (chatline.text.isNotBlank()) {
      chatline.text.lineSequence().forEach(viewModel::addRecentlySentMessage)
    }
    editorHelper.replaceText(text)
  }
}
