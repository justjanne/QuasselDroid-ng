package de.kuschku.quasseldroid.ui.clientsettings.whitelist

import de.kuschku.quasseldroid.persistence.QuasselDatabase

data class Whitelist(
  val certificates: List<QuasselDatabase.SslValidityWhitelistEntry>,
  val hostnames: List<QuasselDatabase.SslHostnameWhitelistEntry>
)
