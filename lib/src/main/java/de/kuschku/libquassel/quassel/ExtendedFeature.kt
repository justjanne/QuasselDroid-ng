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
