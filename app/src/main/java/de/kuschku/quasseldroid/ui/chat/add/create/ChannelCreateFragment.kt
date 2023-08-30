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

package de.kuschku.quasseldroid.ui.chat.add.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.nullIf
import de.kuschku.libquassel.util.helper.safeSwitchMap
import de.kuschku.libquassel.util.helper.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.chat.add.NetworkAdapter
import de.kuschku.quasseldroid.ui.chat.add.NetworkItem
import de.kuschku.quasseldroid.util.helper.setDependent
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment
import de.kuschku.quasseldroid.viewmodel.helper.QuasselViewModelHelper
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ChannelCreateFragment : ServiceBoundSettingsFragment() {
  lateinit var network: AppCompatSpinner
  lateinit var name: EditText
  lateinit var hidden: SwitchCompat
  lateinit var inviteOnly: SwitchCompat
  lateinit var passwordProtected: SwitchCompat
  lateinit var passwordGroup: ViewGroup
  lateinit var password: EditText
  lateinit var save: Button

  @Inject
  lateinit var modelHelper: QuasselViewModelHelper

  private var hasSelectedNetwork = false
  private var networkId = NetworkId(0)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.add_create, container, false)
    this.network = view.findViewById(R.id.network)
    this.name = view.findViewById(R.id.name)
    this.hidden = view.findViewById(R.id.hidden)
    this.inviteOnly = view.findViewById(R.id.invite_only)
    this.passwordProtected = view.findViewById(R.id.password_protected)
    this.passwordGroup = view.findViewById(R.id.password_group)
    this.password = view.findViewById(R.id.password)
    this.save = view.findViewById(R.id.save)

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
    modelHelper.networks.safeSwitchMap {
      combineLatest(it.values.map(Network::liveNetworkInfo)).map {
        it.map {
          NetworkItem(it.networkId, it.networkName)
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, NetworkItem::name))
      }
    }.toLiveData().observe(viewLifecycleOwner, Observer {
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

    passwordProtected.setDependent(passwordGroup)

    save.setOnClickListener {
      save.setText(R.string.label_saving)
      save.isEnabled = false

      val selectedNetworkId = NetworkId(network.selectedItemId.toInt())
      val channelName = name.text.toString().trim()

      val isInviteOnly = inviteOnly.isChecked
      val isHidden = hidden.isChecked
      val isPasswordProtected = passwordProtected.isChecked
      val channelPassword = password.text.toString().trim()

      modelHelper.bufferSyncer.value?.orNull()?.let { bufferSyncer ->
        val existingBuffer = bufferSyncer.find(
          networkId = selectedNetworkId,
          type = Buffer_Type.of(Buffer_Type.ChannelBuffer),
          bufferName = channelName
        )
        val existingChannel = modelHelper.networks.value?.get(selectedNetworkId)?.ircChannel(
          channelName)
          .nullIf { it == IrcChannel.NULL }
        if (existingBuffer != null) {
          if (existingChannel == null) {
            bufferSyncer.find(
              networkId = selectedNetworkId,
              type = Buffer_Type.of(Buffer_Type.StatusBuffer)
            )?.let { statusBuffer ->
              modelHelper.connectedSession.value?.orNull()?.rpcHandler?.apply {
                sendInput(statusBuffer, "/join $channelName")
              }
            }
          }

          activity?.let {
            it.finish()
            ChatActivity.launch(it,
                                bufferId = existingBuffer.bufferId
            )
          }
        } else {
          bufferSyncer.find(
            networkId = selectedNetworkId,
            type = Buffer_Type.of(Buffer_Type.StatusBuffer)
          )?.let { statusBuffer ->
            modelHelper.connectedSession.value?.orNull()?.rpcHandler?.apply {
              sendInput(statusBuffer, "/join $channelName")
              modelHelper.networks.safeSwitchMap {
                it[selectedNetworkId]?.liveIrcChannel(channelName)
                  ?: Observable.empty()
              }.toLiveData().observe(viewLifecycleOwner) {
                if (it.ircUsers().size <= 1) {
                  if (isInviteOnly) {
                    sendInput(statusBuffer, "/mode $channelName +i")
                  }

                  if (isHidden) {
                    sendInput(statusBuffer, "/mode $channelName +s")
                  }

                  if (isPasswordProtected) {
                    sendInput(statusBuffer, "/mode $channelName +k $channelPassword")
                  }
                }

                activity?.let {
                  it.finish()
                  ChatActivity.launch(it,
                    networkId = selectedNetworkId,
                    channel = channelName
                  )
                }
              }
            }
          }
        }
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
