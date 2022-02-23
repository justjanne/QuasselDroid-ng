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

package de.kuschku.quasseldroid.ui.chat.input

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helper.nullIf
import de.kuschku.libquassel.util.helper.safeSwitchMap
import de.kuschku.libquassel.util.helper.value
import de.kuschku.libquassel.util.irc.SenderColorUtil
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.ColorContext
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.emoji.EmojiData
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper.Companion.IGNORED_CHARS

class AutoCompleteHelper(
  activity: FragmentActivity,
  private val autoCompleteSettings: AutoCompleteSettings,
  private val messageSettings: MessageSettings,
  private val ircFormatDeserializer: IrcFormatDeserializer,
  private val contentFormatter: ContentFormatter,
  private val helper: EditorViewModelHelper
) {
  private var autocompleteListener: ((AutoCompletionState) -> Unit)? = null
  private var dataListeners: List<((List<AutoCompleteItem>) -> Unit)> = emptyList()

  var autoCompletionState: AutoCompletionState? = null

  private val senderColors = activity.theme.styledAttributes(
    R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
    R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
    R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
    R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
  ) {
    IntArray(length()) { getColor(it, 0) }
  }

  private val selfColor = activity.theme.styledAttributes(R.attr.colorForegroundSecondary) {
    getColor(0, 0)
  }

  private val colorAccent = activity.theme.styledAttributes(R.attr.colorAccent) {
    getColor(0, 0)
  }

  private val colorAway = activity.theme.styledAttributes(R.attr.colorAway) {
    getColor(0, 0)
  }

  private val colorContext = ColorContext(activity, messageSettings)

  init {
    helper.autoCompleteData.toLiveData().observe(activity, Observer {
      val query = it?.first ?: ""
      val shouldShowResults =
        (autoCompleteSettings.auto && query.length >= 3) ||
        (autoCompleteSettings.prefix && autoCompleteSettings.nicks && query.startsWith('@')) ||
        (autoCompleteSettings.prefix && autoCompleteSettings.buffers && query.startsWith('#')) ||
        (autoCompleteSettings.prefix && autoCompleteSettings.aliases && query.startsWith('/')) ||
        (autoCompleteSettings.prefix && autoCompleteSettings.emoji && query.startsWith(':'))
      val list = if (shouldShowResults) it?.second.orEmpty() else emptyList()
      val data = list.filter {
        it is AutoCompleteItem.AliasItem && autoCompleteSettings.aliases ||
        it is AutoCompleteItem.UserItem && autoCompleteSettings.nicks ||
        it is AutoCompleteItem.ChannelItem && autoCompleteSettings.buffers ||
        it is AutoCompleteItem.EmojiItem && autoCompleteSettings.emoji
      }.map {
        when (it) {
          is AutoCompleteItem.UserItem    -> {
            val nickName = it.nick
            val senderColorIndex = SenderColorUtil.senderColor(nickName)
            val rawInitial = nickName.trimStart(*IGNORED_CHARS).firstOrNull()
                             ?: nickName.firstOrNull()
            val initial = rawInitial?.uppercase().toString()
            val useSelfColor = when (messageSettings.colorizeNicknames) {
              MessageSettings.SenderColorMode.ALL          -> false
              MessageSettings.SenderColorMode.ALL_BUT_MINE -> it.self
              MessageSettings.SenderColorMode.NONE         -> true
            }
            val senderColor = if (useSelfColor) selfColor else senderColors[senderColorIndex]
            it.copy(
              displayNick = contentFormatter.formatNick(it.nick),
              fallbackDrawable = colorContext.buildTextDrawable(initial, senderColor),
              modes = when (messageSettings.showPrefix) {
                MessageSettings.ShowPrefixMode.ALL ->
                  it.modes
                else                               ->
                  it.modes.substring(0, Math.min(it.modes.length, 1))
              },
              realname = ircFormatDeserializer.formatString(
                it.realname.toString(), messageSettings.colorizeMirc
              ),
              avatarUrls = AvatarHelper.avatar(messageSettings, it)
            )
          }
          is AutoCompleteItem.ChannelItem -> {
            val color = if (it.bufferStatus == BufferStatus.ONLINE) colorAccent
            else colorAway

            it.copy(
              icon = colorContext.buildTextDrawable("#", color)
            )
          }
          else                            -> it
        }
      }
      for (dataListener in dataListeners) {
        dataListener(data)
      }
    })
  }

  fun setAutocompleteListener(listener: ((AutoCompletionState) -> Unit)?) {
    this.autocompleteListener = listener
  }

  fun addDataListener(listener: ((List<AutoCompleteItem>) -> Unit)) {
    this.dataListeners += listener
  }

  private fun fullAutoComplete(sessionOptional: Optional<ISession>, id: BufferId,
                               lastWord: Pair<String, IntRange>): List<AutoCompleteItem> {
    val session = sessionOptional.orNull()
    val bufferSyncer = session?.bufferSyncer
    val bufferInfo = bufferSyncer?.bufferInfo(id)
    return if (bufferSyncer != null) {
      val networks = session.networks
      val infos = bufferSyncer.bufferInfos()
      val aliases = session.aliasManager.aliasList()
      val network = networks[bufferInfo?.networkId] ?: Network.NULL
      val ircChannel = if (bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
        network.ircChannel(bufferInfo.bufferName) ?: IrcChannel.NULL
      } else IrcChannel.NULL
      val users = ircChannel.ircUsers()
      fun filterStart(name: String): Boolean {
        return name.trimStart(*IGNORED_CHARS)
          .startsWith(
            lastWord.first.trimStart(*IGNORED_CHARS),
            ignoreCase = true
          )
      }

      fun filter(name: String): Boolean {
        return name.trim(*IGNORED_CHARS)
          .contains(
            lastWord.first.trim(*IGNORED_CHARS),
            ignoreCase = true
          )
      }

      fun getAliases() = aliases.filter {
        filterStart(it.name ?: "")
      }.map {
        AutoCompleteItem.AliasItem(
          "/${it.name}",
          it.expansion
        )
      }

      fun getBuffers() = infos.filter {
        filterStart(it.bufferName ?: "")
      }.filter {
        it.type.toInt() == Buffer_Type.ChannelBuffer.toInt()
      }.mapNotNull { info ->
        networks[info.networkId]?.let { info to it }
      }.map { (info, network) ->
        val channel = network.ircChannel(
          info.bufferName
        ).nullIf { it == IrcChannel.NULL }

        AutoCompleteItem.ChannelItem(
          info = info,
          network = network.networkInfo(),
          bufferStatus = when (channel) {
            null -> BufferStatus.OFFLINE
            else -> BufferStatus.ONLINE
          },
          description = channel?.topic() ?: ""
        )
      }

      fun getUsers(): Set<IrcUser> = when {
        bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true ->
          users
        bufferInfo?.type?.hasFlag(Buffer_Type.QueryBuffer) == true   ->
          network.ircUser(bufferInfo.bufferName).nullIf { it == IrcUser.NULL }?.let {
            setOf(it)
          } ?: emptySet()
        else                                                         ->
          emptySet()
      }

      fun getNicks() = getUsers().filter {
        filterStart(it.nick())
      }.map { user ->
        val userModes = ircChannel.userModes(user)
        val prefixModes = network.prefixModes()

        val lowestMode = userModes.mapNotNull(prefixModes::indexOf).minOrNull()
                         ?: prefixModes.size

        AutoCompleteItem.UserItem(
          user.nick(),
          user.hostMask(),
          network.modesToPrefixes(userModes),
          lowestMode,
          user.realName(),
          user.isAway(),
          user.network().isMyNick(user.nick()),
          network.support("CASEMAPPING")
        )
      }

      fun getEmojis() = EmojiData.processedEmojiMap.filter {
        it.shortCodes.any {
          it.contains(lastWord.first.trim(':'))
        }
      }

      when (lastWord.first.firstOrNull()) {
        '/'  -> getAliases()
        '@'  -> getNicks()
        '#'  -> getBuffers()
        ':'  -> getEmojis()
        else -> getNicks()
      }.sorted()
    } else {
      emptyList()
    }
  }

  fun autoComplete(reverse: Boolean = false) {
    helper.editor.lastWord.safeSwitchMap { it }.value?.let { originalWord ->
      val previous = autoCompletionState
      if (!originalWord.second.isEmpty()) {
        val autoCompletedWords = helper.rawAutoCompleteData.value?.let { (sessionOptional, id, lastWord) ->
          fullAutoComplete(sessionOptional, id, lastWord)
        }?.filter {
          it is AutoCompleteItem.AliasItem && autoCompleteSettings.aliases ||
          it is AutoCompleteItem.UserItem && autoCompleteSettings.nicks ||
          it is AutoCompleteItem.ChannelItem && autoCompleteSettings.buffers ||
          it is AutoCompleteItem.EmojiItem && autoCompleteSettings.emoji
        }.orEmpty()

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
}
