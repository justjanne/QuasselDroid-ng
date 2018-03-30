package de.kuschku.quasseldroid.ui.chat

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.info.InfoActivity
import de.kuschku.quasseldroid.ui.chat.info.InfoDescriptor
import de.kuschku.quasseldroid.ui.chat.info.InfoType
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import javax.inject.Inject

class ToolbarFragment : ServiceBoundFragment() {
  @BindView(R.id.toolbar_title)
  lateinit var toolbarTitle: TextView

  @BindView(R.id.toolbar_subtitle)
  lateinit var toolbarSubtitle: TextView

  @BindView(R.id.toolbar_action_area)
  lateinit var actionArea: View

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var messageSettings: MessageSettings

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

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_toolbar, container, false)
    ButterKnife.bind(this, view)

    combineLatest(viewModel.bufferData, viewModel.isSecure, viewModel.lag).toLiveData()
      .observe(this, Observer {
        if (it != null) {
          val (data, isSecure, lag) = it
          if (data?.info?.type?.hasFlag(Buffer_Type.StatusBuffer) == true) {
            this.title = data.network?.networkName()
          } else {
            this.title = data?.info?.bufferName
          }

          if (lag == 0L || !appearanceSettings.showLag) {
            this.subtitle = colorizeDescription(data?.description)
          } else {
            val description = colorizeDescription(data?.description)
            if (description.isBlank()) {
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
      })

    actionArea.setOnClickListener {
      viewModel.bufferData.value?.info?.let { info ->
        when (info.type.toInt()) {
          BufferInfo.Type.QueryBuffer.toInt()   -> InfoDescriptor(
            type = InfoType.User,
            nick = info.bufferName,
            network = info.networkId
          )
          BufferInfo.Type.ChannelBuffer.toInt() -> InfoDescriptor(
            type = InfoType.Channel,
            channel = info.bufferName,
            network = info.networkId
          )
          BufferInfo.Type.StatusBuffer.toInt()  -> InfoDescriptor(
            type = InfoType.Network,
            network = info.networkId
          )
          else                                  -> null
        }
      }?.let { infoDescriptor ->
        val intent = Intent(requireContext(), InfoActivity::class.java)
        intent.putExtra("info", infoDescriptor)
        startActivity(intent)
      }
    }

    return view
  }

  private fun colorizeDescription(description: String?) = ircFormatDeserializer.formatString(
    requireContext(), description, messageSettings.colorizeMirc
  )
}