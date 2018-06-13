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

package de.kuschku.quasseldroid.ui.chat

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.info.channel.ChannelInfoActivity
import de.kuschku.quasseldroid.ui.chat.info.user.UserInfoActivity
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

    fun colorizeDescription(description: String?) = ircFormatDeserializer.formatString(
      description, messageSettings.colorizeMirc
    )

    combineLatest(viewModel.bufferDataThrottled, viewModel.lag).toLiveData()
      .observe(this, Observer {
        if (it != null) {
          val (data, lag) = it
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
      val bufferData = viewModel.bufferData.value
      bufferData?.info?.let { info ->
        when (info.type.toInt()) {
          BufferInfo.Type.QueryBuffer.toInt()   -> {
            UserInfoActivity.launch(
              requireContext(),
              bufferId = info.bufferId,
              openBuffer = true
            )
          }
          BufferInfo.Type.ChannelBuffer.toInt() -> {
            ChannelInfoActivity.launch(
              requireContext(),
              bufferId = info.bufferId,
              openBuffer = true
            )
          }
          else                                  -> null
        }
      }
    }

    return view
  }
}
