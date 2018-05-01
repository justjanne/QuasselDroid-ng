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
import android.graphics.Typeface
import android.support.v4.app.FragmentActivity
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.util.IrcUserUtils
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.AvatarHelper
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.ui.TextDrawable
import de.kuschku.quasseldroid.viewmodel.EditorViewModel
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus

class AutoCompleteHelper(
  activity: FragmentActivity,
  private val autoCompleteSettings: AutoCompleteSettings,
  private val messageSettings: MessageSettings,
  private val ircFormatDeserializer: IrcFormatDeserializer,
  private val viewModel: EditorViewModel
) {
  private var autocompleteListener: ((AutoCompletionState) -> Unit)? = null
  private var dataListener: ((List<AutoCompleteItem>) -> Unit)? = null

  var autoCompletionState: AutoCompletionState? = null

  private val senderColors = activity.theme.styledAttributes(
    R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
    R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
    R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
    R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
  ) {
    IntArray(length()) { getColor(it, 0) }
  }

  private val avatarSize = activity.resources.getDimensionPixelSize(R.dimen.avatar_size)

  init {
    viewModel.autoCompleteData.toLiveData().observe(activity, Observer {
      val query = it?.first ?: ""
      val shouldShowResults = (autoCompleteSettings.auto && query.length >= 3) ||
                              (autoCompleteSettings.prefix && query.startsWith('@')) ||
                              (autoCompleteSettings.prefix && query.startsWith('#'))
      val list = if (shouldShowResults) it?.second.orEmpty() else emptyList()
      dataListener?.invoke(list.map {
        if (it is AutoCompleteItem.UserItem) {
          val nickName = it.nick
          val senderColorIndex = IrcUserUtils.senderColor(nickName)
          val rawInitial = nickName.trimStart(*IGNORED_CHARS).firstOrNull()
                           ?: nickName.firstOrNull()
          val initial = rawInitial?.toUpperCase().toString()
          val senderColor = senderColors[senderColorIndex]

          fun formatNick(nick: CharSequence): CharSequence {
            val spannableString = SpannableString(nick)
            spannableString.setSpan(
              ForegroundColorSpan(senderColor),
              0,
              nick.length,
              SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
              StyleSpan(Typeface.BOLD),
              0,
              nick.length,
              SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
            )
            return spannableString
          }

          it.copy(
            displayNick = formatNick(it.nick),
            fallbackDrawable = TextDrawable.builder().buildRound(initial, senderColor),
            modes = when (messageSettings.showPrefix) {
              MessageSettings.ShowPrefixMode.ALL ->
                it.modes
              else                               ->
                it.modes.substring(0, Math.min(it.modes.length, 1))
            },
            realname = ircFormatDeserializer.formatString(
              it.realname.toString(), messageSettings.colorizeMirc
            )
          )
        } else {
          it
        }
      })
    })
  }

  fun setAutocompleteListener(listener: ((AutoCompletionState) -> Unit)?) {
    this.autocompleteListener = listener
  }

  fun setDataListener(listener: ((List<AutoCompleteItem>) -> Unit)?) {
    this.dataListener = listener
  }

  private fun autoCompleteDataFull(): List<AutoCompleteItem> {
    return viewModel.rawAutoCompleteData.value?.let { (sessionOptional, id, lastWord) ->
      val session = sessionOptional.orNull()
      val bufferInfo = session?.bufferSyncer?.bufferInfo(id)
      session?.networks?.let { networks ->
        session.bufferSyncer?.bufferInfos()?.let { infos ->
          if (bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
            val network = networks[bufferInfo.networkId]
            network?.ircChannel(
              bufferInfo.bufferName
            )?.let { ircChannel ->
              val users = ircChannel.ircUsers()
              val buffers = infos
                .asSequence()
                .filter {
                  it.type.toInt() == Buffer_Type.ChannelBuffer.toInt()
                }.mapNotNull { info ->
                  networks[info.networkId]?.let { info to it }
                }.map { (info, network) ->
                  val channel = network.ircChannel(info.bufferName) ?: IrcChannel.NULL
                  AutoCompleteItem.ChannelItem(
                    info = info,
                    network = network.networkInfo(),
                    bufferStatus = when (channel) {
                      IrcChannel.NULL -> BufferStatus.OFFLINE
                      else            -> BufferStatus.ONLINE
                    },
                    description = channel.topic()
                  )
                }
              val nicks = users.asSequence().map { user ->
                val userModes = ircChannel.userModes(user)
                val prefixModes = network.prefixModes()

                val lowestMode = userModes.mapNotNull(prefixModes::indexOf).min()
                                 ?: prefixModes.size

                AutoCompleteItem.UserItem(
                  user.nick(),
                  network.modesToPrefixes(userModes),
                  lowestMode,
                  user.realName(),
                  user.isAway(),
                  network.support("CASEMAPPING"),
                  AvatarHelper.avatar(messageSettings, user, avatarSize)
                )
              }

              (nicks + buffers).filter {
                it.name.trimStart(*IGNORED_CHARS)
                  .startsWith(
                    lastWord.first.trimStart(*IGNORED_CHARS),
                    ignoreCase = true
                  )
              }.sorted().toList()
            }
          } else null
        }
      }
    } ?: emptyList()
  }

  fun autoComplete(reverse: Boolean = false) {
    viewModel.lastWord.switchMap { it }.value?.let { originalWord ->
      val previous = autoCompletionState
      if (!originalWord.second.isEmpty()) {
        val autoCompletedWords = autoCompleteDataFull()
        if (previous != null && originalWord.first == previous.originalWord && originalWord.second.start == previous.range.start) {
          val previousIndex = autoCompletedWords.indexOf(previous.completion)
          val autoCompletedWord = if (previousIndex != -1) {
            val change = if (reverse) -1 else +1
            val newIndex = (previousIndex + change + autoCompletedWords.size) % autoCompletedWords.size

            autoCompletedWords[newIndex]
          } else {
            autoCompletedWords.firstOrNull()
          }
          if (autoCompletedWord != null) {
            val newState = AutoCompletionState(
              previous.originalWord,
              originalWord.second,
              previous.completion,
              autoCompletedWord
            )
            autoCompletionState = newState
            autocompleteListener?.invoke(newState)
          } else {
            autoCompletionState = null
          }
        } else {
          val autoCompletedWord = autoCompletedWords.firstOrNull()
          if (autoCompletedWord != null) {
            val newState = AutoCompletionState(
              originalWord.first,
              originalWord.second,
              null,
              autoCompletedWord
            )
            autoCompletionState = newState
            autocompleteListener?.invoke(newState)
          } else {
            autoCompletionState = null
          }
        }
      }
    }
  }

  companion object {
    val IGNORED_CHARS = charArrayOf('-', '_', '[', ']', '{', '}', '|', '`', '^', '.', '\\', '@')
  }
}
