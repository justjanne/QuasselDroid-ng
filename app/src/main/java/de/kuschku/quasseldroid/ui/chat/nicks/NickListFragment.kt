package de.kuschku.quasseldroid.ui.chat.nicks

import android.arch.lifecycle.Observer
import android.content.Intent
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
import de.kuschku.quasseldroid.ui.chat.info.InfoActivity
import de.kuschku.quasseldroid.ui.chat.info.InfoDescriptor
import de.kuschku.quasseldroid.ui.chat.info.InfoType
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
      IntArray(16) {
        getColor(it, 0)
      }
    }

    viewModel.nickData.map {
      it.map {
        val nickName = it.nick
        val senderColorIndex = IrcUserUtils.senderColor(nickName)
        val rawInitial = nickName.trimStart('-', '_', '[', ']', '{', '}', '|', '`', '^', '.', '\\')
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
            requireContext(), it.realname.toString(), messageSettings.colorizeMirc
          )
        )
      }.sortedBy {
        IrcCaseMappers[it.networkCasemapping].toLowerCase(it.nick)
      }.sortedBy {
        it.lowestMode
      }
    }.toLiveData().observe(this, Observer(nickListAdapter::submitList))

    savedInstanceState?.run {
      nickList.layoutManager.onRestoreInstanceState(getParcelable(KEY_STATE_LIST))
    }

    val avatar_size = resources.getDimensionPixelSize(R.dimen.avatar_size)

    val sizeProvider = FixedPreloadSizeProvider<String>(avatar_size, avatar_size)

    val preloadModelProvider = object : ListPreloader.PreloadModelProvider<String> {
      override fun getPreloadItems(position: Int) = nickListAdapter[position]?.avatarUrl?.let {
        mutableListOf(it)
      } ?: mutableListOf()

      override fun getPreloadRequestBuilder(item: String) =
        GlideApp.with(this@NickListFragment).load(item).override(avatar_size)
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
        val intent = Intent(requireContext(), InfoActivity::class.java)
        intent.putExtra("info", InfoDescriptor(
          type = InfoType.User,
          nick = nick,
          buffer = bufferSyncer.find(
            bufferName = nick,
            networkId = networkId,
            type = Buffer_Type.of(Buffer_Type.QueryBuffer)
          )?.bufferId ?: -1,
          network = networkId
        ))
        startActivity(intent)
      }
    }
  }

  companion object {
    private const val KEY_STATE_LIST = "KEY_STATE_LIST"
  }
}
