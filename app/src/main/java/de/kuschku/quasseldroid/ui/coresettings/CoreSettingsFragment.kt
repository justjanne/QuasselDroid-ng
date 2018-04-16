package de.kuschku.quasseldroid.ui.coresettings

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistCreateActivity
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistEditActivity
import de.kuschku.quasseldroid.ui.coresettings.identity.IdentityCreateActivity
import de.kuschku.quasseldroid.ui.coresettings.identity.IdentityEditActivity
import de.kuschku.quasseldroid.ui.coresettings.ignorelist.IgnoreListActivity
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkCreateActivity
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkEditActivity
import de.kuschku.quasseldroid.ui.coresettings.networkconfig.NetworkConfigActivity
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment

class CoreSettingsFragment : ServiceBoundFragment() {
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

    networks.adapter = networkAdapter
    networks.layoutManager = LinearLayoutManager(context)
    networks.addItemDecoration(itemDecoration)
    ViewCompat.setNestedScrollingEnabled(networks, false)

    viewModel.networks.switchMap {
      combineLatest(it.values.map(Network::liveNetworkInfo)).map {
        it.map {
          SettingsItem(it.networkId, it.networkName)
        }.sortedBy(SettingsItem::name)
      }
    }.toLiveData().observe(this, Observer {
      networkAdapter.submitList(it.orEmpty())
    })

    identities.adapter = identityAdapter
    identities.layoutManager = LinearLayoutManager(context)
    identities.addItemDecoration(itemDecoration)
    ViewCompat.setNestedScrollingEnabled(identities, false)

    viewModel.identities.switchMap {
      combineLatest(it.values.map(Identity::liveUpdates)).map {
        it.map {
          SettingsItem(it.id(), it.identityName() ?: "")
        }.sortedBy(SettingsItem::name)
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

    networkconfig.setOnClickListener {
      NetworkConfigActivity.launch(requireContext())
    }

    ignorelist.setOnClickListener {
      IgnoreListActivity.launch(requireContext())
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
