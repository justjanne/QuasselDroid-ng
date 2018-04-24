/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat.info.user

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.util.IrcUserUtils
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.chat.input.AutoCompleteHelper.Companion.IGNORED_CHARS
import de.kuschku.quasseldroid.util.AvatarHelper
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import de.kuschku.quasseldroid.util.ui.TextDrawable
import io.reactivex.Observable
import me.saket.bettermovementmethod.BetterLinkMovementMethod
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

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_info_user, container, false)
    ButterKnife.bind(this, view)

    val openBuffer = arguments?.getBoolean("openBuffer")

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

    val networkId = arguments?.getInt("networkId")
    val nickName = arguments?.getString("nick")
    combineLatest(viewModel.session, viewModel.networks).switchMap { (sessionOptional, networks) ->
      if (openBuffer == true) {
        val session = sessionOptional?.orNull()
        val bufferSyncer = session?.bufferSyncer
        val bufferInfo = bufferSyncer?.bufferInfo(arguments?.getInt("bufferId") ?: -1)
        bufferInfo?.let {
          networks[it.networkId]?.liveIrcUser(it.bufferName)
        }
      } else {
        networks[networkId]?.liveIrcUser(nickName)
      } ?: Observable.just(IrcUser.NULL)
    }.filter {
      it != IrcUser.NULL
    }.switchMap(IrcUser::updates).toLiveData().observe(this, Observer { user ->
      if (user != null) {
        val senderColorIndex = IrcUserUtils.senderColor(user.nick())
        val rawInitial = user.nick().trimStart(*IGNORED_CHARS).firstOrNull()
                         ?: user.nick().firstOrNull()
        val initial = rawInitial?.toUpperCase().toString()
        val senderColor = senderColors[senderColorIndex]

        avatar.loadAvatars(
          AvatarHelper.avatar(messageSettings, user, maxOf(avatar.width, avatar.height)),
          TextDrawable.builder().buildRect(initial, senderColor),
          crop = false
        )

        nick.text = user.nick()
        realName.text = contentFormatter.format(mircColors, user.realName())
        realName.visibleIf(user.realName().isNotBlank() && user.realName() != user.nick())

        awayMessage.text = user.awayMessage()
        awayContainer.visibleIf(user.awayMessage().isNotBlank())

        account.text = user.account()
        accountContainer.visibleIf(user.account().isNotBlank())

        ident.text = user.user()
        identContainer.visibleIf(user.user().isNotBlank())

        host.text = user.host()
        hostContainer.visibleIf(user.host().isNotBlank())

        server.text = user.server()
        serverContainer.visibleIf(user.server().isNotBlank())

        actionQuery.setOnClickListener {
          viewModel.session {
            it.orNull()?.let { session ->
              val info = session.bufferSyncer?.find(
                bufferName = user.nick(),
                networkId = user.network().networkId(),
                type = Buffer_Type.of(Buffer_Type.QueryBuffer)
              )

              if (info != null) {
                ChatActivity.launch(requireContext(), bufferId = info.bufferId)
              } else {
                viewModel.allBuffers.map {
                  listOfNotNull(it.find {
                    it.networkId == user.network().networkId() && it.bufferName == user.nick()
                  })
                }.filter {
                  it.isNotEmpty()
                }.firstElement().toLiveData().observe(this, Observer {
                  it?.firstOrNull()?.let { info ->
                    ChatActivity.launch(requireContext(), bufferId = info.bufferId)
                  }
                })

                session.bufferSyncer?.find(
                  networkId = networkId,
                  type = Buffer_Type.of(Buffer_Type.StatusBuffer)
                )?.let { statusInfo ->
                  session.rpcHandler?.sendInput(statusInfo, "/query ${user.nick()}")
                }
              }
            }
          }
        }

        actionIgnore.setOnClickListener {
          Toast.makeText(requireContext(), "Not Implemented", Toast.LENGTH_SHORT).show()
        }

        actionWhois.setOnClickListener {
          viewModel.session {
            it.orNull()?.let { session ->
              session.bufferSyncer?.find(
                networkId = networkId,
                type = Buffer_Type.of(Buffer_Type.StatusBuffer)
              )?.let { statusInfo ->
                session.rpcHandler?.sendInput(statusInfo, "/whois ${user.nick()}")
              }
            }
          }
        }

        actionMention.setOnClickListener {
          ChatActivity.launch(requireContext(), sharedText = "${user.nick()}: ")
        }
        actionMention.visibleIf(arguments?.getBoolean("openBuffer") == false)
      }
    })

    val movementMethod = BetterLinkMovementMethod.newInstance()
    movementMethod.setOnLinkLongClickListener(LinkLongClickMenuHelper())
    realName.movementMethod = movementMethod

    actionQuery.setTooltip()
    actionQuery.retint()
    actionIgnore.setTooltip()
    actionIgnore.retint()
    actionWhois.setTooltip()
    actionWhois.retint()
    actionMention.setTooltip()
    actionMention.retint()

    return view
  }
}
