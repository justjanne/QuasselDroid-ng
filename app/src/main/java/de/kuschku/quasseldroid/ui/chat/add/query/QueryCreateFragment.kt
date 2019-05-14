/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.quasseldroid.ui.chat.add.query

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.FixedPreloadSizeProvider
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.mapOrElse
import de.kuschku.libquassel.util.helper.mapSwitchMap
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.libquassel.util.irc.SenderColorUtil
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.chat.add.NetworkAdapter
import de.kuschku.quasseldroid.ui.chat.add.NetworkItem
import de.kuschku.quasseldroid.ui.chat.nicks.NickListAdapter
import de.kuschku.quasseldroid.util.ColorContext
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.helper.loadWithFallbacks
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.viewmodel.data.Avatar
import de.kuschku.quasseldroid.viewmodel.data.IrcUserItem
import de.kuschku.quasseldroid.viewmodel.data.MatchMode
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import de.kuschku.quasseldroid.viewmodel.helper.QueryCreateViewModelHelper
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class QueryCreateFragment : ServiceBoundFragment() {
  @BindView(R.id.network)
  lateinit var network: AppCompatSpinner

  @BindView(R.id.name)
  lateinit var name: EditText

  @BindView(R.id.query)
  lateinit var query: Button

  @BindView(R.id.list)
  lateinit var list: RecyclerView

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var modelHelper: QueryCreateViewModelHelper

  private var hasSelectedNetwork = false
  private var networkId = NetworkId(0)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.add_query, container, false)
    ButterKnife.bind(this, view)

    networkId = NetworkId(
      savedInstanceState?.getInt("network_id", 0)
      ?: arguments?.getInt("network_id", 0)
      ?: 0
    )

    val networkAdapter = NetworkAdapter()
    network.adapter = networkAdapter

    network.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {
        networkId = NetworkId(0)
        modelHelper.queryCreate.networkId.onNext(networkId)
      }

      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        networkId = networkAdapter.getItem(position)?.id
                    ?: NetworkId(0)
        hasSelectedNetwork = true
        modelHelper.queryCreate.networkId.onNext(networkId)
      }
    }

    var hasSetNetwork = false
    modelHelper.networks.switchMap {
      combineLatest(it.values.map(Network::liveNetworkInfo)).map {
        it.map {
          NetworkItem(it.networkId, it.networkName)
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, NetworkItem::name))
      }
    }.toLiveData().observe(this, Observer {
      if (it != null) {
        networkAdapter.submitList(it)
        if (!hasSetNetwork && networkId.isValidId() && it.isNotEmpty()) {
          network.post {
            val index = networkAdapter.indexOf(networkId)
            if (index != null) {
              network.setSelection(index)
              hasSelectedNetwork = true
            }
          }
          hasSetNetwork = true
        }
      }
    })

    name.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        modelHelper.queryCreate.nickName.onNext(s?.toString() ?: "")
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    })

    val nickListAdapter = NickListAdapter(messageSettings, clickListener)
    list.adapter = nickListAdapter
    list.layoutManager = object : LinearLayoutManager(context) {
      override fun supportsPredictiveItemAnimations() = false
    }
    list.itemAnimator = DefaultItemAnimator()

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

    val colorContext = ColorContext(requireContext(), messageSettings)

    val avatarSize = resources.getDimensionPixelSize(R.dimen.avatar_size)

    val sizeProvider = FixedPreloadSizeProvider<List<Avatar>>(avatarSize, avatarSize)

    val preloadModelProvider = object : ListPreloader.PreloadModelProvider<List<Avatar>> {
      override fun getPreloadItems(position: Int) = listOfNotNull(
        nickListAdapter[position]?.let { AvatarHelper.avatar(messageSettings, it) }
      )

      override fun getPreloadRequestBuilder(item: List<Avatar>) =
        GlideApp.with(this@QueryCreateFragment).loadWithFallbacks(item)?.override(avatarSize)
    }

    val preloader = RecyclerViewPreloader(Glide.with(this), preloadModelProvider, sizeProvider, 10)

    list.addOnScrollListener(preloader)

    val nickData = combineLatest(modelHelper.networks, modelHelper.queryCreate.networkId)
      .map { (networks, networkId) ->
        Optional.ofNullable(networks[networkId])
      }.mapSwitchMap {
        it.liveIrcUsers()
      }.mapOrElse(emptyList()).switchMap {
        combineLatest<IrcUserItem>(
          it.map<IrcUser, Observable<IrcUserItem>?> {
            it.updates().map { user ->
              IrcUserItem(
                user.network().networkId(),
                user.nick(),
                "",
                0,
                user.realName(),
                user.hostMask(),
                user.isAway(),
                user.network().isMyNick(user.nick()),
                user.network().support("CASEMAPPING")
              )
            }
          }
        )
      }

    val searchedSortedNickData = combineLatest(modelHelper.queryCreate.nickName, nickData)
      .map { (search, users) ->
        users.filter {
          it.nick.contains(search, ignoreCase = true)
        }.map {
          val matchMode = when {
            it.nick.equals(search, ignoreCase = true)     -> MatchMode.EXACT
            it.nick.startsWith(search, ignoreCase = true) -> MatchMode.START
            else                                          -> MatchMode.CONTAINS
          }

          Pair(matchMode, it)
        }.sortedBy { (_, user) ->
          IrcCaseMappers.unicode.toLowerCaseNullable(user.nick)
        }.sortedBy { (matchMode, _) ->
          matchMode.priority
        }.map { (_, user) ->
          user
        }
      }

    val nickDataThrottled = searchedSortedNickData
      .distinctUntilChanged()
      .throttleLast(100, TimeUnit.MILLISECONDS)

    nickDataThrottled.map {
      it.asSequence().map {
        val nickName = it.nick
        val senderColorIndex = SenderColorUtil.senderColor(nickName)
        val rawInitial = nickName.trimStart(*EditorViewModelHelper.IGNORED_CHARS)
                           .firstOrNull() ?: nickName.firstOrNull()
        val initial = rawInitial?.toUpperCase().toString()
        val useSelfColor = when (messageSettings.colorizeNicknames) {
          MessageSettings.ColorizeNicknamesMode.ALL          -> false
          MessageSettings.ColorizeNicknamesMode.ALL_BUT_MINE -> it.self
          MessageSettings.ColorizeNicknamesMode.NONE         -> true
        }
        val senderColor = if (useSelfColor) selfColor else senderColors[senderColorIndex]

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
          fallbackDrawable = colorContext.buildTextDrawable(initial, senderColor),
          initial = initial,
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
      }.sortedBy {
        IrcCaseMappers[it.networkCasemapping].toLowerCase(it.nick.trimStart(*EditorViewModelHelper.IGNORED_CHARS))
          .trimStart(*EditorViewModelHelper.IGNORED_CHARS)
      }.sortedBy {
        it.lowestMode
      }.toList()
    }.toLiveData().observe(this, Observer {
      nickListAdapter.submitList(it)
    })

    query.setOnClickListener {
      val selectedNetworkId = NetworkId(network.selectedItemId.toInt())
      val nickName = name.text.toString().trim()

      clickListener(selectedNetworkId, nickName)
    }

    return view
  }

  private val clickListener: ((NetworkId, String) -> Unit) = { selectedNetworkId, nickName ->
    query.setText(R.string.label_saving)
    query.isEnabled = false

    activity?.let {
      it.finish()
      ChatActivity.launch(
        it,
        networkId = selectedNetworkId,
        nickName = nickName,
        forceJoin = true
      )
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    if (networkId.isValidId() && hasSelectedNetwork) {
      outState.putInt("network_id", networkId.id)
    }
  }
}
