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

package de.kuschku.quasseldroid.ui.chat.info.channel

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.chat.topic.TopicActivity
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject

class ChannelInfoFragment : ServiceBoundFragment() {

  @BindView(R.id.name)
  lateinit var name: TextView

  @BindView(R.id.topic)
  lateinit var topic: TextView

  @BindView(R.id.action_edit_topic)
  lateinit var actionEditTopic: Button

  @BindView(R.id.action_who)
  lateinit var actionWho: Button

  @BindView(R.id.action_part)
  lateinit var actionPart: Button

  @BindView(R.id.action_join)
  lateinit var actionJoin: Button

  @Inject
  lateinit var contentFormatter: ContentFormatter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_info_channel, container, false)
    ButterKnife.bind(this, view)

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

    val openBuffer = arguments?.getBoolean("openBuffer")

    combineLatest(viewModel.session, viewModel.networks).map { (sessionOptional, networks) ->
      if (openBuffer == true) {
        val session = sessionOptional?.orNull()
        val bufferSyncer = session?.bufferSyncer
        val bufferInfo = bufferSyncer?.bufferInfo(arguments?.getInt("bufferId") ?: -1)
        bufferInfo?.let {
          networks[it.networkId]?.ircChannel(it.bufferName)
        }
      } else {
        networks[arguments?.getInt("networkId")]?.ircChannel(arguments?.getString("nick"))
      } ?: IrcChannel.NULL
    }.filter {
      it != IrcChannel.NULL
    }.switchMap(IrcChannel::updates).toLiveData().observe(this, Observer { channel ->
      if (channel != null) {
        name.text = channel.name()
        topic.text = contentFormatter.format(mircColors, channel.topic())

        actionEditTopic.setOnClickListener {
          TopicActivity.launch(requireContext(), buffer = arguments?.getInt("bufferId") ?: -1)
        }

        actionPart.setOnClickListener {
          viewModel.session.value?.orNull()?.let { session ->
            session.bufferSyncer?.find(
              networkId = channel.network().networkId(),
              type = Buffer_Type.of(Buffer_Type.StatusBuffer)
            )?.let { statusInfo ->
              session.rpcHandler?.sendInput(statusInfo, "/part ${channel.name()}")
              requireActivity().finish()
            }
          }
        }

        actionWho.setOnClickListener {
          viewModel.session.value?.orNull()?.let { session ->
            session.bufferSyncer?.find(
              networkId = channel.network().networkId(),
              type = Buffer_Type.of(Buffer_Type.StatusBuffer)
            )?.let { statusInfo ->
              session.rpcHandler?.sendInput(statusInfo, "/who ${channel.name()}")
              requireActivity().finish()
            }
          }
        }
      }
    })

    val movementMethod = BetterLinkMovementMethod.newInstance()
    movementMethod.setOnLinkLongClickListener(LinkLongClickMenuHelper())
    topic.movementMethod = movementMethod

    actionEditTopic.setTooltip()
    actionEditTopic.retint()
    actionWho.setTooltip()
    actionWho.retint()
    actionJoin.setTooltip()
    actionJoin.retint()
    actionPart.setTooltip()
    actionPart.retint()

    return view
  }
}
