package de.kuschku.quasseldroid.ui.chat.nicks

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.info.InfoActivity
import de.kuschku.quasseldroid.ui.chat.info.InfoDescriptor
import de.kuschku.quasseldroid.ui.chat.info.InfoType
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import javax.inject.Inject

class NickListFragment : ServiceBoundFragment() {
  @BindView(R.id.nickList)
  lateinit var nickList: RecyclerView

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_nick_list, container, false)
    ButterKnife.bind(this, view)

    val nickListAdapter = NickListAdapter(clickListener)
    nickList.adapter = nickListAdapter
    nickList.layoutManager = object : LinearLayoutManager(context) {
      override fun supportsPredictiveItemAnimations() = false
    }
    nickList.itemAnimator = DefaultItemAnimator()
    viewModel.nickData.map {
      it.map {
        it.copy(
          modes = when (messageSettings.showPrefix) {
            MessageSettings.ShowPrefixMode.ALL ->
              it.modes
            else                               ->
              it.modes.substring(0, Math.min(it.modes.length, 1))
          },
          realname = ircFormatDeserializer.formatString(
            requireContext(), it.realname.toString(), messageSettings.colorizeMirc
          )
        )
      }.sortedBy {
        IrcCaseMappers[it.networkCasemapping].toLowerCase(it.nick)
      }.sortedBy {
        it.lowestMode
      }
    }.toLiveData().observe(this, Observer(nickListAdapter::submitList))

    savedInstanceState?.run {
      nickList.layoutManager.onRestoreInstanceState(getParcelable(KEY_STATE_LIST))
    }

    return view
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(KEY_STATE_LIST, nickList.layoutManager.onSaveInstanceState())
  }

  private val clickListener: ((String) -> Unit)? = { nick ->
    viewModel.bufferData.value?.info?.let(BufferInfo::networkId)?.let { networkId ->
      val intent = Intent(requireContext(), InfoActivity::class.java)
      intent.putExtra("info", InfoDescriptor(
        type = InfoType.User,
        nick = nick,
        network = networkId
      ))
      startActivity(intent)
    }
  }

  companion object {
    private const val KEY_STATE_LIST = "KEY_STATE_LIST"
  }
}