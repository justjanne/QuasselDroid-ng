/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat.info.core

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ssl.X509Helper
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment

class CoreInfoFragment : ServiceBoundFragment() {

  @BindView(R.id.version)
  lateinit var version: TextView

  @BindView(R.id.version_date)
  lateinit var versionDate: TextView

  @BindView(R.id.uptime_container)
  lateinit var uptimeContainer: View

  @BindView(R.id.uptime)
  lateinit var uptime: TextView

  @BindView(R.id.secure_container)
  lateinit var secureContainer: View

  @BindView(R.id.secure)
  lateinit var secureText: TextView

  @BindView(R.id.secure_icon)
  lateinit var secureIcon: ImageView

  @BindView(R.id.clients)
  lateinit var clients: RecyclerView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_info_core, container, false)
    ButterKnife.bind(this, view)

    viewModel.coreInfo.toLiveData().observe(this, Observer {
      it?.orNull().let {
        version.text = it?.quasselVersion?.let(Html::fromHtml)
        versionDate.text = it?.quasselBuildDate?.let(Html::fromHtml)

        val startTime = it?.startTime?.toString()
        uptime.text = requireContext().getString(R.string.label_core_online_since,
                                                 startTime.toString())
        uptimeContainer.visibleIf(startTime != null)
      }
    })

    val secure = requireContext().getVectorDrawableCompat(R.drawable.ic_lock)?.mutate()
    val partiallySecure = requireContext().getVectorDrawableCompat(R.drawable.ic_lock)?.mutate()
    val insecure = requireContext().getVectorDrawableCompat(R.drawable.ic_lock_open)?.mutate()
    requireContext().theme.styledAttributes(
      R.attr.colorTintSecure,
      R.attr.colorTintPartiallySecure,
      R.attr.colorTintInsecure
    ) {
      secure?.tint(getColor(0, 0))
      partiallySecure?.tint(getColor(1, 0))
      insecure?.tint(getColor(2, 0))
    }

    viewModel.sslSession.toLiveData().observe(this, Observer {
      val sslSession = it?.orNull()
      val leafCertificate = sslSession?.peerCertificateChain?.firstOrNull()
      val issuerName = leafCertificate?.issuerDN?.name?.let(X509Helper::commonName)

      if (sslSession == null) {
        secureText.text = requireContext().getString(R.string.label_core_connection_insecure)
        secureIcon.setImageDrawable(insecure)
      } else {
        secureText.text = requireContext().getString(
          R.string.label_core_connection_verified_by,
          issuerName
          ?: requireContext().getString(R.string.label_core_connection_verified_by_unknown)
        )
        secureIcon.setImageDrawable(secure)
      }
    })

    clients.layoutManager = LinearLayoutManager(requireContext())
    val adapter = ClientAdapter()
    adapter.setDisconnectListener {
      val sessionOptional = viewModel.session.value
      val session = sessionOptional?.orNull()
      val rpcHandler = session?.rpcHandler
      rpcHandler?.requestKickClient(it)
    }
    clients.adapter = adapter
    viewModel.coreInfoClients.toLiveData().observe(this, Observer {
      adapter.submitList(it)
    })

    return view
  }
}
