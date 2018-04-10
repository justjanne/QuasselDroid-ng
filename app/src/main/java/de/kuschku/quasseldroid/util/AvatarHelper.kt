package de.kuschku.quasseldroid.util

import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.quasseldroid.persistence.QuasselDatabase

object AvatarHelper {
  fun avatar(message: QuasselDatabase.DatabaseMessage? = null, user: IrcUser? = null): String? {
    val ident = message?.sender?.let(HostmaskHelper::user)
                ?: user?.user()
                ?: return null

    val userId = Regex("[us]id(\\d+)").matchEntire(ident)?.groupValues?.lastOrNull()
                 ?: return null

    return "https://static.irccloud-cdn.com/avatar-redirect/$userId"
  }
}
