package de.kuschku.quasseldroid_ng.ui.chat.nicks

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.settings.AppearanceSettings
import de.kuschku.quasseldroid_ng.settings.Settings
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.map
import de.kuschku.quasseldroid_ng.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid_ng.viewmodel.QuasselViewModel

class NickListFragment : ServiceBoundFragment() {
  private lateinit var viewModel: QuasselViewModel

  private val handlerThread = AndroidHandlerThread("NickList")

  @BindView(R.id.nickList)
  lateinit var nickList: RecyclerView

  private var ircFormatDeserializer: IrcFormatDeserializer? = null
  private lateinit var appearanceSettings: AppearanceSettings

  override fun onCreate(savedInstanceState: Bundle?) {
    handlerThread.onCreate()
    super.onCreate(savedInstanceState)

    viewModel = ViewModelProviders.of(activity!!)[QuasselViewModel::class.java]
    appearanceSettings = Settings.appearance(activity!!)

    if (ircFormatDeserializer == null) {
      ircFormatDeserializer = IrcFormatDeserializer(context!!)
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_nick_list, container, false)
    ButterKnife.bind(this, view)

    val nickListAdapter = NickListAdapter(clickListener)
    nickList.adapter = nickListAdapter
    nickList.layoutManager = LinearLayoutManager(context)
    nickList.itemAnimator = DefaultItemAnimator()
    viewModel.nickData.map {
      it.map {
        it.copy(
          modes = when (appearanceSettings.showPrefix) {
            AppearanceSettings.ShowPrefixMode.ALL ->
              it.modes
            else                                  ->
              it.modes.substring(0, Math.min(it.modes.length, 1))
          },
          realname = ircFormatDeserializer?.formatString(
            it.realname.toString(), appearanceSettings.colorizeMirc
          ) ?: it.realname
        )
      }.sortedBy {
        IrcCaseMappers[it.networkCasemapping].toLowerCase(it.nick)
      }.sortedBy {
        it.lowestMode
      }
    }.observe(this, Observer(nickListAdapter::submitList))

    return view
  }

  override fun onDestroy() {
    handlerThread.onDestroy()
    super.onDestroy()
  }

  private val clickListener: ((String) -> Unit)? = {
    // TODO
  }
}