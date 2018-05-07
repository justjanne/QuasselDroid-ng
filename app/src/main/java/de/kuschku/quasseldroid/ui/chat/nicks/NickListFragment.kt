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

package de.kuschku.quasseldroid.ui.chat.nicks

import android.arch.lifecycle.Observer
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.FixedPreloadSizeProvider
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.IrcUserUtils
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.info.user.UserInfoActivity
import de.kuschku.quasseldroid.ui.chat.input.AutoCompleteHelper.Companion.IGNORED_CHARS
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.helper.loadWithFallbacks
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.TextDrawable
import de.kuschku.quasseldroid.viewmodel.data.Avatar
import javax.inject.Inject

class NickListFragment : ServiceBoundFragment() {
  @BindView(R.id.nickList)
  lateinit var nickList: RecyclerView

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_nick_list, container, false)
    ButterKnife.bind(this, view)

    val nickListAdapter = NickListAdapter(messageSettings, clickListener)
    nickList.adapter = nickListAdapter
    nickList.layoutManager = object : LinearLayoutManager(context) {
      override fun supportsPredictiveItemAnimations() = false
    }
    nickList.itemAnimator = DefaultItemAnimator()

    val senderColors = requireContext().theme.styledAttributes(
      R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
      R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
      R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
      R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
    ) {
      IntArray(length()) {
        getColor(it, 0)
      }
    }

    val selfColor = requireContext().theme.styledAttributes(R.attr.colorForegroundSecondary) {
      getColor(0, 0)
    }

    val avatarSize = resources.getDimensionPixelSize(R.dimen.avatar_size)
    viewModel.nickData.toLiveData().observe(this, Observer {
      runInBackground {
        it?.asSequence()?.map {
          val nickName = it.nick
          val senderColorIndex = IrcUserUtils.senderColor(nickName)
          val rawInitial = nickName.trimStart('-',
                                              '_',
                                              '[',
                                              ']',
                                              '{',
                                              '}',
                                              '|',
                                              '`',
                                              '^',
                                              '.',
                                              '\\')
                             .firstOrNull() ?: nickName.firstOrNull()
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
            ),
            avatarUrls = AvatarHelper.avatar(messageSettings, it, avatarSize)
          )
        }?.sortedBy {
          IrcCaseMappers[it.networkCasemapping].toLowerCase(it.nick.trimStart(*IGNORED_CHARS))
            .trimStart(*IGNORED_CHARS)
        }?.sortedBy {
          it.lowestMode
        }?.toList()?.let {
          activity?.runOnUiThread {
            nickListAdapter.submitList(it)
          }
        }
      }
    })
    savedInstanceState?.run {
      nickList.layoutManager.onRestoreInstanceState(getParcelable(KEY_STATE_LIST))
    }

    val sizeProvider = FixedPreloadSizeProvider<List<Avatar>>(avatarSize, avatarSize)

    val preloadModelProvider = object : ListPreloader.PreloadModelProvider<List<Avatar>> {
      override fun getPreloadItems(position: Int) = listOfNotNull(
        nickListAdapter[position]?.let { AvatarHelper.avatar(messageSettings, it) }
      )

      override fun getPreloadRequestBuilder(item: List<Avatar>) =
        GlideApp.with(this@NickListFragment).loadWithFallbacks(item)?.override(avatarSize)
    }

    val preloader = RecyclerViewPreloader(Glide.with(this), preloadModelProvider, sizeProvider, 10)

    nickList.addOnScrollListener(preloader)

    return view
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(KEY_STATE_LIST, nickList.layoutManager.onSaveInstanceState())
  }

  private val clickListener: ((String) -> Unit)? = { nick ->
    viewModel.session.value?.orNull()?.bufferSyncer?.let { bufferSyncer ->
      viewModel.bufferData.value?.info?.let(BufferInfo::networkId)?.let { networkId ->
        UserInfoActivity.launch(
          requireContext(),
          openBuffer = false,
          bufferId = bufferSyncer.find(
            bufferName = nick,
            networkId = networkId,
            type = Buffer_Type.of(Buffer_Type.QueryBuffer)
          )?.let(BufferInfo::bufferId),
          nick = nick,
          networkId = networkId
        )
      }
    }
  }

  companion object {
    private const val KEY_STATE_LIST = "KEY_STATE_LIST"
  }
}
