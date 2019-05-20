/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.quasseldroid.util.ui.presenter

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.flag.minus
import de.kuschku.quasseldroid.persistence.models.Filtered
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.ColorContext
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.viewmodel.data.BufferListItem
import de.kuschku.quasseldroid.viewmodel.data.BufferProps
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import javax.inject.Inject

class BufferPresenter @Inject constructor(
  val appearanceSettings: AppearanceSettings,
  val messageSettings: MessageSettings,
  val ircFormatDeserializer: IrcFormatDeserializer,
  val colorContext: ColorContext
) {
  fun render(props: BufferProps,
             activities: Map<BufferId, UInt>,
             defaultFiltered: UInt
  ): BufferProps {
    val activity = props.activity - (activities[props.info.bufferId]
                                     ?: defaultFiltered
                                     ?: 0u)

    return props.copy(
      activity = activity,
      description = ircFormatDeserializer.formatString(
        props.description.toString(),
        colorize = messageSettings.colorizeMirc
      ),
      bufferActivity = Buffer_Activity.of(
        when {
          props.highlights > 0                  -> Buffer_Activity.Highlight
          activity.hasFlag(Message_Type.Plain) ||
          activity.hasFlag(Message_Type.Notice) ||
          activity.hasFlag(Message_Type.Action) -> Buffer_Activity.NewMessage
          activity.isNotEmpty()                 -> Buffer_Activity.OtherActivity
          else                                  -> Buffer_Activity.NoActivity
        }
      ),
      fallbackDrawable = if (props.info.type.hasFlag(Buffer_Type.QueryBuffer)) {
        props.ircUser?.let {
          val nickName = it.nick()
          val useSelfColor = when (messageSettings.colorizeNicknames) {
            MessageSettings.ColorizeNicknamesMode.ALL          -> false
            MessageSettings.ColorizeNicknamesMode.ALL_BUT_MINE ->
              props.ircUser?.network()?.isMyNick(nickName) == true
            MessageSettings.ColorizeNicknamesMode.NONE         -> true
          }

          colorContext.buildTextDrawable(it.nick(), useSelfColor)
        } ?: colorContext.buildTextDrawable("", colorContext.colorAway)
      } else {
        val color = if (props.bufferStatus == BufferStatus.ONLINE) colorContext.colorAccent
        else colorContext.colorAway

        colorContext.buildTextDrawable("#", color)
      },
      avatarUrls = props.ircUser?.let {
        AvatarHelper.avatar(messageSettings, it, colorContext.avatarSize)
      } ?: emptyList()
    )
  }

  fun render(buffers: List<BufferListItem>, filteredList: List<Filtered>, defaultFiltered: UInt) =
    buffers.map {
      it.copy(props = render(
        it.props,
        filteredList.associate { it.bufferId to it.filtered.toUInt() },
        defaultFiltered
      ))
    }
}
