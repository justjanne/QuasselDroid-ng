/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package de.kuschku.quasseldroid.util

import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate as javaCertificate
import javax.security.cert.X509Certificate as javaxCertificate

private val certificateFactory = CertificateFactory.getInstance("X.509")

fun javaxCertificate.toJavaCertificate(): javaCertificate =
  certificateFactory.generateCertificate(ByteArrayInputStream(this.encoded))
    as javaCertificate

fun javaCertificate.toJavaXCertificate(): javaxCertificate =
  javaxCertificate.getInstance(this.encoded)
