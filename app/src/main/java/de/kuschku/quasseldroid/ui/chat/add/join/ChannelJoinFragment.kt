/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.quasseldroid.ui.chat.add.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.chat.add.NetworkAdapter
import de.kuschku.quasseldroid.ui.chat.add.NetworkItem
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.viewmodel.helper.QuasselViewModelHelper
import javax.inject.Inject

class ChannelJoinFragment : ServiceBoundFragment() {
  @BindView(R.id.network)
  lateinit var network: AppCompatSpinner

  @BindView(R.id.name)
  lateinit var name: EditText

  @BindView(R.id.join)
  lateinit var join: Button

  @Inject
  lateinit var modelHelper: QuasselViewModelHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.add_join, container, false)
    ButterKnife.bind(this, view)

    val networkId = NetworkId(arguments?.getInt("network_id", 0) ?: 0)

    val networkAdapter = NetworkAdapter()
    network.adapter = networkAdapter

    var hasSetNetwork = false
    modelHelper.networks.switchMap {
      combineLatest(it.values.map(Network::liveNetworkInfo)).map {
        it.map {
          NetworkItem(it.networkId, it.networkName)
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, NetworkItem::name))
      }
    }.toLiveData().observe(this, Observer {
      if (it != null) {
        networkAdapter.submitList(it)
        if (!hasSetNetwork && networkId.isValidId() && it.isNotEmpty()) {
          network.post {
            val index = networkAdapter.indexOf(networkId)
            if (index != null) {
              network.setSelection(index)
            }
          }
          hasSetNetwork = true
        }
      }
    })

    join.setOnClickListener {
      join.setText(R.string.label_saving)
      join.isEnabled = false

      val networkId = NetworkId(network.selectedItemId.toInt())
      val channelName = name.text.toString().trim()

      modelHelper.bufferSyncer.value?.orNull()?.let { bufferSyncer ->
        bufferSyncer.find(
          networkId = networkId,
          type = Buffer_Type.of(Buffer_Type.StatusBuffer)
        )?.let { statusBuffer ->
          modelHelper.session.value?.orNull()?.rpcHandler?.apply {
            sendInput(statusBuffer, "/join $channelName")
          }
        }
      }

      activity?.let {
        it.finish()
        ChatActivity.launch(it, networkId = networkId, channel = channelName)
      }
    }

    return view
  }
}
