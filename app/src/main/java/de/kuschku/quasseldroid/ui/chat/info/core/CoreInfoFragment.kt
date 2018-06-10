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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ssl.X509Helper
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.missingfeatures.MissingFeature
import de.kuschku.quasseldroid.util.missingfeatures.MissingFeaturesDialog
import de.kuschku.quasseldroid.util.missingfeatures.RequiredFeatures
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.BetterLinkMovementMethod
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class CoreInfoFragment : ServiceBoundFragment() {

  @BindView(R.id.version)
  lateinit var version: TextView

  @BindView(R.id.version_date)
  lateinit var versionDate: TextView

  @BindView(R.id.missing_features)
  lateinit var missingFeatures: Button

  @BindView(R.id.uptime_container)
  lateinit var uptimeContainer: View

  @BindView(R.id.uptime)
  lateinit var uptime: TextView

  @BindView(R.id.secure)
  lateinit var secureText: TextView

  @BindView(R.id.secure_icon)
  lateinit var secureIcon: ImageView

  @BindView(R.id.clients_title)
  lateinit var clientsTitle: View

  @BindView(R.id.clients)
  lateinit var clients: RecyclerView

  private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

  private val movementMethod = BetterLinkMovementMethod.newInstance()

  init {
    movementMethod.setOnLinkLongClickListener(LinkLongClickMenuHelper())
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_info_core, container, false)
    ButterKnife.bind(this, view)

    var missingFeatureList: List<MissingFeature> = emptyList()
    viewModel.coreInfo.toLiveData().observe(this, Observer {
      it?.orNull().let { data ->
        version.text = data?.quasselVersion?.let(Html::fromHtml)
        versionDate.text = data?.quasselBuildDate?.let(Html::fromHtml)

        val features = viewModel.session.value?.orNull()?.features?.core
                       ?: QuasselFeatures.empty()
        missingFeatureList = RequiredFeatures.features.filter {
          it.feature !in features.enabledFeatures
        }
        missingFeatures.visibleIf(missingFeatureList.isNotEmpty())

        val startTime = data?.startTime?.atZone(ZoneId.systemDefault())?.let(dateTimeFormatter::format)
        uptime.text = requireContext().getString(R.string.label_core_online_since,
                                                 startTime.toString())
        uptimeContainer.visibleIf(startTime != null)
      }
    })
    missingFeatures.setOnClickListener {
      MissingFeaturesDialog.Builder(requireActivity())
        .missingFeatures(missingFeatureList)
        .readOnly(true)
        .show()
    }
    version.movementMethod = movementMethod
    versionDate.movementMethod = movementMethod

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
                       ?: requireContext().getString(R.string.label_core_connection_verified_by_unknown)

      if (sslSession == null) {
        secureText.text = requireContext().getString(R.string.label_core_connection_insecure)
        secureIcon.setImageDrawable(insecure)
      } else {
        secureText.text = requireContext().getString(
          R.string.label_core_connection_verified_by, issuerName
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
      clientsTitle.visibleIf(it?.isNotEmpty() == true)
      adapter.submitList(it)
    })

    return view
  }
}
