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
enum class QuasselFeature(override val bit: Int) : Flag<QuasselFeature> {
  None(0x0000),
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
  /** Show prefixes for senders in backlog */
  SenderPrefixes(0x1000);

  companion object : Flags.Factory<QuasselFeature> {
    override val NONE: Flags<QuasselFeature> = QuasselFeature.of()
    val validValues = values().filter { it.bit != 0 }.toTypedArray()
    override fun of(bit: Int) = Flags.of<QuasselFeature>(bit)
    override fun of(vararg flags: QuasselFeature) = Flags.of(*flags)
  }
}
