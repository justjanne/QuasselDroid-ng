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

package de.kuschku.quasseldroid.ui.coresettings.chatlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.flag.minus
import de.kuschku.libquassel.util.flag.plus
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.safeSwitchMap
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.defaults.Defaults
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.ui.settings.fragment.Changeable
import de.kuschku.quasseldroid.util.ui.settings.fragment.Savable
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import javax.inject.Inject

abstract class ChatListBaseFragment(private val initDefault: Boolean) :
  ServiceBoundSettingsFragment(), Savable, Changeable {
  lateinit var bufferViewName: EditText
  lateinit var showSearch: SwitchCompat
  lateinit var sortAlphabetically: SwitchCompat
  lateinit var addNewBuffersAutomatically: SwitchCompat
  lateinit var networkId: Spinner
  lateinit var showStatusBuffer: SwitchCompat
  lateinit var showChannels: SwitchCompat
  lateinit var showQueries: SwitchCompat
  lateinit var minimumActivity: Spinner
  lateinit var hideInactiveBuffers: SwitchCompat
  lateinit var hideInactiveNetworks: SwitchCompat

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  protected var chatlist: Pair<BufferViewConfig?, BufferViewConfig>? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_chatlist, container, false)
    this.bufferViewName = view.findViewById(R.id.buffer_view_name)
    this.showSearch = view.findViewById(R.id.show_search)
    this.sortAlphabetically = view.findViewById(R.id.sort_alphabetically)
    this.addNewBuffersAutomatically = view.findViewById(R.id.add_new_buffers_automatically)
    this.networkId = view.findViewById(R.id.network_id)
    this.showStatusBuffer = view.findViewById(R.id.show_status_buffer)
    this.showChannels = view.findViewById(R.id.show_channels)
    this.showQueries = view.findViewById(R.id.show_queries)
    this.minimumActivity = view.findViewById(R.id.minimum_activity)
    this.hideInactiveBuffers = view.findViewById(R.id.hide_inactive_buffers)
    this.hideInactiveNetworks = view.findViewById(R.id.hide_inactive_networks)

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

    val networkAdapter = NetworkAdapter(R.string.settings_chatlist_network_all)
    networkId.adapter = networkAdapter

    modelHelper.networks.safeSwitchMap {
      combineLatest(it.values.map(Network::liveNetworkInfo)).map {
        it.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, INetwork.NetworkInfo::networkName))
      }
    }.toLiveData().observe(viewLifecycleOwner, Observer {
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

    if (initDefault) {
      modelHelper.connectedSession
        .filter(Optional<ISession>::isPresent)
        .map(Optional<ISession>::get)
        .firstElement()
        .toLiveData().observe(viewLifecycleOwner, Observer {
          it?.let {
            update(Defaults.bufferViewConfig(requireContext(), it.proxy),
                   minimumActivityAdapter,
                   networkAdapter)
          }
        })
    } else {
      modelHelper.bufferViewConfigMap.map { Optional.ofNullable(it[chatlistId]) }
        .filter(Optional<BufferViewConfig>::isPresent)
        .map(Optional<BufferViewConfig>::get)
        .firstElement()
        .toLiveData().observe(viewLifecycleOwner, Observer {
          it?.let {
            update(it, minimumActivityAdapter, networkAdapter)
          }
        })
    }

    networkId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {
        showStatusBuffer.isChecked = true
        showStatusBuffer.isEnabled = false
      }

      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (NetworkId(id.toInt()).isValidId()) {
          showStatusBuffer.isEnabled = true
        } else {
          showStatusBuffer.isChecked = true
          showStatusBuffer.isEnabled = false
        }
      }
    }

    return view
  }

  private fun update(it: BufferViewConfig,
                     minimumActivityAdapter: MinimumActivityAdapter,
                     networkAdapter: NetworkAdapter) {
    if (this.chatlist == null) {
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
  }

  override fun hasChanged() = chatlist?.let { (it, data) ->
    applyChanges(data, it)
    it == null || !data.isEqual(it)
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

    data.setNetworkId(NetworkId(networkId.selectedItemId.toInt()))
    data.setMinimumActivity(minimumActivity.selectedItemId.toInt())

    if (old != null) {
      data.initSetBufferList(old.initBufferList())
      data.initSetTemporarilyRemovedBuffers(old.initTemporarilyRemovedBuffers())
      data.initSetRemovedBuffers(old.initRemovedBuffers())
    }
  }
}
