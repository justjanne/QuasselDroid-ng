/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
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
import de.kuschku.quasseldroid.util.helper.styledAttributes
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

    val mircColors = requireContext().theme.styledAttributes(
      R.attr.mircColor00, R.attr.mircColor01, R.attr.mircColor02, R.attr.mircColor03,
      R.attr.mircColor04, R.attr.mircColor05, R.attr.mircColor06, R.attr.mircColor07,
      R.attr.mircColor08, R.attr.mircColor09, R.attr.mircColor10, R.attr.mircColor11,
      R.attr.mircColor12, R.attr.mircColor13, R.attr.mircColor14, R.attr.mircColor15,
      R.attr.mircColor16, R.attr.mircColor17, R.attr.mircColor18, R.attr.mircColor19,
      R.attr.mircColor20, R.attr.mircColor21, R.attr.mircColor22, R.attr.mircColor23,
      R.attr.mircColor24, R.attr.mircColor25, R.attr.mircColor26, R.attr.mircColor27,
      R.attr.mircColor28, R.attr.mircColor29, R.attr.mircColor30, R.attr.mircColor31,
      R.attr.mircColor32, R.attr.mircColor33, R.attr.mircColor34, R.attr.mircColor35,
      R.attr.mircColor36, R.attr.mircColor37, R.attr.mircColor38, R.attr.mircColor39,
      R.attr.mircColor40, R.attr.mircColor41, R.attr.mircColor42, R.attr.mircColor43,
      R.attr.mircColor44, R.attr.mircColor45, R.attr.mircColor46, R.attr.mircColor47,
      R.attr.mircColor48, R.attr.mircColor49, R.attr.mircColor50, R.attr.mircColor51,
      R.attr.mircColor52, R.attr.mircColor53, R.attr.mircColor54, R.attr.mircColor55,
      R.attr.mircColor56, R.attr.mircColor57, R.attr.mircColor58, R.attr.mircColor59,
      R.attr.mircColor60, R.attr.mircColor61, R.attr.mircColor62, R.attr.mircColor63,
      R.attr.mircColor64, R.attr.mircColor65, R.attr.mircColor66, R.attr.mircColor67,
      R.attr.mircColor68, R.attr.mircColor69, R.attr.mircColor70, R.attr.mircColor71,
      R.attr.mircColor72, R.attr.mircColor73, R.attr.mircColor74, R.attr.mircColor75,
      R.attr.mircColor76, R.attr.mircColor77, R.attr.mircColor78, R.attr.mircColor79,
      R.attr.mircColor80, R.attr.mircColor81, R.attr.mircColor82, R.attr.mircColor83,
      R.attr.mircColor84, R.attr.mircColor85, R.attr.mircColor86, R.attr.mircColor87,
      R.attr.mircColor88, R.attr.mircColor89, R.attr.mircColor90, R.attr.mircColor91,
      R.attr.mircColor92, R.attr.mircColor93, R.attr.mircColor94, R.attr.mircColor95,
      R.attr.mircColor96, R.attr.mircColor97, R.attr.mircColor98
    ) {
      IntArray(99) {
        getColor(it, 0)
      }
    }

    fun colorizeDescription(description: String?) = ircFormatDeserializer.formatString(
      mircColors, description, messageSettings.colorizeMirc
    )

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
          BufferInfo.Type.QueryBuffer.toInt()   -> {
            UserInfoActivity.launch(requireContext(), bufferId = info.bufferId, openBuffer = true)
          }
          BufferInfo.Type.ChannelBuffer.toInt() -> {
            ChannelInfoActivity.launch(requireContext(),
                                       bufferId = info.bufferId,
                                       openBuffer = true)
          }
          else                                  -> null
        }
      }
    }

    return view
  }
}
