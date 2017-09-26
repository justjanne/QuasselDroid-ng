package de.kuschku.libquassel.util.irc

object HostmaskHelper {
  fun nick(mask: String): String {
    val (nick, _, _) = split(mask)
    return nick
  }

  fun user(mask: String): String {
    val (_, user, _) = split(mask)
    return user
  }

  fun host(mask: String): String {
    val (_, _, host) = split(mask)
    return host
  }

  fun split(mask: String): Triple<String, String, String> {
    val userPartHostSplit = mask.split("@", limit = 2)
    if (userPartHostSplit.size < 2)
      return Triple(mask, "", "")

    val (userPart, host) = userPartHostSplit

    val nickUserSplit = userPart.split('!', limit = 2)
    if (nickUserSplit.size < 2)
      return Triple(mask, "", host)

    val (nick, user) = nickUserSplit
    return Triple(nick, user, host)
  }
}
