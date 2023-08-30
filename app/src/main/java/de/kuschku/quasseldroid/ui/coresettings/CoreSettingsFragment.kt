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

package de.kuschku.quasseldroid.ui.coresettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.safeSwitchMap
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.aliaslist.AliasListActivity
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistCreateActivity
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistEditActivity
import de.kuschku.quasseldroid.ui.coresettings.highlightlist.HighlightListActivity
import de.kuschku.quasseldroid.ui.coresettings.identity.IdentityCreateActivity
import de.kuschku.quasseldroid.ui.coresettings.identity.IdentityEditActivity
import de.kuschku.quasseldroid.ui.coresettings.ignorelist.IgnoreListActivity
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkCreateActivity
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkEditActivity
import de.kuschku.quasseldroid.ui.coresettings.networkconfig.NetworkConfigActivity
import de.kuschku.quasseldroid.ui.coresettings.passwordchange.PasswordChangeActivity
import de.kuschku.quasseldroid.ui.info.core.CoreInfoActivity
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.missingfeatures.MissingFeature
import de.kuschku.quasseldroid.util.missingfeatures.MissingFeaturesDialog
import de.kuschku.quasseldroid.util.missingfeatures.RequiredFeatures
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.view.BannerView
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import io.reactivex.Observable
import javax.inject.Inject

class CoreSettingsFragment : ServiceBoundFragment() {
  lateinit var featureContextMissing: BannerView
  lateinit var coreinfo: View
  lateinit var passwordchange: View
  lateinit var networks: RecyclerView
  lateinit var newNetwork: Button
  lateinit var identities: RecyclerView
  lateinit var newIdentity: Button
  lateinit var chatlists: RecyclerView
  lateinit var newChatlist: Button
  lateinit var ignorelist: View
  lateinit var highlightlist: View
  lateinit var aliaslist: View
  lateinit var networkconfig: View

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_list, container, false)
    this.featureContextMissing = view.findViewById(R.id.feature_context_missing)
    this.coreinfo = view.findViewById(R.id.coreinfo)
    this.passwordchange = view.findViewById(R.id.passwordchange)
    this.networks = view.findViewById(R.id.networks)
    this.newNetwork = view.findViewById(R.id.new_network)
    this.identities = view.findViewById(R.id.identities)
    this.newIdentity = view.findViewById(R.id.new_identity)
    this.chatlists = view.findViewById(R.id.chatlists)
    this.newChatlist = view.findViewById(R.id.new_chatlist)
    this.ignorelist = view.findViewById(R.id.ignorelist)
    this.highlightlist = view.findViewById(R.id.highlightlist)
    this.aliaslist = view.findViewById(R.id.aliaslist)
    this.networkconfig = view.findViewById(R.id.networkconfig)

    val networkAdapter = SettingsItemAdapter<NetworkId> {
      NetworkEditActivity.launch(requireContext(), network = it)
    }

    val identityAdapter = SettingsItemAdapter<IdentityId> {
      IdentityEditActivity.launch(requireContext(), identity = it)
    }

    val chatListAdapter = SettingsItemAdapter<Int> {
      ChatlistEditActivity.launch(requireContext(), chatlist = it)
    }

    val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)

    coreinfo.setOnClickListener {
      CoreInfoActivity.launch(requireContext())
    }

    passwordchange.setOnClickListener {
      PasswordChangeActivity.launch(requireContext())
    }

    networks.adapter = networkAdapter
    networks.layoutManager = LinearLayoutManager(context)
    networks.addItemDecoration(itemDecoration)
    ViewCompat.setNestedScrollingEnabled(networks, false)

    modelHelper.networks.safeSwitchMap {
      if (it.isEmpty()) {
        Observable.just(emptyList())
      } else {
        combineLatest(it.values.map(Network::liveNetworkInfo)).map {
          it.map {
            SettingsItem(it.networkId, it.networkName)
          }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, SettingsItem<NetworkId>::name))
        }
      }
    }.toLiveData().observe(viewLifecycleOwner, Observer {
      networkAdapter.submitList(it.orEmpty())
    })

    identities.adapter = identityAdapter
    identities.layoutManager = LinearLayoutManager(context)
    identities.addItemDecoration(itemDecoration)
    ViewCompat.setNestedScrollingEnabled(identities, false)

    modelHelper.identities.safeSwitchMap {
      if (it.isEmpty()) {
        Observable.just(emptyList())
      } else {
        combineLatest(it.values.map(Identity::liveUpdates)).map {
          it.map {
            SettingsItem(it.id(), it.identityName() ?: "")
          }.sortedBy(SettingsItem<IdentityId>::name)
        }
      }
    }.toLiveData().observe(viewLifecycleOwner, Observer {
      identityAdapter.submitList(it.orEmpty())
    })

    chatlists.adapter = chatListAdapter
    chatlists.layoutManager = LinearLayoutManager(context)
    chatlists.addItemDecoration(itemDecoration)
    ViewCompat.setNestedScrollingEnabled(chatlists, false)

    modelHelper.bufferViewConfigMap.safeSwitchMap {
      combineLatest(it.values.map(BufferViewConfig::liveUpdates)).map {
        it.map {
          SettingsItem(it.bufferViewId(), it.bufferViewName())
        }.sortedBy(SettingsItem<Int>::name)
      }
    }.toLiveData().observe(viewLifecycleOwner, Observer {
      chatListAdapter.submitList(it.orEmpty())
    })

    var missingFeatureList: List<MissingFeature> = emptyList()
    modelHelper.negotiatedFeatures.toLiveData().observe(viewLifecycleOwner,
                                                        Observer { (connected, features) ->
                                                          missingFeatureList = RequiredFeatures.features.filter {
                                                            it.feature !in features.enabledFeatures
                                                          }
                                                          featureContextMissing.visibleIf(connected && missingFeatureList.isNotEmpty())
                                                        })

    featureContextMissing.setOnClickListener {
      MissingFeaturesDialog.Builder(requireActivity())
        .missingFeatures(missingFeatureList)
        .readOnly(true)
        .show()
    }

    networkconfig.setOnClickListener {
      NetworkConfigActivity.launch(requireContext())
    }

    ignorelist.setOnClickListener {
      IgnoreListActivity.launch(requireContext())
    }

    highlightlist.setOnClickListener {
      HighlightListActivity.launch(requireContext())
    }

    aliaslist.setOnClickListener {
      AliasListActivity.launch(requireContext())
    }

    newNetwork.setOnClickListener {
      NetworkCreateActivity.launch(requireContext())
    }

    newIdentity.setOnClickListener {
      IdentityCreateActivity.launch(requireContext())
    }

    newChatlist.setOnClickListener {
      ChatlistCreateActivity.launch(requireContext())
    }

    return view
  }
}
