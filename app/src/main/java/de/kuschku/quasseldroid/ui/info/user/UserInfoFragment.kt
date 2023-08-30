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

package de.kuschku.quasseldroid.ui.info.user

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helper.*
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.coresettings.ignorelist.IgnoreListActivity
import de.kuschku.quasseldroid.util.ColorContext
import de.kuschku.quasseldroid.util.ShortcutCreationHelper
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.avatars.MatrixApi
import de.kuschku.quasseldroid.util.avatars.MatrixAvatarInfo
import de.kuschku.quasseldroid.util.avatars.MatrixAvatarResponse
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.irc.format.spans.IrcItalicSpan
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.BetterLinkMovementMethod
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import de.kuschku.quasseldroid.viewmodel.data.Avatar
import de.kuschku.quasseldroid.viewmodel.data.BufferProps
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class UserInfoFragment : ServiceBoundFragment() {
  lateinit var avatar: ImageView
  lateinit var nick: TextView
  lateinit var realName: TextView
  lateinit var actionQuery: Button
  lateinit var actionIgnore: Button
  lateinit var actionWhois: Button
  lateinit var actionMention: Button
  lateinit var actionShortcut: Button
  lateinit var awayContainer: ViewGroup
  lateinit var awayMessage: TextView
  lateinit var accountContainer: ViewGroup
  lateinit var account: TextView
  lateinit var identContainer: ViewGroup
  lateinit var ident: TextView
  lateinit var hostContainer: ViewGroup
  lateinit var host: TextView
  lateinit var serverContainer: ViewGroup
  lateinit var server: TextView
  lateinit var commonChannels: RecyclerView

  @Inject
  lateinit var contentFormatter: ContentFormatter

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var matrixApi: MatrixApi

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  private var actualUrl: String? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.info_user, container, false)
    this.avatar = view.findViewById(R.id.avatar)
    this.nick = view.findViewById(R.id.nick)
    this.realName = view.findViewById(R.id.real_name)
    this.actionQuery = view.findViewById(R.id.action_query)
    this.actionIgnore = view.findViewById(R.id.action_ignore)
    this.actionWhois = view.findViewById(R.id.action_whois)
    this.actionMention = view.findViewById(R.id.action_mention)
    this.actionShortcut = view.findViewById(R.id.action_shortcut)
    this.awayContainer = view.findViewById(R.id.away_container)
    this.awayMessage = view.findViewById(R.id.away_message)
    this.accountContainer = view.findViewById(R.id.account_container)
    this.account = view.findViewById(R.id.account)
    this.identContainer = view.findViewById(R.id.ident_container)
    this.ident = view.findViewById(R.id.ident)
    this.hostContainer = view.findViewById(R.id.host_container)
    this.host = view.findViewById(R.id.host)
    this.serverContainer = view.findViewById(R.id.server_container)
    this.server = view.findViewById(R.id.server)
    this.commonChannels = view.findViewById(R.id.common_channels)

    val openBuffer = arguments?.getBoolean("openBuffer")

    val bufferId = BufferId(arguments?.getInt("bufferId") ?: -1)
    val networkId = NetworkId(arguments?.getInt("networkId") ?: -1)
    val hostMask = arguments?.getString("hostmask") ?: ""
    val fallbackRealname = arguments?.getString("realname") ?: ""

    var currentBufferInfo: BufferInfo? = null
    var currentIrcUser: IrcUser?

    val commonChannelsAdapter = ChannelAdapter()
    commonChannels.layoutManager = LinearLayoutManager(context)
    commonChannels.itemAnimator = DefaultItemAnimator()
    commonChannels.adapter = commonChannelsAdapter

    val colorContext = ColorContext(requireContext(), messageSettings)

    val colorAccent = requireContext().theme.styledAttributes(androidx.appcompat.R.attr.colorAccent) {
      getColor(0, 0)
    }

    val colorAway = requireContext().theme.styledAttributes(R.attr.colorAway) {
      getColor(0, 0)
    }

    combineLatest(modelHelper.connectedSession, modelHelper.networks).safeSwitchMap { (sessionOptional, networks) ->
      val session = sessionOptional?.orNull()
      val ignoreListManager = session?.ignoreListManager
      val bufferSyncer = session?.bufferSyncer

      val bufferInfo: Observable<Optional<BufferInfo>> = bufferSyncer?.liveBufferInfo(bufferId)
        ?: Observable.just(Optional.empty())

      val nick: Observable<String> = if (openBuffer == true) {
        bufferInfo.map { (it.orNull()?.bufferName ?: "").ifEmpty { HostmaskHelper.nick(hostMask) } }
      } else Observable.just(HostmaskHelper.nick(hostMask))

      val user: Observable<IrcUser> = nick.safeSwitchMap {
        networks[networkId]?.liveIrcUser(it)
          ?.safeSwitchMap(IrcUser::updates)
          ?: Observable.just(IrcUser.NULL)
      }

      val ignoreRules = user.safeSwitchMap {
        ignoreListManager?.liveMatchingRules(it.hostMask().ifEmpty { hostMask })
          ?: Observable.just(emptyList())
      }

      val userMeta = combineLatest(nick, user).map { (nick, user) ->
        if (user == IrcUser.NULL) {
          IrcUserMeta(
            networkId = networkId,
            nick = nick,
            knownToCore = false,
            user = HostmaskHelper.user(hostMask),
            host = HostmaskHelper.host(hostMask),
            realName = fallbackRealname,
          )
        } else {
          IrcUserMeta(
            networkId = user.network().networkId(),
            nick = user.nick(),
            knownToCore = true,
            user = user.user(),
            host = user.host(),
            account = user.account(),
            server = user.server(),
            realName = user.realName(),
            isAway = user.isAway(),
            awayMessage = user.awayMessage(),
            ircUser = user,
          )
        }
      }

      combineLatest(bufferInfo, userMeta, ignoreRules).safeSwitchMap { (bufferInfo, meta, ignoreItems) ->
        val channels = if (meta.ircUser == null) {
          Observable.just(emptyList())
        } else {
          combineLatest(meta.ircUser.channels().map { channelName ->
            meta.ircUser.network().liveIrcChannel(channelName).safeSwitchMap { channel ->
              channel.updates().map {
                Optional.ofNullable(
                  bufferSyncer?.find(
                    bufferName = channelName,
                    networkId = meta.ircUser.network().networkId()
                  )?.let { info ->
                    val bufferStatus =
                      if (it == IrcChannel.NULL) BufferStatus.OFFLINE
                      else BufferStatus.ONLINE
                    val color =
                      if (bufferStatus == BufferStatus.ONLINE) colorAccent
                      else colorAway
                    val fallbackDrawable = colorContext.buildTextDrawable("#", color)

                    BufferProps(
                      info = info,
                      network = meta.ircUser.network().networkInfo(),
                      description = it.topic(),
                      activity = Message_Type.of(),
                      bufferStatus = bufferStatus,
                      networkConnectionState = meta.ircUser.network().connectionState(),
                      fallbackDrawable = fallbackDrawable
                    )
                  }
                )
              }
            }
          }).map { it.mapNotNull(Optional<BufferProps>::orNull) }
        }

        channels.map {
          IrcUserLiveMeta(
            meta = meta,
            network = meta.ircUser?.network(),
            info = bufferInfo.orNull(),
            channels = it.sortedBy { IrcCaseMappers.unicode.toLowerCaseNullable(it.info.bufferName) },
            ignoreListItems = ignoreItems.orEmpty()
          )
        }
      }
    }.toLiveData().observe(viewLifecycleOwner, Observer {
      val live = it
      val user = it.meta

      currentBufferInfo = live.info
      currentIrcUser = user.ircUser
      actionShortcut.visibleIf(currentBufferInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
      avatar.post {
        avatar.visibility = View.GONE
        actualUrl = null
        avatar.loadAvatars(
          AvatarHelper.avatar(messageSettings, user, maxOf(avatar.width, avatar.height)),
          crop = false
        ) { model ->
          avatar.visibility = View.VISIBLE
          when (model) {
            is String -> {
              actualUrl = model
            }
            is Avatar.MatrixAvatar -> {
              runInBackground {
                matrixApi.avatarUrl(model.userId).execute().body()
                  ?.let<MatrixAvatarResponse, Unit> {
                    it.avatarUrl?.let {
                      val avatarInfo = MatrixAvatarInfo(it, model.size)
                      val url = Uri.parse(avatarInfo.avatarUrl)

                      val imageUrl = matrixApi.avatarImage(
                        server = url.host ?: "",
                        id = url.pathSegments.first()
                      ).request().url()
                      actualUrl = imageUrl.toString()
                    }
                  }
              }
            }
          }
        }
      }
      nick.text = ircFormatDeserializer.formatString(user.nick, messageSettings.colorizeMirc)
      val (content, _) = contentFormatter.formatContent(
        user.realName ?: "",
        networkId = user.networkId
      )
      realName.text = content
      realName.visibleIf(!user.realName.isNullOrBlank() && user.realName != user.nick)
      awayMessage.text = user.awayMessage.nullIf<String?> { it.isNullOrBlank() } ?: SpannableString(
        getString(
          R.string.label_no_away_message
        )
      ).apply {
        setSpan(IrcItalicSpan(), 0, length, 0)
      }
      awayContainer.visibleIf(user.isAway == true)
      account.text = user.account
      accountContainer.visibleIf(!user.account.isNullOrBlank())
      val (userIdent, _) = contentFormatter.formatContent(
        user.user ?: "",
        networkId = user.networkId
      )
      ident.text = userIdent
      identContainer.visibleIf(userIdent.isNotBlank())
      val (userHost, _) = contentFormatter.formatContent(
        user.host ?: "",
        networkId = user.networkId
      )
      host.text = userHost
      hostContainer.visibleIf(userHost.isNotBlank())
      server.text = user.server
      serverContainer.visibleIf(!user.server.isNullOrBlank())
      actionWhois.visibleIf(user.knownToCore)
      actionQuery.setOnClickListener { view ->
        modelHelper.connectedSession.value?.orNull()?.let<ISession, Unit> { session ->
          val info = session.bufferSyncer.find(
            bufferName = user.nick,
            networkId = user.networkId,
            type = Buffer_Type.of(Buffer_Type.QueryBuffer)
          )

          if (info != null) {
            ChatActivity.launch(view.context, bufferId = info.bufferId)
          } else {
            modelHelper.allBuffers.map {
              listOfNotNull(it.find {
                it.networkId == user.networkId && it.bufferName == user.nick
              })
            }.filter {
              it.isNotEmpty()
            }.firstElement().toLiveData().observe(viewLifecycleOwner, Observer {
              it?.firstOrNull()?.let { info ->
                ChatActivity.launch(view.context, bufferId = info.bufferId)
              }
            })

            session.bufferSyncer.find(
              networkId = user.networkId,
              type = Buffer_Type.of(Buffer_Type.StatusBuffer)
            )?.let { statusInfo ->
              session.rpcHandler.sendInput(statusInfo, "/query ${user.nick}")
            }
          }
        }
      }
      var ignoreMenu: PopupMenu? = null
      actionIgnore.setOnClickListener { view ->
        PopupMenu(actionIgnore.context, actionIgnore).also<PopupMenu> { menu ->
          ignoreMenu?.dismiss()
          menu.menuInflater.inflate(R.menu.context_ignore, menu.menu)
          for (ignoreItem in live.ignoreListItems) {
            menu.menu.add(ignoreItem.ignoreRule).apply<MenuItem> {
              this.isCheckable = true
              this.isChecked = ignoreItem.isActive
            }
          }
          menu.setOnMenuItemClickListener {
            when {
              it.itemId == R.id.action_create -> {
                IgnoreListActivity.launch(
                  view.context,
                  addRule = HostmaskHelper.build(user.nick, user.user, user.host)
                )
                menu.dismiss()
                ignoreMenu = null
                true
              }
              it.itemId == R.id.action_show -> {
                IgnoreListActivity.launch(
                  view.context
                )
                menu.dismiss()
                ignoreMenu = null
                true
              }
              it.isCheckable -> {
                modelHelper.ignoreListManager.value?.orNull()
                  ?.requestToggleIgnoreRule(it.title.toString())
                true
              }
              else -> false
            }
          }
          menu.setOnDismissListener {
            ignoreMenu = null
          }
          menu.show()
        }
      }
      actionMention.setOnClickListener { view ->
        ChatActivity.launch(view.context, sharedText = "${user.nick}: ")
      }
      actionWhois.setOnClickListener { view ->
        modelHelper.connectedSession {
          it.orNull()?.let { session ->
            session.bufferSyncer.find(
              networkId = user.networkId,
              type = Buffer_Type.of(Buffer_Type.StatusBuffer)
            )?.let { statusInfo ->
              session.rpcHandler.sendInput(statusInfo, "/whois ${user.nick} ${user.nick}")
            }
          }
        }
      }
      actionShortcut.setOnClickListener {
        context?.let<Context, Unit> { context ->
          currentBufferInfo?.let { info ->
            ShortcutCreationHelper.create(
              context = context,
              messageSettings = messageSettings,
              accountId = accountId,
              info = info,
              ircUser = currentIrcUser
            )
          }
        }
      }
      commonChannelsAdapter.submitList(live.channels)
    })

    avatar.setOnClickListener {
      actualUrl?.let {
        context?.startActivity(Intent(Intent.ACTION_VIEW).apply {
          data = Uri.parse(it)
        })
      }
    }

    actionMention.visibleIf(arguments?.getBoolean("openBuffer") == false)

    val movementMethod = BetterLinkMovementMethod.newInstance()
    movementMethod.setOnLinkLongClickListener(LinkLongClickMenuHelper())
    realName.movementMethod = movementMethod

    actionQuery.setTooltip()
    actionQuery.setCompoundDrawablesWithIntrinsicBounds(
      null,
      requireContext().getVectorDrawableCompat(R.drawable.ic_message_bulleted),
      null,
      null
    )
    actionQuery.retint()

    actionIgnore.setTooltip()
    actionIgnore.setCompoundDrawablesWithIntrinsicBounds(
      null,
      requireContext().getVectorDrawableCompat(R.drawable.ic_eye_off),
      null,
      null
    )
    actionIgnore.retint()

    actionWhois.setTooltip()
    actionWhois.setCompoundDrawablesWithIntrinsicBounds(
      null,
      requireContext().getVectorDrawableCompat(R.drawable.ic_info),
      null,
      null
    )
    actionWhois.retint()

    actionMention.setTooltip()
    actionMention.setCompoundDrawablesWithIntrinsicBounds(
      null,
      requireContext().getVectorDrawableCompat(R.drawable.ic_share_alternative),
      null,
      null
    )
    actionMention.retint()

    actionShortcut.setTooltip()
    actionShortcut.setCompoundDrawablesWithIntrinsicBounds(
      null,
      requireContext().getVectorDrawableCompat(R.drawable.ic_link),
      null,
      null
    )
    actionShortcut.retint()

    return view
  }
}
