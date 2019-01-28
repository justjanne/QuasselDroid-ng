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
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.quassel.syncables.Network
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
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.missingfeatures.MissingFeature
import de.kuschku.quasseldroid.util.missingfeatures.MissingFeaturesDialog
import de.kuschku.quasseldroid.util.missingfeatures.RequiredFeatures
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.BannerView
import io.reactivex.Observable

class CoreSettingsFragment : ServiceBoundFragment() {
  @BindView(R.id.feature_context_missing)
  lateinit var featureContextMissing: BannerView

  @BindView(R.id.coreinfo)
  lateinit var coreinfo: View

  @BindView(R.id.passwordchange)
  lateinit var passwordchange: View

  @BindView(R.id.networks)
  lateinit var networks: RecyclerView

  @BindView(R.id.new_network)
  lateinit var newNetwork: Button

  @BindView(R.id.identities)
  lateinit var identities: RecyclerView

  @BindView(R.id.new_identity)
  lateinit var newIdentity: Button

  @BindView(R.id.chatlists)
  lateinit var chatlists: RecyclerView

  @BindView(R.id.new_chatlist)
  lateinit var newChatlist: Button

  @BindView(R.id.ignorelist)
  lateinit var ignorelist: View

  @BindView(R.id.highlightlist)
  lateinit var highlightlist: View

  @BindView(R.id.aliaslist)
  lateinit var aliaslist: View

  @BindView(R.id.networkconfig)
  lateinit var networkconfig: View

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_list, container, false)
    ButterKnife.bind(this, view)

    val networkAdapter = SettingsItemAdapter {
      NetworkEditActivity.launch(requireContext(), network = it)
    }

    val identityAdapter = SettingsItemAdapter {
      IdentityEditActivity.launch(requireContext(), identity = it)
    }

    val chatListAdapter = SettingsItemAdapter {
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

    viewModel.networks.switchMap {
      if (it.isEmpty()) {
        Observable.just(emptyList())
      } else {
        combineLatest(it.values.map(Network::liveNetworkInfo)).map {
          it.map {
            SettingsItem(it.networkId, it.networkName)
          }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, SettingsItem::name))
        }
      }
    }.toLiveData().observe(this, Observer {
      networkAdapter.submitList(it.orEmpty())
    })

    identities.adapter = identityAdapter
    identities.layoutManager = LinearLayoutManager(context)
    identities.addItemDecoration(itemDecoration)
    ViewCompat.setNestedScrollingEnabled(identities, false)

    viewModel.identities.switchMap {
      if (it.isEmpty()) {
        Observable.just(emptyList())
      } else {
        combineLatest(it.values.map(Identity::liveUpdates)).map {
          it.map {
            SettingsItem(it.id(), it.identityName() ?: "")
          }.sortedBy(SettingsItem::name)
        }
      }
    }.toLiveData().observe(this, Observer {
      identityAdapter.submitList(it.orEmpty())
    })

    chatlists.adapter = chatListAdapter
    chatlists.layoutManager = LinearLayoutManager(context)
    chatlists.addItemDecoration(itemDecoration)
    ViewCompat.setNestedScrollingEnabled(chatlists, false)

    viewModel.bufferViewConfigMap.switchMap {
      combineLatest(it.values.map(BufferViewConfig::liveUpdates)).map {
        it.map {
          SettingsItem(it.bufferViewId(), it.bufferViewName())
        }.sortedBy(SettingsItem::name)
      }
    }.toLiveData().observe(this, Observer {
      chatListAdapter.submitList(it.orEmpty())
    })

    var missingFeatureList: List<MissingFeature> = emptyList()
    viewModel.negotiatedFeatures.toLiveData().observe(this, Observer { (connected, features) ->
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
