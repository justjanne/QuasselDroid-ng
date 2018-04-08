package de.kuschku.quasseldroid.ui.chat.info

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.chat.topic.TopicActivity
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import io.reactivex.Observable
import javax.inject.Inject

class InfoFragment : ServiceBoundFragment() {
  @BindView(R.id.groups)
  lateinit var groups: RecyclerView

  @Inject
  lateinit var contentFormatter: ContentFormatter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_info, container, false)
    ButterKnife.bind(this, view)

    val adapter = InfoGroupAdapter()
    groups.layoutManager = LinearLayoutManager(requireContext())
    groups.adapter = adapter

    val info = arguments?.getSerializable("info") as? InfoDescriptor
    viewModel.session.switchMap { sessionOptional ->
      val network = sessionOptional.orNull()?.networks?.get(info?.network)
      if (info == null || network == null) {
        Observable.just(Optional.empty())
      } else {
        when (info.type) {
          InfoType.User    -> {
            network.liveIrcUser(info.nick).switchMap {
              it.updates().map { user ->
                Optional.of(InfoData(
                  type = info.type,
                  user = user,
                  network = network,
                  properties = listOf(
                    InfoGroup(
                      name = getString(R.string.property_group_ircuser_identity),
                      properties = listOf(
                        InfoProperty(
                          name = getString(R.string.property_ircuser_nick),
                          value = user.nick()
                        ),
                        InfoProperty(
                          name = getString(R.string.property_ircuser_user),
                          value = user.user()
                        ),
                        InfoProperty(
                          name = getString(R.string.property_ircuser_host),
                          value = user.host()
                        ),
                        InfoProperty(
                          name = getString(R.string.property_ircuser_realname),
                          value = contentFormatter.format(requireContext(), user.realName())
                        ),
                        InfoProperty(
                          name = getString(R.string.property_ircuser_account),
                          value = user.account()
                        )
                      )
                    )
                  )
                ))
              }
            }
          }
          InfoType.Channel -> {
            network.liveIrcChannel(info.channel).map { channel ->
              Optional.of(InfoData(
                type = info.type,
                channel = channel,
                network = network,
                properties = listOf(
                  InfoGroup(
                    name = getString(R.string.property_group_ircchannel_channel),
                    properties = listOf(
                      InfoProperty(
                        name = getString(R.string.property_ircchannel_topic),
                        value = contentFormatter.format(requireContext(), channel.topic()),
                        actions = listOf(
                          InfoPropertyAction(
                            name = getString(R.string.property_ircchannel_topic_action_edit),
                            featured = true,
                            onClick = {
                              val intent = Intent(requireContext(), TopicActivity::class.java)
                              intent.putExtra("buffer", info.buffer)
                              startActivity(intent)
                            }
                          )
                        )
                      )
                    )
                  )
                )
              ))
            }
          }
          InfoType.Network -> {
            network.live_connectionState.map {
              Optional.of(InfoData(
                type = info.type,
                network = network
              ))
            }
          }
        }
      }
    }.toLiveData().observe(this, Observer {
      LoggingHandler.log(LoggingHandler.LogLevel.ERROR, "DEBUG", "data: $it")
      adapter.submitList(it?.orNull()?.properties.orEmpty())
    })

    return view
  }
}
