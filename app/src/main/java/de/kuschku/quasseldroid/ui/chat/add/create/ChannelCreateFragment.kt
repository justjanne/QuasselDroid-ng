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

package de.kuschku.quasseldroid.ui.chat.add.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.util.helpers.nullIf
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.setDependent
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment
import de.kuschku.quasseldroid.viewmodel.helper.QuasselViewModelHelper
import io.reactivex.Observable
import javax.inject.Inject

class ChannelCreateFragment : ServiceBoundSettingsFragment() {
  @BindView(R.id.network)
  lateinit var network: AppCompatSpinner

  @BindView(R.id.name)
  lateinit var name: EditText

  @BindView(R.id.hidden)
  lateinit var hidden: SwitchCompat

  @BindView(R.id.invite_only)
  lateinit var inviteOnly: SwitchCompat

  @BindView(R.id.password_protected)
  lateinit var passwordProtected: SwitchCompat

  @BindView(R.id.password_group)
  lateinit var passwordGroup: ViewGroup

  @BindView(R.id.password)
  lateinit var password: EditText

  @BindView(R.id.save)
  lateinit var save: Button

  @Inject
  lateinit var modelHelper: QuasselViewModelHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.add_create, container, false)
    ButterKnife.bind(this, view)

    val networkAdapter = NetworkAdapter()
    network.adapter = networkAdapter

    modelHelper.networks.switchMap {
      combineLatest(it.values.map(Network::liveNetworkInfo)).map {
        it.map {
          NetworkItem(
            it.networkId,
            it.networkName
          )
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, NetworkItem::name))
      }
    }.toLiveData().observe(this, Observer {
      if (it != null) {
        networkAdapter.submitList(it)
      }
    })

    passwordProtected.setDependent(passwordGroup)

    save.setOnClickListener {
      save.setText(R.string.label_saving)
      save.isEnabled = false

      val networkId = NetworkId(network.selectedItemId.toInt())
      val channelName = name.text.toString().trim()

      val isInviteOnly = inviteOnly.isChecked
      val isHidden = hidden.isChecked
      val isPasswordProtected = passwordProtected.isChecked
      val channelPassword = password.text.toString().trim()

      modelHelper.bufferSyncer.value?.orNull()?.let { bufferSyncer ->
        val existingBuffer = bufferSyncer.find(
          networkId = networkId,
          type = Buffer_Type.of(Buffer_Type.ChannelBuffer),
          bufferName = channelName
        )
        val existingChannel = modelHelper.networks.value?.get(networkId)?.ircChannel(channelName)
          .nullIf { it == IrcChannel.NULL }
        if (existingBuffer != null) {
          if (existingChannel == null) {
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
            ChatActivity.launch(it,
              bufferId = existingBuffer.bufferId
            )
          }
        } else {
          bufferSyncer.find(
            networkId = networkId,
            type = Buffer_Type.of(Buffer_Type.StatusBuffer)
          )?.let { statusBuffer ->
            modelHelper.session.value?.orNull()?.rpcHandler?.apply {
              sendInput(statusBuffer, "/join $channelName")
              modelHelper.networks.switchMap {
                it[networkId]?.liveIrcChannel(channelName)
                  ?: Observable.empty()
              }.subscribe {
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
                    networkId = networkId,
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
}
