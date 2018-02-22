package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.ui.settings.Settings
import de.kuschku.quasseldroid_ng.ui.settings.data.AppearanceSettings
import de.kuschku.quasseldroid_ng.ui.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid_ng.util.helper.invoke
import de.kuschku.quasseldroid_ng.util.helper.let
import de.kuschku.quasseldroid_ng.util.irc.format.IrcFormatSerializer
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment

class EditorFragment : ServiceBoundFragment() {

  @BindView(R.id.send)
  lateinit var send: ImageButton

  @BindView(R.id.chatline)
  lateinit var chatline: EditText

  private lateinit var viewModel: QuasselViewModel

  private var ircFormatSerializer: IrcFormatSerializer? = null
  private lateinit var appearanceSettings: AppearanceSettings

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    viewModel = ViewModelProviders.of(activity!!)[QuasselViewModel::class.java]
    appearanceSettings = Settings.appearance(activity!!)

    if (ircFormatSerializer == null) {
      ircFormatSerializer = IrcFormatSerializer(context!!)
    }
  }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_editor, container, false)
    ButterKnife.bind(this, view)



    send.setOnClickListener {
      send()
    }

    chatline.setOnKeyListener { _, keyCode, event ->
      if (event.hasNoModifiers() && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
        send()
      }
      false
    }

    return view
  }

  fun send() {
    viewModel.session { session ->
      viewModel.getBuffer().let { bufferId ->
        session.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
          session.rpcHandler?.sendInput(bufferInfo, chatline.text.toString())
        }
      }
    }
    chatline.text.clear()
  }
}