package de.kuschku.quasseldroid.ui.coresettings.chatlist

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.util.helper.toLiveData
import io.reactivex.Observable

class ChatListFragment : SettingsFragment() {
  private var chatlist: Pair<BufferViewConfig, BufferViewConfig>? = null

  @BindView(R.id.buffer_view_name)
  lateinit var bufferViewName: TextView

  @BindView(R.id.show_search)
  lateinit var showSearch: SwitchCompat

  @BindView(R.id.sort_alphabetically)
  lateinit var sortAlphabetically: SwitchCompat

  @BindView(R.id.add_new_buffers_automatically)
  lateinit var addNewBuffersAutomatically: SwitchCompat

  @BindView(R.id.disable_decoration)
  lateinit var disableDecoration: SwitchCompat

  @BindView(R.id.show_channels)
  lateinit var showChannels: SwitchCompat

  @BindView(R.id.show_queries)
  lateinit var showQueries: SwitchCompat

  @BindView(R.id.hide_inactive_buffers)
  lateinit var hideInactiveBuffers: SwitchCompat

  @BindView(R.id.hide_inactive_networks)
  lateinit var hideInactiveNetworks: SwitchCompat

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_chatlist, container, false)
    ButterKnife.bind(this, view)

    val chatlistId = arguments?.getInt("chatlist", -1) ?: -1

    viewModel.bufferViewConfigMap.switchMap {
      it[chatlistId]?.liveUpdates() ?: Observable.empty()
    }.firstElement()
      .toLiveData().observe(this, Observer {
        if (it != null) {
          this.chatlist = Pair(it, it.copy())
          this.chatlist?.let { (_, data) ->
            bufferViewName.text = data.bufferViewName()
            showSearch.isChecked = data.showSearch()
            sortAlphabetically.isChecked = data.sortAlphabetically()
            addNewBuffersAutomatically.isChecked = data.addNewBuffersAutomatically()
            disableDecoration.isChecked = data.disableDecoration()

            hideInactiveBuffers.isChecked = data.hideInactiveBuffers()
            hideInactiveNetworks.isChecked = data.hideInactiveNetworks()
          }
        }
      })

    return view
  }

  override fun onSave() = chatlist?.let { (it, data) ->
    data.setBufferViewName(bufferViewName.text.toString())
    data.setShowSearch(showSearch.isChecked)
    data.setSortAlphabetically(sortAlphabetically.isChecked)
    data.setAddNewBuffersAutomatically(addNewBuffersAutomatically.isChecked)
    data.setDisableDecoration(disableDecoration.isChecked)

    data.setHideInactiveBuffers(hideInactiveBuffers.isChecked)
    data.setHideInactiveNetworks(hideInactiveNetworks.isChecked)

    it.requestUpdate(data.toVariantMap())
    true
  } ?: false
}
