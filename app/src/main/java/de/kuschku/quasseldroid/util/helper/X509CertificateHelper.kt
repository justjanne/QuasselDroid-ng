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

package de.kuschku.quasseldroid.util.helper

import org.apache.commons.codec.digest.DigestUtils
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException
import java.security.cert.X509Certificate

val X509Certificate.isValid: Boolean
  get() = try {
    checkValidity()
    true
  } catch (e: CertificateExpiredException) {
    false
  } catch (e: CertificateNotYetValidException) {
    false
  }

val X509Certificate.fingerprint: String
  get() = DigestUtils.sha1(encoded).joinToString(":") {
    (it.toInt() and 0xff).toString(16)
  }

val javax.security.cert.X509Certificate.fingerprint: String
  get() = DigestUtils.sha1(encoded).joinToString(":") {
    (it.toInt() and 0xff).toString(16)
  }
