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
import de.kuschku.quasseldroid.util.AvatarHelper
import de.kuschku.quasseldroid.util.helper.loadWithFallbacks
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.TextDrawable
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

    val sizeProvider = FixedPreloadSizeProvider<List<String>>(avatarSize, avatarSize)

    val preloadModelProvider = object : ListPreloader.PreloadModelProvider<List<String>> {
      override fun getPreloadItems(position: Int) = listOfNotNull(
        nickListAdapter[position]?.let { AvatarHelper.avatar(messageSettings, it) }
      )

      override fun getPreloadRequestBuilder(item: List<String>) =
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
