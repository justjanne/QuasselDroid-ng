/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
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

package de.kuschku.libquassel.connection

import java.security.GeneralSecurityException
import java.security.cert.X509Certificate

sealed class QuasselSecurityException(
  val certificateChain: Array<out X509Certificate>?,
  cause: Throwable
) : GeneralSecurityException(cause) {
  class Certificate(
    certificateChain: Array<out X509Certificate>?,
    cause: Exception
  ) : QuasselSecurityException(certificateChain, cause)

  class Hostname(
    certificateChain: Array<out X509Certificate>?,
    val address: SocketAddress,
    cause: Exception
  ) : QuasselSecurityException(certificateChain, cause)
}
