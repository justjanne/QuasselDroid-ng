package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.ui.settings.data.AppearanceSettings
import de.kuschku.quasseldroid_ng.ui.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid_ng.util.helper.visibleIf
import de.kuschku.quasseldroid_ng.util.helper.zip
import de.kuschku.quasseldroid_ng.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid_ng.util.ui.SpanFormatter

class ToolbarFragment : ServiceBoundFragment() {
  @BindView(R.id.toolbar_title)
  lateinit var toolbarTitle: TextView

  @BindView(R.id.toolbar_subtitle)
  lateinit var toolbarSubtitle: TextView

  private lateinit var viewModel: QuasselViewModel

  private var ircFormatDeserializer: IrcFormatDeserializer? = null
  private val appearanceSettings = AppearanceSettings(
    showPrefix = AppearanceSettings.ShowPrefixMode.FIRST,
    colorizeNicknames = AppearanceSettings.ColorizeNicknamesMode.ALL_BUT_MINE,
    colorizeMirc = true,
    timeFormat = ""
  )

  var title: CharSequence?
    get() = toolbarTitle.text
    set(value) {
      if (value != null)
        toolbarTitle.text = value
      else
        toolbarTitle.setText(R.string.app_name)
    }

  var subtitle: CharSequence?
    get() = toolbarTitle.text
    set(value) {
      toolbarSubtitle.text = value ?: ""
      toolbarSubtitle.visibleIf(value?.isNotEmpty() == true)
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    viewModel = ViewModelProviders.of(activity!!)[QuasselViewModel::class.java]

    if (ircFormatDeserializer == null) {
      ircFormatDeserializer = IrcFormatDeserializer(context!!)
    }
  }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_toolbar, container, false)
    ButterKnife.bind(this, view)

    viewModel.bufferData.zip(viewModel.isSecure, viewModel.lag).observe(
      this, Observer {
      if (it != null) {
        val (data, isSecure, lag) = it
        if (data?.info?.type?.hasFlag(Buffer_Type.StatusBuffer) == true) {
          this.title = data.network?.networkName
        } else {
          this.title = data?.info?.bufferName
        }

        if (lag == 0L || !appearanceSettings.showLag) {
          this.subtitle = colorizeDescription(data?.description)
        } else {
          val description = colorizeDescription(data?.description)
          if (description.isNullOrBlank()) {
            this.subtitle = "Lag: ${lag}ms"
          } else {
            this.subtitle = SpanFormatter.format(
              "Lag: %dms | %s",
              lag,
              colorizeDescription(data?.description)
            )
          }
        }
      }
    }
    )

    return view
  }

  private fun colorizeDescription(description: String?) = ircFormatDeserializer?.formatString(
    description, appearanceSettings.colorizeMirc
  )
                                                          ?: description

  data class BufferData(
    val info: BufferInfo? = null,
    val network: INetwork.NetworkInfo? = null,
    val description: String? = null
  )

}