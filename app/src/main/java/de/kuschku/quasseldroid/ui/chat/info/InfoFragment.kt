package de.kuschku.quasseldroid.ui.chat.info

import android.arch.lifecycle.Observer
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
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.viewmodel.data.InfoGroup
import de.kuschku.quasseldroid.viewmodel.data.InfoProperty
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
                      name = "Identity",
                      properties = listOf(
                        InfoProperty(
                          name = "Nickname",
                          value = user.nick()
                        ),
                        InfoProperty(
                          name = "Ident",
                          value = user.user()
                        ),
                        InfoProperty(
                          name = "Host",
                          value = user.host()
                        ),
                        InfoProperty(
                          name = "Real Name",
                          value = contentFormatter.format(requireContext(), user.realName())
                        ),
                        InfoProperty(
                          name = "Account",
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
                    name = "Channel",
                    properties = listOf(
                      InfoProperty(
                        name = "Topic",
                        value = contentFormatter.format(requireContext(), channel.topic())
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