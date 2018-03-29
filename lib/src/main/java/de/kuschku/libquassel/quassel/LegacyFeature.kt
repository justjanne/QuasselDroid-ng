package de.kuschku.libquassel.quassel

import de.kuschku.libquassel.util.Flag
import de.kuschku.libquassel.util.Flags

/**
 * A list of features that are optional in core and/or client, but need runtime checking
 *
 * Some features require an uptodate counterpart, but don't justify a protocol break.
 * This is what we use this enum for. Add such features to it and check at runtime on the other
 * side for their existence.
 *
 * This list should be cleaned up after every protocol break, as we can assume them to be present then.
 */
enum class LegacyFeature(override val bit: Int) : Flag<LegacyFeature> {
  SynchronizedMarkerLine(0x0001),
  SaslAuthentication(0x0002),
  SaslExternal(0x0004),
  HideInactiveNetworks(0x0008),
  PasswordChange(0x0010),
  /** IRCv3 capability negotiation, account tracking */
  CapNegotiation(0x0020),
  /** IRC server SSL validation */
  VerifyServerSSL(0x0040),
  /** IRC server custom message rate limits */
  CustomRateLimits(0x0080),
  DccFileTransfer(0x0100),
  /** Timestamp formatting in away (e.g. %%hh:mm%%) */
  AwayFormatTimestamp(0x0200),
  /** Whether or not the core supports auth backends. */
  Authenticators(0x0400),
  /** Sync buffer activity status */
  BufferActivitySync(0x0800),
  /** Core-Side highlight configuration and matching */
  CoreSideHighlights(0x1000),
  /** Show prefixes for senders in backlog */
  SenderPrefixes(0x2000),
  /** Supports RPC call disconnectFromCore to remotely disconnect a client */
  RemoteDisconnect(0x4000),
  /** Transmit features as list of strings */
  ExtendedFeatures(0x8000);

  companion object : Flags.Factory<LegacyFeature> {
    override val NONE: Flags<LegacyFeature> = LegacyFeature.of()
    val validValues = values().filter { it.bit != 0 }.toTypedArray()
    override fun of(bit: Int) = Flags.of<LegacyFeature>(bit)
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
