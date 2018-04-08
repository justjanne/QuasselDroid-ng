package de.kuschku.quasseldroid.ui.chat.input

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.helper.invoke
import de.kuschku.quasseldroid.util.helper.lineSequence
import de.kuschku.quasseldroid.util.helper.retint
import de.kuschku.quasseldroid.util.helper.visibleIf
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

  @BindView(R.id.history_panel)
  lateinit var historyPanel: SlidingUpPanelLayout

  @BindView(R.id.autocomplete_list)
  lateinit var autoCompleteList: RecyclerView

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

  lateinit var editorHelper: EditorHelper

  lateinit var autoCompleteHelper: AutoCompleteHelper

  val panelSlideListener = object : SlidingUpPanelLayout.PanelSlideListener {
    override fun onPanelSlide(panel: View?, slideOffset: Float) = Unit

    override fun onPanelStateChanged(panel: View?,
                                     previousState: SlidingUpPanelLayout.PanelState?,
                                     newState: SlidingUpPanelLayout.PanelState?) {
      editorHelper.setMultiLine(newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_chatline, container, false)
    ButterKnife.bind(this, view)


    val editorViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
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

    if (autoCompleteSettings.prefix || autoCompleteSettings.auto) {
      val autocompleteAdapter = AutoCompleteAdapter(messageSettings, chatline::autoComplete)
      autoCompleteList.layoutManager = LinearLayoutManager(activity)
      autoCompleteList.itemAnimator = DefaultItemAnimator()
      autoCompleteList.adapter = autocompleteAdapter
      autoCompleteHelper.setDataListener {
        autocompleteAdapter.submitList(it)
      }
    }

    messageHistory.itemAnimator = DefaultItemAnimator()
    messageHistory.layoutManager = LinearLayoutManager(requireContext())
    val messageHistoryAdapter = MessageHistoryAdapter()
    messageHistoryAdapter.setOnItemClickListener { text ->
      editorHelper.replaceText(text)
      historyPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }
    messageHistory.adapter = messageHistoryAdapter
    viewModel.recentlySentMessages_liveData.observe(
      this, Observer(messageHistoryAdapter::submitList)
    )

    fun send() {
      if (chatline.text.isNotBlank()) {
        val lines = chatline.text.lineSequence().map {
          it.toString() to ircFormatSerializer.toEscapeCodes(SpannableString(it))
        }

        viewModel.session { sessionOptional ->
          val session = sessionOptional.orNull()
          viewModel.buffer { bufferId ->
            session?.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
              val output = mutableListOf<IAliasManager.Command>()
              for ((stripped, formatted) in lines) {
                viewModel.addRecentlySentMessage(stripped)
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
          historyPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
          true
        }
        else                      -> false
      }
    }

    return view
  }
}
