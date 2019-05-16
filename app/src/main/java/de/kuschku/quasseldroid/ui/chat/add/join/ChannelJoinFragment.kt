/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.chat.add.NetworkAdapter
import de.kuschku.quasseldroid.ui.chat.add.NetworkItem
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

  private var hasSelectedNetwork = false
  private var networkId = NetworkId(0)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.add_join, container, false)
    ButterKnife.bind(this, view)

    networkId = NetworkId(
      savedInstanceState?.getInt("network_id", 0)
      ?: arguments?.getInt("network_id", 0)
      ?: 0
    )

    val networkAdapter = NetworkAdapter()
    network.adapter = networkAdapter

    network.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {
        networkId = NetworkId(0)
      }

      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        networkId = networkAdapter.getItem(position)?.id
                    ?: NetworkId(0)
        hasSelectedNetwork = true
      }
    }

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
              hasSelectedNetwork = true
            }
          }
          hasSetNetwork = true
        }
      }
    })

    join.setOnClickListener {
      join.setText(R.string.label_saving)
      join.isEnabled = false

      val selectedNetworkId = NetworkId(network.selectedItemId.toInt())
      val channelName = name.text.toString().trim()

      activity?.let {
        it.finish()
        ChatActivity.launch(
          it,
          networkId = selectedNetworkId,
          channel = channelName,
          forceJoin = true
        )
      }
    }

    return view
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    if (networkId.isValidId() && hasSelectedNetwork) {
      outState.putInt("network_id", networkId.id)
    }
  }
}
