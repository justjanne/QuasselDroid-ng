package de.kuschku.quasseldroid.ui.coresettings.chatlist

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.flag.minus
import de.kuschku.libquassel.util.flag.plus
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.toLiveData

abstract class ChatListBaseFragment : SettingsFragment(), SettingsFragment.Savable,
                                      SettingsFragment.Changeable {
  @BindView(R.id.buffer_view_name)
  lateinit var bufferViewName: EditText

  @BindView(R.id.show_search)
  lateinit var showSearch: SwitchCompat

  @BindView(R.id.sort_alphabetically)
  lateinit var sortAlphabetically: SwitchCompat

  @BindView(R.id.add_new_buffers_automatically)
  lateinit var addNewBuffersAutomatically: SwitchCompat

  @BindView(R.id.network_id)
  lateinit var networkId: Spinner

  @BindView(R.id.show_status_buffer)
  lateinit var showStatusBuffer: SwitchCompat

  @BindView(R.id.show_channels)
  lateinit var showChannels: SwitchCompat

  @BindView(R.id.show_queries)
  lateinit var showQueries: SwitchCompat

  @BindView(R.id.minimum_activity)
  lateinit var minimumActivity: Spinner

  @BindView(R.id.hide_inactive_buffers)
  lateinit var hideInactiveBuffers: SwitchCompat

  @BindView(R.id.hide_inactive_networks)
  lateinit var hideInactiveNetworks: SwitchCompat

  protected var chatlist: Pair<BufferViewConfig?, BufferViewConfig>? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_chatlist, container, false)
    ButterKnife.bind(this, view)

    val chatlistId = arguments?.getInt("chatlist", -1) ?: -1

    val minimumActivityAdapter = MinimumActivityAdapter(listOf(
      MinimumActivityItem(
        activity = Buffer_Activity.NoActivity,
        name = R.string.settings_chatlist_minimum_activity_no_activity
      ),
      MinimumActivityItem(
        activity = Buffer_Activity.OtherActivity,
        name = R.string.settings_chatlist_minimum_activity_other_activity
      ),
      MinimumActivityItem(
        activity = Buffer_Activity.NewMessage,
        name = R.string.settings_chatlist_minimum_activity_new_message
      ),
      MinimumActivityItem(
        activity = Buffer_Activity.Highlight,
        name = R.string.settings_chatlist_minimum_activity_highlight
      )
    ))
    minimumActivity.adapter = minimumActivityAdapter

    val networkAdapter = NetworkAdapter()
    networkId.adapter = networkAdapter

    viewModel.networks.switchMap {
      combineLatest(it.values.map(Network::liveNetworkInfo)).map {
        it.sortedBy(INetwork.NetworkInfo::networkName)
      }
    }.toLiveData().observe(this, Observer {
      if (it != null) {
        val selectOriginal = networkId.selectedItemId == Spinner.INVALID_ROW_ID
        networkAdapter.submitList(listOf(null) + it)
        if (selectOriginal) {
          this.chatlist?.let { (_, data) ->
            networkAdapter.indexOf(data.networkId())?.let(networkId::setSelection)
          }
        }
      }
    })

    viewModel.bufferViewConfigMap.map { Optional.ofNullable(it[chatlistId]) }
      .filter(Optional<BufferViewConfig>::isPresent)
      .map(Optional<BufferViewConfig>::get)
      .firstElement()
      .toLiveData().observe(this, Observer {
        it?.let {
          this.chatlist = Pair(it, it.copy())
          this.chatlist?.let { (_, data) ->
            bufferViewName.setText(data.bufferViewName())
            showSearch.isChecked = data.showSearch()
            sortAlphabetically.isChecked = data.sortAlphabetically()
            addNewBuffersAutomatically.isChecked = data.addNewBuffersAutomatically()
            showStatusBuffer.isChecked = data.allowedBufferTypes().hasFlag(Buffer_Type.StatusBuffer)

            minimumActivity.setSelection(
              minimumActivityAdapter.indexOf(data.minimumActivity()) ?: 0
            )

            networkAdapter.indexOf(data.networkId())?.let(networkId::setSelection)

            hideInactiveBuffers.isChecked = data.hideInactiveBuffers()
            hideInactiveNetworks.isChecked = data.hideInactiveNetworks()

            showQueries.isChecked = data.allowedBufferTypes().hasFlag(Buffer_Type.QueryBuffer)
            showChannels.isChecked = data.allowedBufferTypes().hasFlag(Buffer_Type.ChannelBuffer)
          }
        }
      })
    networkId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {
        showStatusBuffer.isChecked = true
        showStatusBuffer.isEnabled = false
      }

      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (id == -1L) {
          showStatusBuffer.isChecked = true
          showStatusBuffer.isEnabled = false
        } else {
          showStatusBuffer.isEnabled = true
        }
      }
    }

    return view
  }

  override fun hasChanged() = chatlist?.let { (it, data) ->
    applyChanges(data, it)

    it == null ||
    data.bufferViewName() != it.bufferViewName() ||
    data.showSearch() != it.showSearch() ||
    data.sortAlphabetically() != it.sortAlphabetically() ||
    data.addNewBuffersAutomatically() != it.addNewBuffersAutomatically() ||
    data.hideInactiveBuffers() != it.hideInactiveBuffers() ||
    data.hideInactiveNetworks() != it.hideInactiveNetworks() ||
    data.allowedBufferTypes() != it.allowedBufferTypes() ||
    data.networkId() != it.networkId() ||
    data.minimumActivity() != it.minimumActivity()
  } ?: true

  protected fun applyChanges(data: BufferViewConfig, old: BufferViewConfig?) {
    data.setBufferViewName(bufferViewName.text.toString())
    data.setShowSearch(showSearch.isChecked)
    data.setSortAlphabetically(sortAlphabetically.isChecked)
    data.setAddNewBuffersAutomatically(addNewBuffersAutomatically.isChecked)

    data.setHideInactiveBuffers(hideInactiveBuffers.isChecked)
    data.setHideInactiveNetworks(hideInactiveNetworks.isChecked)

    var allowedBufferTypes = data.allowedBufferTypes()
    if (showQueries.isChecked) allowedBufferTypes += Buffer_Type.QueryBuffer
    else allowedBufferTypes -= Buffer_Type.QueryBuffer
    if (showChannels.isChecked) allowedBufferTypes += Buffer_Type.ChannelBuffer
    else allowedBufferTypes -= Buffer_Type.ChannelBuffer
    if (showStatusBuffer.isChecked) allowedBufferTypes += Buffer_Type.StatusBuffer
    else allowedBufferTypes -= Buffer_Type.StatusBuffer
    data.setAllowedBufferTypes(allowedBufferTypes)

    data.setNetworkId(networkId.selectedItemId.toInt())
    data.setMinimumActivity(minimumActivity.selectedItemId.toInt())

    if (old != null) {
      data.initSetBufferList(old.initBufferList())
      data.initSetTemporarilyRemovedBuffers(old.initTemporarilyRemovedBuffers())
      data.initSetRemovedBuffers(old.initRemovedBuffers())
    }
  }
}
