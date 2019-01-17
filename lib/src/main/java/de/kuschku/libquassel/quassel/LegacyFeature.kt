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

package de.kuschku.libquassel.quassel

import de.kuschku.libquassel.util.flag.Flag
import de.kuschku.libquassel.util.flag.Flags

/**
 * A list of features that are optional in core and/or client, but need runtime checking
 *
 * Some features require an uptodate counterpart, but don't justify a protocol break.
 * This is what we use this enum for. Add such features to it and check at runtime on the other
 * side for their existence.
 *
 * This list should be cleaned up after every protocol break, as we can assume them to be present then.
 */
enum class LegacyFeature(override val bit: UInt) : Flag<LegacyFeature> {
  SynchronizedMarkerLine(0x0001u),
  SaslAuthentication(0x0002u),
  SaslExternal(0x0004u),
  HideInactiveNetworks(0x0008u),
  PasswordChange(0x0010u),
  /** IRCv3 capability negotiation, account tracking */
  CapNegotiation(0x0020u),
  /** IRC server SSL validation */
  VerifyServerSSL(0x0040u),
  /** IRC server custom message rate limits */
  CustomRateLimits(0x0080u),
  DccFileTransfer(0x0100u),
  /** Timestamp formatting in away (e.g. %%hh:mm%%) */
  AwayFormatTimestamp(0x0200u),
  /** Whether or not the core supports auth backends. */
  Authenticators(0x0400u),
  /** Sync buffer activity status */
  BufferActivitySync(0x0800u),
  /** Core-Side highlight configuration and matching */
  CoreSideHighlights(0x1000u),
  /** Show prefixes for senders in backlog */
  SenderPrefixes(0x2000u),
  /** Supports RPC call disconnectFromCore to remotely disconnect a client */
  RemoteDisconnect(0x4000u),
  /** Transmit features as list of strings */
  ExtendedFeatures(0x8000u);

  companion object : Flags.Factory<LegacyFeature> {
    override val NONE: Flags<LegacyFeature> = LegacyFeature.of()
    override fun of(bit: Int) = Flags.of<LegacyFeature>(bit)
    override fun of(bit: UInt) = Flags.of<LegacyFeature>(bit)
    override fun of(vararg flags: LegacyFeature) = Flags.of(*flags)
    override fun of(flags: Iterable<LegacyFeature>) = Flags.of(flags)

    fun fromExtended(it: ExtendedFeature) = when (it) {
      ExtendedFeature.SynchronizedMarkerLine -> LegacyFeature.SynchronizedMarkerLine
      ExtendedFeature.SaslAuthentication     -> LegacyFeature.SaslAuthentication
      ExtendedFeature.SaslExternal           -> LegacyFeature.SaslExternal
      ExtendedFeature.HideInactiveNetworks   -> LegacyFeature.HideInactiveNetworks
      ExtendedFeature.PasswordChange         -> LegacyFeature.PasswordChange
      ExtendedFeature.CapNegotiation         -> LegacyFeature.CapNegotiation
      ExtendedFeature.VerifyServerSSL        -> LegacyFeature.VerifyServerSSL
      ExtendedFeature.CustomRateLimits       -> LegacyFeature.CustomRateLimits
      ExtendedFeature.DccFileTransfer        -> LegacyFeature.DccFileTransfer
      ExtendedFeature.AwayFormatTimestamp    -> LegacyFeature.AwayFormatTimestamp
      ExtendedFeature.Authenticators         -> LegacyFeature.Authenticators
      ExtendedFeature.BufferActivitySync     -> LegacyFeature.BufferActivitySync
      ExtendedFeature.CoreSideHighlights     -> LegacyFeature.CoreSideHighlights
      ExtendedFeature.SenderPrefixes         -> LegacyFeature.SenderPrefixes
      ExtendedFeature.RemoteDisconnect       -> LegacyFeature.RemoteDisconnect
      ExtendedFeature.ExtendedFeatures       -> LegacyFeature.ExtendedFeatures
      else                                   -> null
    }
  }

  fun toExtended() = when (this) {
    LegacyFeature.SynchronizedMarkerLine -> ExtendedFeature.SynchronizedMarkerLine
    LegacyFeature.SaslAuthentication     -> ExtendedFeature.SaslAuthentication
    LegacyFeature.SaslExternal           -> ExtendedFeature.SaslExternal
    LegacyFeature.HideInactiveNetworks   -> ExtendedFeature.HideInactiveNetworks
    LegacyFeature.PasswordChange         -> ExtendedFeature.PasswordChange
    LegacyFeature.CapNegotiation         -> ExtendedFeature.CapNegotiation
    LegacyFeature.VerifyServerSSL        -> ExtendedFeature.VerifyServerSSL
    LegacyFeature.CustomRateLimits       -> ExtendedFeature.CustomRateLimits
    LegacyFeature.DccFileTransfer        -> ExtendedFeature.DccFileTransfer
    LegacyFeature.AwayFormatTimestamp    -> ExtendedFeature.AwayFormatTimestamp
    LegacyFeature.Authenticators         -> ExtendedFeature.Authenticators
    LegacyFeature.BufferActivitySync     -> ExtendedFeature.BufferActivitySync
    LegacyFeature.CoreSideHighlights     -> ExtendedFeature.CoreSideHighlights
    LegacyFeature.SenderPrefixes         -> ExtendedFeature.SenderPrefixes
    LegacyFeature.RemoteDisconnect       -> ExtendedFeature.RemoteDisconnect
    LegacyFeature.ExtendedFeatures       -> ExtendedFeature.ExtendedFeatures
  }
}
