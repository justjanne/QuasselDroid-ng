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

package de.kuschku.quasseldroid.ui.info.core

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.ssl.X509Helper
import de.kuschku.libquassel.ssl.commonName
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.info.certificate.CertificateInfoActivity
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.missingfeatures.MissingFeature
import de.kuschku.quasseldroid.util.missingfeatures.MissingFeaturesDialog
import de.kuschku.quasseldroid.util.missingfeatures.RequiredFeatures
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.BetterLinkMovementMethod
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import javax.inject.Inject

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

  @BindView(R.id.secure_certificate)
  lateinit var secureCertificate: TextView

  @BindView(R.id.secure_certificate_icon)
  lateinit var secureCertificateIcon: ImageView

  @BindView(R.id.secure_connection_protocol)
  lateinit var secureConnectionProtocol: TextView

  @BindView(R.id.secure_connection_ciphersuite)
  lateinit var secureConnectionCiphersuite: TextView

  @BindView(R.id.secure_details)
  lateinit var secureDetails: Button

  @BindView(R.id.clients_title)
  lateinit var clientsTitle: View

  @BindView(R.id.clients)
  lateinit var clients: RecyclerView

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
  private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

  private val movementMethod = BetterLinkMovementMethod.newInstance()

  private val cipherSuiteRegex = Regex("TLS_(.*)_WITH_(.*)")

  init {
    movementMethod.setOnLinkLongClickListener(LinkLongClickMenuHelper())
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.info_core, container, false)
    ButterKnife.bind(this, view)

    var missingFeatureList: List<MissingFeature> = emptyList()
    combineLatest(modelHelper.coreInfo, modelHelper.coreFeatures).toLiveData()
      .observe(this, Observer {
        val data = it?.first?.orNull()
        val connected = it?.second?.first
                        ?: false
        val features = it?.second?.second
                       ?: QuasselFeatures.empty()

        version.text = data?.quasselVersion.let(Html::fromHtml)
        val versionTime = data?.quasselBuildDate?.toLongOrNull()
        val formattedVersionTime = if (versionTime != null)
          dateFormatter.format(Instant.ofEpochSecond(versionTime).atZone(ZoneId.systemDefault()))
        else
          data?.quasselBuildDate?.let(Html::fromHtml)
        versionDate.text = formattedVersionTime
        missingFeatureList = RequiredFeatures.features.filter {
          it.feature !in features.enabledFeatures
        }
        missingFeatures.visibleIf(connected && missingFeatureList.isNotEmpty())

        val startTime = data?.startTime?.atZone(ZoneId.systemDefault())?.let(dateTimeFormatter::format)
        uptime.text = requireContext().getString(R.string.label_core_online_since,
                                                 startTime.toString())
        uptimeContainer.visibleIf(startTime != null)
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

    secureDetails.setOnClickListener {
      CertificateInfoActivity.launch(it.context)
    }

    modelHelper.sslSession.toLiveData().observe(this, Observer {
      val certificateChain = it?.orNull()?.peerCertificateChain?.map(X509Helper::convert).orEmpty()
      val leafCertificate = certificateChain.firstOrNull()
      if (leafCertificate != null) {
        secureCertificate.text = requireContext().getString(
          R.string.label_core_connection_verified_by,
          leafCertificate.issuerX500Principal.commonName
        )
        if (leafCertificate.isValid) {
          secureCertificateIcon.setImageDrawable(secure)
        } else {
          secureCertificateIcon.setImageDrawable(partiallySecure)
        }
        secureDetails.visibility = View.VISIBLE
      } else {
        secureCertificate.text = context?.getString(R.string.label_core_connection_insecure)
        secureCertificateIcon.setImageDrawable(insecure)
        secureDetails.visibility = View.GONE
      }

      val (keyExchangeMechanism, cipherSuite) = it.orNull()?.cipherSuite?.let { cipherSuite ->
        cipherSuiteRegex.matchEntire(cipherSuite)?.destructured
      }?.let { (keyExchangeMechanism, cipherSuite) ->
        Pair(keyExchangeMechanism, cipherSuite)
      } ?: Pair(null, null)

      val protocol = it.orNull()?.protocol
      if (cipherSuite != null && keyExchangeMechanism != null && protocol != null) {
        secureConnectionProtocol.text = context?.getString(R.string.label_core_connection_protocol,
                                                           protocol)
        secureConnectionCiphersuite.text = context?.getString(R.string.label_core_connection_ciphersuite,
                                                              cipherSuite,
                                                              keyExchangeMechanism)
        secureConnectionProtocol.visibility = View.VISIBLE
        secureConnectionCiphersuite.visibility = View.VISIBLE
      } else {
        secureConnectionProtocol.visibility = View.GONE
        secureConnectionCiphersuite.visibility = View.GONE
      }
    })

    clients.layoutManager = LinearLayoutManager(requireContext())
    val adapter = ClientAdapter()
    adapter.setDisconnectListener {
      val sessionOptional = modelHelper.session.value
      val session = sessionOptional?.orNull()
      val rpcHandler = session?.rpcHandler
      rpcHandler?.requestKickClient(it)
    }
    clients.adapter = adapter
    modelHelper.coreInfoClients.toLiveData().observe(this, Observer {
      clientsTitle.visibleIf(it?.isNotEmpty() == true)
      adapter.submitList(it)
    })

    return view
  }
}
