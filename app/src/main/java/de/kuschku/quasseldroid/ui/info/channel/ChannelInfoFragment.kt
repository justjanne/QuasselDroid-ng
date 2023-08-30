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

package de.kuschku.quasseldroid.ui.info.channel

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.safeSwitchMap
import de.kuschku.libquassel.util.helper.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.topic.TopicActivity
import de.kuschku.quasseldroid.util.ShortcutCreationHelper
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.BetterLinkMovementMethod
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import javax.inject.Inject

class ChannelInfoFragment : ServiceBoundFragment() {
  lateinit var name: TextView
  lateinit var topic: TextView
  lateinit var actionEditTopic: Button
  lateinit var actionWho: Button
  lateinit var actionPart: Button
  lateinit var actionJoin: Button
  lateinit var actionShortcut: Button

  @Inject
  lateinit var contentFormatter: ContentFormatter

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.info_channel, container, false)
    this.name = view.findViewById(R.id.name)
    this.topic = view.findViewById(R.id.topic)
    this.actionEditTopic = view.findViewById(R.id.action_edit_topic)
    this.actionWho = view.findViewById(R.id.action_who)
    this.actionPart = view.findViewById(R.id.action_part)
    this.actionJoin = view.findViewById(R.id.action_join)
    this.actionShortcut = view.findViewById(R.id.action_shortcut)

    val openBuffer = arguments?.getBoolean("openBuffer")
    val bufferId = BufferId(arguments?.getInt("bufferId") ?: -1)
    val networkId = NetworkId(arguments?.getInt("networkId") ?: -1)

    var currentBufferInfo: BufferInfo?

    combineLatest(modelHelper.connectedSession,
                  modelHelper.networks).map { (sessionOptional, networks) ->
      if (openBuffer == true) {
        val session = sessionOptional?.orNull()
        val bufferSyncer = session?.bufferSyncer
        val bufferInfo = bufferSyncer?.bufferInfo(bufferId)
        bufferInfo?.let { info ->
          networks[info.networkId]?.ircChannel(info.bufferName)?.let {
            Pair(info, it)
          }
        }
      } else {
        networks[networkId]?.ircChannel(arguments?.getString("nick"))?.let {
          Pair(null, it)
        }
      } ?: Pair(null, IrcChannel.NULL)
    }.filter {
      it.second != IrcChannel.NULL
    }.safeSwitchMap { (info, channel) ->
      channel.updates().map {
        Pair(info, it)
      }
    }.toLiveData().observe(viewLifecycleOwner, Observer { (info, channel) ->
      name.text = channel.name()
      val (content, hasSpoilers) = contentFormatter.formatContent(
        channel.topic(),
        networkId = channel.network().networkId()
      )
      topic.text = content

      currentBufferInfo = info
      actionShortcut.visibleIf(info != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

      actionEditTopic.setOnClickListener {
        TopicActivity.launch(requireContext(), buffer = arguments?.getInt("bufferId") ?: -1)
      }

      actionPart.setOnClickListener {
        modelHelper.connectedSession.value?.orNull()?.let { session ->
          session.bufferSyncer.find(
            networkId = channel.network().networkId(),
            type = Buffer_Type.of(Buffer_Type.StatusBuffer)
          )?.let { statusInfo ->
            session.rpcHandler.sendInput(statusInfo, "/part ${channel.name()}")
            requireActivity().finish()
          }
        }
      }

      actionWho.setOnClickListener {
        modelHelper.connectedSession.value?.orNull()?.let { session ->
          session.bufferSyncer.find(
            networkId = channel.network().networkId(),
            type = Buffer_Type.of(Buffer_Type.StatusBuffer)
          )?.let { statusInfo ->
            session.rpcHandler.sendInput(statusInfo, "/who ${channel.name()}")
            requireActivity().finish()
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
              info = info
            )
          }
        }
      }
    })

    val movementMethod = BetterLinkMovementMethod.newInstance()
    movementMethod.setOnLinkLongClickListener(LinkLongClickMenuHelper())
    topic.movementMethod = movementMethod

    actionEditTopic.setTooltip()
    actionEditTopic.setCompoundDrawablesWithIntrinsicBounds(
      null,
      requireContext().getVectorDrawableCompat(R.drawable.ic_pencil),
      null,
      null
    )
    actionEditTopic.retint()

    actionWho.setTooltip()
    actionWho.setCompoundDrawablesWithIntrinsicBounds(
      null,
      requireContext().getVectorDrawableCompat(R.drawable.ic_info),
      null,
      null
    )
    actionWho.retint()

    actionJoin.setTooltip()
    actionJoin.setCompoundDrawablesWithIntrinsicBounds(
      null,
      requireContext().getVectorDrawableCompat(R.drawable.ic_account_plus),
      null,
      null
    )
    actionJoin.retint()

    actionPart.setTooltip()
    actionPart.setCompoundDrawablesWithIntrinsicBounds(
      null,
      requireContext().getVectorDrawableCompat(R.drawable.ic_account_minus),
      null,
      null
    )
    actionPart.retint()

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
