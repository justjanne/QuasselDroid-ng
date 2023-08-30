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

package de.kuschku.quasseldroid.ui.info.certificate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import de.kuschku.libquassel.ssl.X509Helper
import de.kuschku.libquassel.ssl.commonName
import de.kuschku.libquassel.ssl.organization
import de.kuschku.libquassel.ssl.organizationalUnit
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.sha1Fingerprint
import de.kuschku.quasseldroid.util.helper.sha256Fingerprint
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import javax.inject.Inject

class CertificateInfoFragment : ServiceBoundSettingsFragment() {
  lateinit var content: View
  lateinit var error: View
  lateinit var subjectCommonNameWrapper: View
  lateinit var subjectCommonName: TextView
  lateinit var subjectHostnamesWrapper: View
  lateinit var subjectHostnames: TextView
  lateinit var subjectOrganizationWrapper: View
  lateinit var subjectOrganization: TextView
  lateinit var subjectOrganizationalUnitWrapper: View
  lateinit var subjectOrganizationalUnit: TextView
  lateinit var issuerCommonNameWrapper: View
  lateinit var issuerCommonName: TextView
  lateinit var issuerOrganizationWrapper: View
  lateinit var issuerOrganization: TextView
  lateinit var issuerOrganizationalUnitWrapper: View
  lateinit var issuerOrganizationalUnit: TextView
  lateinit var notBefore: TextView
  lateinit var notAfter: TextView
  lateinit var fingerprintSha256: TextView
  lateinit var fingerprintSha1: TextView

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.info_certificate, container, false)
    this.content = view.findViewById(R.id.content)
    this.error = view.findViewById(R.id.error)
    this.subjectCommonNameWrapper = view.findViewById(R.id.subject_common_name_wrapper)
    this.subjectCommonName = view.findViewById(R.id.subject_common_name)
    this.subjectHostnamesWrapper = view.findViewById(R.id.subject_hostnames_wrapper)
    this.subjectHostnames = view.findViewById(R.id.subject_hostnames)
    this.subjectOrganizationWrapper = view.findViewById(R.id.subject_organization_wrapper)
    this.subjectOrganization = view.findViewById(R.id.subject_organization)
    this.subjectOrganizationalUnitWrapper = view.findViewById(R.id.subject_organizational_unit_wrapper)
    this.subjectOrganizationalUnit = view.findViewById(R.id.subject_organizational_unit)
    this.issuerCommonNameWrapper = view.findViewById(R.id.issuer_common_name_wrapper)
    this.issuerCommonName = view.findViewById(R.id.issuer_common_name)
    this.issuerOrganizationWrapper = view.findViewById(R.id.issuer_organization_wrapper)
    this.issuerOrganization = view.findViewById(R.id.issuer_organization)
    this.issuerOrganizationalUnitWrapper = view.findViewById(R.id.issuer_organizational_unit_wrapper)
    this.issuerOrganizationalUnit = view.findViewById(R.id.issuer_organizational_unit)
    this.notBefore = view.findViewById(R.id.not_before)
    this.notAfter = view.findViewById(R.id.not_after)
    this.fingerprintSha256 = view.findViewById(R.id.fingerprint_sha256)
    this.fingerprintSha1 = view.findViewById(R.id.fingerprint_sha1)

    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    modelHelper.peerCertificateChain.toLiveData().observe(viewLifecycleOwner, Observer {
      val leafCertificate = it.firstOrNull()
      if (leafCertificate != null) {
        content.visibility = View.VISIBLE
        error.visibility = View.GONE

        subjectCommonName.text = leafCertificate.subjectX500Principal.commonName
        subjectCommonNameWrapper.visibleIf(!subjectCommonName.text.isNullOrBlank())

        subjectHostnames.text = X509Helper.hostnames(leafCertificate).joinToString(", ")
        subjectHostnamesWrapper.visibleIf(!subjectHostnames.text.isNullOrBlank())

        subjectOrganization.text = leafCertificate.subjectX500Principal.organization
        subjectOrganizationWrapper.visibleIf(!subjectOrganization.text.isNullOrBlank())

        subjectOrganizationalUnit.text = leafCertificate.subjectX500Principal.organizationalUnit
        subjectOrganizationalUnitWrapper.visibleIf(!subjectOrganizationalUnit.text.isNullOrBlank())

        issuerCommonName.text = leafCertificate.issuerX500Principal.commonName
        issuerCommonNameWrapper.visibleIf(!issuerCommonName.text.isNullOrBlank())

        issuerOrganization.text = leafCertificate.issuerX500Principal.organization
        issuerOrganizationWrapper.visibleIf(!issuerOrganization.text.isNullOrBlank())

        issuerOrganizationalUnit.text = leafCertificate.issuerX500Principal.organizationalUnit
        issuerOrganizationalUnitWrapper.visibleIf(!issuerOrganizationalUnit.text.isNullOrBlank())

        notBefore.text = dateFormatter.format(
          Instant.ofEpochMilli(leafCertificate.notBefore.time)
            .atZone(ZoneId.systemDefault())
        )
        notAfter.text = dateFormatter.format(
          Instant.ofEpochMilli(leafCertificate.notAfter.time)
            .atZone(ZoneId.systemDefault())
        )

        fingerprintSha256.text = leafCertificate.sha256Fingerprint
        fingerprintSha1.text = leafCertificate.sha1Fingerprint
      } else {
        content.visibility = View.GONE
        error.visibility = View.VISIBLE
      }
    })

    return view
  }
}
