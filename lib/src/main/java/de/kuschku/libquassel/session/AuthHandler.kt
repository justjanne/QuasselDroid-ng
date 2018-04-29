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

package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.message.HandshakeMessage

interface AuthHandler {
  fun handle(f: HandshakeMessage.ClientInit) = false
  fun handle(f: HandshakeMessage.ClientInitReject) = false
  fun handle(f: HandshakeMessage.ClientInitAck) = false
  fun handle(f: HandshakeMessage.CoreSetupData) = false
  fun handle(f: HandshakeMessage.CoreSetupReject) = false
  fun handle(f: HandshakeMessage.CoreSetupAck) = false
  fun handle(f: HandshakeMessage.ClientLogin) = false
  fun handle(f: HandshakeMessage.ClientLoginReject) = false
  fun handle(f: HandshakeMessage.ClientLoginAck) = false
  fun handle(f: HandshakeMessage.SessionInit) = false

  fun handle(f: HandshakeMessage): Boolean = when (f) {
    is HandshakeMessage.ClientInit        -> handle(f)
    is HandshakeMessage.ClientInitReject  -> handle(f)
    is HandshakeMessage.ClientInitAck     -> handle(f)
    is HandshakeMessage.CoreSetupData     -> handle(f)
    is HandshakeMessage.CoreSetupReject   -> handle(f)
    is HandshakeMessage.CoreSetupAck      -> handle(f)
    is HandshakeMessage.ClientLogin       -> handle(f)
    is HandshakeMessage.ClientLoginReject -> handle(f)
    is HandshakeMessage.ClientLoginAck    -> handle(f)
    is HandshakeMessage.SessionInit       -> handle(f)
  }
}
