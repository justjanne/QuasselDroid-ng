package de.kuschku.quasseldroid.ui.chat.topic

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.input.*
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.util.helper.invoke
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.irc.format.IrcFormatSerializer
import de.kuschku.quasseldroid.viewmodel.EditorViewModel
import javax.inject.Inject

class TopicFragment : SettingsFragment() {
  @BindView(R.id.chatline)
  lateinit var chatline: RichEditText

  @BindView(R.id.formatting_toolbar)
  lateinit var toolbar: RichToolbar

  @BindView(R.id.autocomplete_list_expanded)
  lateinit var autoCompleteList: RecyclerView

  @Inject
  lateinit var autoCompleteSettings: AutoCompleteSettings

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var formatDeserializer: IrcFormatDeserializer

  @Inject
  lateinit var formatSerializer: IrcFormatSerializer

  lateinit var editorHelper: EditorHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_topic, container, false)
    ButterKnife.bind(this, view)

    val editorViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
    editorViewModel.quasselViewModel.onNext(viewModel)

    val autoCompleteHelper = AutoCompleteHelper(
      requireActivity(),
      autoCompleteSettings,
      messageSettings,
      formatDeserializer,
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

    val bufferId = arguments?.getInt("buffer", -1) ?: -1
    viewModel.buffer.onNext(bufferId)
    viewModel.bufferData.filter {
      it.info != null
    }.firstElement().toLiveData().observe(this, Observer {
      chatline.setText(formatDeserializer.formatString(chatline.context, it?.description, true))
    })

    return view
  }

  override fun onSave(): Boolean {
    viewModel.session { sessionOptional ->
      val session = sessionOptional.orNull()
      viewModel.buffer { bufferId ->
        session?.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
          val topic = formatSerializer.toEscapeCodes(chatline.text)
          session.rpcHandler?.sendInput(bufferInfo, "/topic $topic")
          return true
        }
      }
    }
    return false
  }
}
