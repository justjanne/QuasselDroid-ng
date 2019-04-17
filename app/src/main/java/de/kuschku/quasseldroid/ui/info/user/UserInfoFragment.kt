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

package de.kuschku.quasseldroid.ui.info.user

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
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
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.BufferSyncer
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helpers.nullIf
import de.kuschku.libquassel.util.helpers.value
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
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.irc.format.spans.IrcItalicSpan
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.BetterLinkMovementMethod
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import de.kuschku.quasseldroid.viewmodel.data.Avatar
import de.kuschku.quasseldroid.viewmodel.data.BufferHiddenState
import de.kuschku.quasseldroid.viewmodel.data.BufferProps
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import io.reactivex.Observable
import javax.inject.Inject

class UserInfoFragment : ServiceBoundFragment() {
  @BindView(R.id.avatar)
  lateinit var avatar: ImageView

  @BindView(R.id.nick)
  lateinit var nick: TextView

  @BindView(R.id.real_name)
  lateinit var realName: TextView

  @BindView(R.id.action_query)
  lateinit var actionQuery: Button

  @BindView(R.id.action_ignore)
  lateinit var actionIgnore: Button

  @BindView(R.id.action_whois)
  lateinit var actionWhois: Button

  @BindView(R.id.action_mention)
  lateinit var actionMention: Button

  @BindView(R.id.action_shortcut)
  lateinit var actionShortcut: Button

  @BindView(R.id.away_container)
  lateinit var awayContainer: ViewGroup

  @BindView(R.id.away_message)
  lateinit var awayMessage: TextView

  @BindView(R.id.account_container)
  lateinit var accountContainer: ViewGroup

  @BindView(R.id.account)
  lateinit var account: TextView

  @BindView(R.id.ident_container)
  lateinit var identContainer: ViewGroup

  @BindView(R.id.ident)
  lateinit var ident: TextView

  @BindView(R.id.host_container)
  lateinit var hostContainer: ViewGroup

  @BindView(R.id.host)
  lateinit var host: TextView

  @BindView(R.id.server_container)
  lateinit var serverContainer: ViewGroup

  @BindView(R.id.server)
  lateinit var server: TextView

  @BindView(R.id.common_channels)
  lateinit var commonChannels: RecyclerView

  @Inject
  lateinit var contentFormatter: ContentFormatter

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
    ButterKnife.bind(this, view)

    val openBuffer = arguments?.getBoolean("openBuffer")

    val bufferId = BufferId(arguments?.getInt("bufferId") ?: -1)
    val networkId = NetworkId(arguments?.getInt("networkId") ?: -1)
    val nickName = arguments?.getString("nick")

    var currentBufferInfo: BufferInfo? = null
    var currentIrcUser: IrcUser?

    fun updateShortcutVisibility() {
      actionShortcut.visibleIf(currentBufferInfo != null)
    }

    val commonChannelsAdapter = ChannelAdapter()
    commonChannels.layoutManager = LinearLayoutManager(context)
    commonChannels.itemAnimator = DefaultItemAnimator()
    commonChannels.adapter = commonChannelsAdapter

    val colorContext = ColorContext(requireContext(), messageSettings)

    val colorAccent = requireContext().theme.styledAttributes(R.attr.colorAccent) {
      getColor(0, 0)
    }

    val colorAway = requireContext().theme.styledAttributes(R.attr.colorAway) {
      getColor(0, 0)
    }

    combineLatest(modelHelper.session, modelHelper.networks).switchMap { (sessionOptional, networks) ->
      fun processUser(user: IrcUser, bufferSyncer: BufferSyncer? = null, info: BufferInfo? = null,
                      ignoreItems: List<IgnoreListManager.IgnoreListItem>? = null): Observable<Optional<IrcUserInfo>> {
        actionShortcut.post(::updateShortcutVisibility)
        return when {
          user == IrcUser.NULL && info != null -> Observable.just(Optional.of(IrcUserInfo(
            networkId = info.networkId,
            nick = info.bufferName ?: "",
            knownToCore = true,
            info = info
          )))
          user == IrcUser.NULL                 -> Observable.just(Optional.empty())
          else                                 -> {
            fun buildUserInfo(channels: List<BufferProps>) = IrcUserInfo(
              networkId = user.network().networkId(),
              nick = user.nick(),
              user = user.user(),
              host = user.host(),
              account = user.account(),
              server = user.server(),
              realName = user.realName(),
              isAway = user.isAway(),
              awayMessage = user.awayMessage(),
              network = user.network(),
              knownToCore = true,
              info = info,
              ircUser = user,
              channels = channels.sortedBy {
                IrcCaseMappers.unicode.toLowerCaseNullable(it.info.bufferName)
              },
              ignoreListItems = ignoreItems.orEmpty()
            )

            if (user.channels().isEmpty()) {
              Observable.just(Optional.of(
                buildUserInfo(emptyList())
              ))
            } else {
              combineLatest(user.channels().map { channelName ->
                user.network().liveIrcChannel(
                  channelName
                ).switchMap { channel ->
                  channel.updates().map {
                    Optional.ofNullable(
                      bufferSyncer?.find(
                        bufferName = channelName,
                        networkId = user.network().networkId()
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
                          network = user.network().networkInfo(),
                          description = it.topic(),
                          activity = Message_Type.of(),
                          bufferStatus = bufferStatus,
                          hiddenState = BufferHiddenState.VISIBLE,
                          networkConnectionState = user.network().connectionState(),
                          fallbackDrawable = fallbackDrawable
                        )
                      }
                    )
                  }
                }
              }).map {
                it.mapNotNull(Optional<BufferProps>::orNull)
              }.map {
                Optional.of(buildUserInfo(it))
              }
            }
          }
        }
      }

