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

package de.kuschku.quasseldroid.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide

import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.info.channel.ChannelInfoActivity
import de.kuschku.quasseldroid.ui.info.user.UserInfoActivity
import de.kuschku.quasseldroid.util.ColorContext
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.helper.loadAvatars
import de.kuschku.quasseldroid.util.helper.setTooltip
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import javax.inject.Inject

class ToolbarFragment : ServiceBoundFragment() {
  lateinit var toolbarTitle: TextView

  lateinit var toolbarSubtitle: TextView

  lateinit var icon: AppCompatImageView

  lateinit var actionArea: View

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

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
    val view = inflater.inflate(R.layout.chat_toolbar, container, false)
    this.toolbarTitle = view.findViewById(R.id.toolbar_title)
    this.toolbarSubtitle = view.findViewById(R.id.toolbar_subtitle)
    this.icon = view.findViewById(R.id.toolbar_icon)
    this.actionArea = view.findViewById(R.id.toolbar_action_area)

    fun colorizeDescription(description: String?) = ircFormatDeserializer.formatString(
      description, messageSettings.colorizeMirc
    )

    val avatarSize = resources.getDimensionPixelSize(R.dimen.avatar_size_buffer)

    val colorContext = ColorContext(requireContext(), messageSettings)

    combineLatest(modelHelper.bufferDataThrottled, modelHelper.lag).map {
      val avatarInfo = it.first?.ircUser?.let { user ->
        val avatarUrls = AvatarHelper.avatar(messageSettings, user, avatarSize)

        val nickName = user.nick()
        val useSelfColor = when (messageSettings.colorizeNicknames) {
          MessageSettings.SenderColorMode.ALL          -> false
          MessageSettings.SenderColorMode.ALL_BUT_MINE ->
            user.network().isMyNick(nickName)
          MessageSettings.SenderColorMode.NONE         -> true
        }

        val fallbackDrawable = colorContext.buildTextDrawable(user.nick(), useSelfColor)

        Pair(avatarUrls, fallbackDrawable)
      }

      Triple(it.first, it.second, avatarInfo)
    }.toLiveData()
      .observe(viewLifecycleOwner, Observer {
        if (it != null) {
          val (data, lag, avatarInfo) = it

          if (avatarInfo != null) {
            val (avatarUrls, fallbackDrawable) = avatarInfo
            icon.loadAvatars(avatarUrls, fallbackDrawable, crop = !messageSettings.squareAvatars)
            icon.visibility = View.VISIBLE
          } else {
            Glide.with(icon).clear(icon)
            icon.visibility = View.GONE
          }

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
      val bufferData = modelHelper.bufferData.value
      bufferData?.info?.let { info ->
        when (info.type.toInt()) {
          BufferInfo.Type.QueryBuffer.toInt()   -> {
            UserInfoActivity.launch(
              requireContext(),
              bufferId = info.bufferId,
              networkId = info.networkId,
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
          else                                  -> Unit
        }
      }
    }
    actionArea.setTooltip()

    return view
  }
}
