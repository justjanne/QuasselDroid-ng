package de.kuschku.quasseldroid.ui.coresettings

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatListActivity
import de.kuschku.quasseldroid.ui.coresettings.identity.IdentityActivity
import de.kuschku.quasseldroid.ui.coresettings.networkconfig.NetworkConfigActivity
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment

class CoreSettingsFragment : ServiceBoundFragment() {
  @BindView(R.id.networks)
  lateinit var networks: RecyclerView

  @BindView(R.id.identities)
  lateinit var identities: RecyclerView

  @BindView(R.id.chatlists)
  lateinit var chatlists: RecyclerView

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
      /*
      val intent = Intent(requireContext(), NetworkActivity::class.java)
      intent.putExtra("network", it)
      startActivity(intent)
      */
    }

    val identityAdapter = SettingsItemAdapter {
      val intent = Intent(requireContext(), IdentityActivity::class.java)
      intent.putExtra("identity", it)
      startActivity(intent)
    }

    val chatListAdapter = SettingsItemAdapter {
      val intent = Intent(requireContext(), ChatListActivity::class.java)
      intent.putExtra("chatlist", it)
      startActivity(intent)
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
      startActivity(Intent(requireContext(), NetworkConfigActivity::class.java))
    }

    return view
  }
}
