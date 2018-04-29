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

package de.kuschku.libquassel.quassel

enum class ExtendedFeature {
  SynchronizedMarkerLine,
  SaslAuthentication,
  SaslExternal,
  HideInactiveNetworks,
  PasswordChange,
  /** IRCv3 capability negotiation, account tracking */
  CapNegotiation,
  /** IRC server SSL validation */
  VerifyServerSSL,
  /** IRC server custom message rate limits */
  CustomRateLimits,
  DccFileTransfer,
  /** Timestamp formatting in away (e.g. %%hh:mm%%) */
  AwayFormatTimestamp,
  /** Whether or not the core supports auth backends. */
  Authenticators,
  /** Sync buffer activity status */
  BufferActivitySync,
  /** Core-Side highlight configuration and matching */
  CoreSideHighlights,
  /** Show prefixes for senders in backlog */
  SenderPrefixes,
  /** Supports RPC call disconnectFromCore to remotely disconnect a client */
  RemoteDisconnect,
  /** Transmit features as list of strings */
  ExtendedFeatures,
  /** Serialize message time as 64-bit */
  LongMessageTime,
  /** Real Name and Avatar URL in backlog */
  RichMessages;

  companion object {
    private val map = values().associateBy(ExtendedFeature::name)
    fun of(name: String) = map[name]
  }
}
