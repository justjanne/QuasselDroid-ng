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

package de.justjanne.libquassel.client.testutil

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

object TestX509TrustManager : X509TrustManager {
  override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    // FIXME: accept everything
  }

  override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    // FIXME: accept everything
  }

  override fun getAcceptedIssuers(): Array<X509Certificate> {
    // FIXME: accept nothing
    return emptyArray()
  }
}
