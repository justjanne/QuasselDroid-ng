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
import de.kuschku.libquassel.util.IrcUserUtils
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.ui.TextDrawable
import de.kuschku.quasseldroid.viewmodel.EditorViewModel
import de.kuschku.quasseldroid.viewmodel.EditorViewModel.Companion.IGNORED_CHARS
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem

class AutoCompleteHelper(
  activity: FragmentActivity,
  private val autoCompleteSettings: AutoCompleteSettings,
  private val messageSettings: MessageSettings,
  private val ircFormatDeserializer: IrcFormatDeserializer,
  private val viewModel: EditorViewModel
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

  init {
    viewModel.autoCompleteData.toLiveData().observe(activity, Observer {
      val query = it?.first ?: ""
      val shouldShowResults =
        (autoCompleteSettings.auto && query.length >= 3) ||
        (autoCompleteSettings.prefix && autoCompleteSettings.nicks && query.startsWith('@')) ||
        (autoCompleteSettings.prefix && autoCompleteSettings.buffers && query.startsWith('#')) ||
        (autoCompleteSettings.prefix && autoCompleteSettings.aliases && query.startsWith('/'))
      val list = if (shouldShowResults) it?.second.orEmpty() else emptyList()
      val data = list.filter {
        it is AutoCompleteItem.AliasItem && autoCompleteSettings.aliases ||
        it is AutoCompleteItem.UserItem && autoCompleteSettings.nicks ||
        it is AutoCompleteItem.ChannelItem && autoCompleteSettings.buffers
      }.map {
        if (it is AutoCompleteItem.UserItem) {
          val nickName = it.nick
          val senderColorIndex = IrcUserUtils.senderColor(nickName)
          val rawInitial = nickName.trimStart(*IGNORED_CHARS).firstOrNull()
                           ?: nickName.firstOrNull()
          val initial = rawInitial?.toUpperCase().toString()
          val senderColor = when (messageSettings.colorizeNicknames) {
            MessageSettings.ColorizeNicknamesMode.ALL          -> senderColors[senderColorIndex]
            MessageSettings.ColorizeNicknamesMode.ALL_BUT_MINE ->
              if (it.self) selfColor
              else senderColors[senderColorIndex]
            MessageSettings.ColorizeNicknamesMode.NONE         -> selfColor
          }

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
      }
      dataListeners.forEach {
        it(data)
      }
    })
  }

  fun setAutocompleteListener(listener: ((AutoCompletionState) -> Unit)?) {
    this.autocompleteListener = listener
  }

  fun addDataListener(listener: ((List<AutoCompleteItem>) -> Unit)) {
    this.dataListeners += listener
  }

  fun removeDataListener(listener: ((List<AutoCompleteItem>) -> Unit)) {
    this.dataListeners -= listener
  }

  fun autoComplete(reverse: Boolean = false) {
    viewModel.lastWord.switchMap { it }.value?.let { originalWord ->
      val previous = autoCompletionState
      if (!originalWord.second.isEmpty()) {
        val autoCompletedWords = viewModel.autoCompleteData.value?.second?.filter {
          it is AutoCompleteItem.AliasItem && autoCompleteSettings.aliases ||
          it is AutoCompleteItem.UserItem && autoCompleteSettings.nicks ||
          it is AutoCompleteItem.ChannelItem && autoCompleteSettings.buffers
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
