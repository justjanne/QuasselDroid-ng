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

package de.kuschku.quasseldroid.ui.chat.info.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helpers.nullIf
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
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

  @Inject
  lateinit var contentFormatter: ContentFormatter

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var matrixApi: MatrixApi

  private var actualUrl: String? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_info_user, container, false)
    ButterKnife.bind(this, view)

    val openBuffer = arguments?.getBoolean("openBuffer")

    val networkId2 = arguments?.getInt("networkId")
    val nickName2 = arguments?.getString("nick")
    combineLatest(viewModel.session, viewModel.networks).switchMap { (sessionOptional, networks) ->
      fun processUser(user: IrcUser, info: BufferInfo? = null) = when {
        user == IrcUser.NULL && info != null -> Optional.of(IrcUserInfo(
          networkId = info.networkId,
          nick = info.bufferName ?: "",
          knownToCore = true
        ))
        user == IrcUser.NULL                 -> Optional.empty()
        else                                 -> Optional.of(IrcUserInfo(
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
          knownToCore = true
        ))
      }

      if (openBuffer == true) {
        val session = sessionOptional?.orNull()
        val bufferSyncer = session?.bufferSyncer
        val bufferInfo = bufferSyncer?.bufferInfo(arguments?.getInt("bufferId") ?: -1)
        bufferInfo?.let {
          networks[it.networkId]?.liveIrcUser(it.bufferName)?.switchMap(IrcUser::updates)?.map {
            processUser(it, bufferInfo)
          }
        }
      } else {
        networks[networkId2]
          ?.liveIrcUser(nickName2)
          ?.switchMap(IrcUser::updates)
          ?.map { user -> processUser(user) }
      } ?: Observable.just(IrcUser.NULL).map { user -> processUser(user) }
    }.toLiveData().observe(this, Observer {
      val processUser = { user: IrcUserInfo ->
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
        realName.text = contentFormatter.formatContent(user.realName ?: "",
                                                       networkId = user.networkId)
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

        actionQuery.setOnClickListener {
          viewModel.session.value?.orNull()?.let { session ->
            val info = session.bufferSyncer?.find(
              bufferName = user.nick,
              networkId = user.networkId,
              type = Buffer_Type.of(Buffer_Type.QueryBuffer)
            )

            if (info != null) {
              ChatActivity.launch(requireContext(),
                                  bufferId = info.bufferId)
            } else {
              viewModel.allBuffers.map {
                listOfNotNull(it.find {
                  it.networkId == user.networkId && it.bufferName == user.nick
                })
              }.filter {
                it.isNotEmpty()
              }.firstElement().toLiveData().observe(this, Observer {
                it?.firstOrNull()?.let { info ->
                  ChatActivity.launch(requireContext(),
                                      bufferId = info.bufferId)
                }
              })

              session.bufferSyncer?.find(
                networkId = user.networkId,
                type = Buffer_Type.of(Buffer_Type.StatusBuffer)
              )?.let { statusInfo ->
                session.rpcHandler?.sendInput(statusInfo,
                                              "/query ${user.nick}")
              }
            }
          }
        }

        actionIgnore.setOnClickListener {
          Toast.makeText(requireContext(), "Not Implemented", Toast.LENGTH_SHORT).show()
        }

        actionMention.setOnClickListener {
          ChatActivity.launch(requireContext(), sharedText = "${user.nick}: ")
        }

        actionWhois.setOnClickListener {
          viewModel.session {
            it.orNull()?.let { session ->
              session.bufferSyncer?.find(
                networkId = user.networkId,
                type = Buffer_Type.of(Buffer_Type.StatusBuffer)
              )?.let { statusInfo ->
                session.rpcHandler?.sendInput(statusInfo, "/whois ${user.nick} ${user.nick}")
              }
            }
          }
        }
      }
      it?.orNull()?.let(processUser)
    })

    avatar.setOnClickListener {
      context?.startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(actualUrl)
      })
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

    return view
  }
}
