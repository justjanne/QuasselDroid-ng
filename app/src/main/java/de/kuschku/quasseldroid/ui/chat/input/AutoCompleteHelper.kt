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

  val mircColors = activity.theme.styledAttributes(
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
              mircColors, it.realname.toString(), messageSettings.colorizeMirc
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