      val session = sessionOptional?.orNull()
      if (openBuffer == true) {
        val bufferSyncer = session?.bufferSyncer
        val bufferInfo = bufferSyncer?.bufferInfo(bufferId)
        bufferInfo?.let {
          networks[it.networkId]?.liveIrcUser(it.bufferName)?.switchMap(IrcUser::updates)?.switchMap {
            processUser(it, bufferSyncer, bufferInfo)
          }
        }
      } else {
        val ignoreListManager = session?.ignoreListManager

        networks[networkId]
          ?.liveIrcUser(nickName)
          ?.switchMap(IrcUser::updates)
          ?.switchMap { user ->
            ignoreListManager?.liveMatchingRules(user.hostMask())?.map {
              Pair(user, it)
            } ?: Observable.just(Pair(user, emptyList()))
          }?.switchMap { (user, ignoreItems) ->
            processUser(user,
                        sessionOptional?.orNull()?.bufferSyncer,
                        ignoreItems = ignoreItems)
          }
      } ?: Observable.just(IrcUser.NULL).switchMap { user -> processUser(user, null, null) }
    }.toLiveData().observe(this, Observer {
      val user = it.orNull()
      if (user != null) {
        currentBufferInfo = user.info
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
              is String              -> {
                actualUrl = model
              }
              is Avatar.MatrixAvatar -> {
                runInBackground {
                  matrixApi.avatarUrl(model.userId).execute().body()?.let {
                    it.avatarUrl?.let {
                      val avatarInfo = MatrixAvatarInfo(it, model.size)
                      val url = Uri.parse(avatarInfo.avatarUrl)

                      val imageUrl = matrixApi.avatarImage(server = url.host,
                                                           id = url.pathSegments.first()).request().url()
                      actualUrl = imageUrl.toString()
                    }
                  }
                }
              }
            }
          }
        }

        nick.text = user.nick
        val (content, _) = contentFormatter.formatContent(
          user.realName ?: "",
          networkId = user.networkId
        )
        realName.text = content
        realName.visibleIf(!user.realName.isNullOrBlank() && user.realName != user.nick)

        awayMessage.text = user.awayMessage.nullIf { it.isNullOrBlank() } ?: SpannableString(
          getString(
            R.string.label_no_away_message)).apply {
          setSpan(IrcItalicSpan(), 0, length, 0)
        }
        awayContainer.visibleIf(user.isAway == true)

        account.text = user.account
        accountContainer.visibleIf(!user.account.isNullOrBlank())

        ident.text = user.user
        identContainer.visibleIf(!user.user.isNullOrBlank())

        host.text = user.host
        hostContainer.visibleIf(!user.host.isNullOrBlank())

        server.text = user.server
        serverContainer.visibleIf(!user.server.isNullOrBlank())

        actionWhois.visibleIf(user.knownToCore)

        actionQuery.setOnClickListener { view ->
          modelHelper.session.value?.orNull()?.let { session ->
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
              }.firstElement().toLiveData().observe(this, Observer {
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
          PopupMenu(actionIgnore.context, actionIgnore).also { menu ->
            ignoreMenu?.dismiss()
            menu.menuInflater.inflate(R.menu.context_ignore, menu.menu)
            for (ignoreItem in user.ignoreListItems) {
              menu.menu.add(ignoreItem.ignoreRule).apply {
                isCheckable = true
                isChecked = ignoreItem.isActive
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
                  modelHelper.ignoreListManager.value?.orNull()?.requestToggleIgnoreRule(it.title.toString())
                  true
                }
                else               -> false
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
          modelHelper.session {
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
          this.context?.let { context ->
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

        commonChannelsAdapter.submitList(user.channels)
      }
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
