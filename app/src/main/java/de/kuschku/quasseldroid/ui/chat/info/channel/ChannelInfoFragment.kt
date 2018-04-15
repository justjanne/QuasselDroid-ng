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
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.retint
import de.kuschku.quasseldroid.util.helper.setTooltip
import de.kuschku.quasseldroid.util.helper.toLiveData
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
        topic.text = contentFormatter.format(requireContext(), channel.topic())

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
