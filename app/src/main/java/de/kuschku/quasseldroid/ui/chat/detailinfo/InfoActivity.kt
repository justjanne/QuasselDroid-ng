package de.kuschku.quasseldroid.ui.chat.detailinfo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.service.ServiceBoundActivity
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid.viewmodel.data.InfoGroup
import de.kuschku.quasseldroid.viewmodel.data.InfoProperty
import io.reactivex.Observable
import javax.inject.Inject

class InfoActivity : ServiceBoundActivity() {
  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  @BindView(R.id.groups)
  lateinit var groups: RecyclerView

  @Inject
  lateinit var contentFormatter: ContentFormatter

  private lateinit var viewModel: QuasselViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_info)
    ButterKnife.bind(this)

    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    viewModel = ViewModelProviders.of(this)[QuasselViewModel::class.java]
    viewModel.backendWrapper.onNext(this.backend)

    val adapter = InfoGroupAdapter()
    groups.layoutManager = LinearLayoutManager(this)
    groups.adapter = adapter

    val info = intent.getSerializableExtra("info") as? InfoDescriptor
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
                          value = contentFormatter.format(this, user.realName())
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
                    properties = listOf(
                      InfoProperty(
                        name = "Topic",
                        value = contentFormatter.format(this, channel.topic())
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
      log(LoggingHandler.LogLevel.ERROR, "DEBUG", "data: $it")
      adapter.submitList(it?.orNull()?.properties.orEmpty())
    })

    viewModel.buffer.onNext(intent.getIntExtra("buffer", -1))
  }
}