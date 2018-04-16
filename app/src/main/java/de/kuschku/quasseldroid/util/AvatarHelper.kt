package de.kuschku.quasseldroid.util

import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.backport.codec.Hex
import de.kuschku.quasseldroid.util.helper.letIf
import de.kuschku.quasseldroid.util.helper.notBlank
import de.kuschku.quasseldroid.viewmodel.data.IrcUserItem
import org.apache.commons.codec.digest.DigestUtils

object AvatarHelper {
  fun avatar(settings: MessageSettings, message: QuasselDatabase.DatabaseMessage,
             size: Int? = null) = listOfNotNull(
    message.avatarUrl.notBlank()?.let { listOf(it) },
    settings.showIRCCloudAvatars.letIf {
      ircCloudFallback(HostmaskHelper.user(message.sender), size)
    },
    settings.showGravatarAvatars.letIf {
      gravatarFallback(message.realName, size)
    }
  ).flatten()

  fun avatar(settings: MessageSettings, user: IrcUserItem, size: Int? = null) = listOfNotNull(
    settings.showIRCCloudAvatars.letIf {
      ircCloudFallback(HostmaskHelper.user(user.hostmask), size)
    },
    settings.showGravatarAvatars.letIf {
      gravatarFallback(user.realname.toString(), size)
    }
  ).flatten()

  fun avatar(settings: MessageSettings, user: IrcUser, size: Int? = null) = listOfNotNull(
    settings.showIRCCloudAvatars.letIf {
      ircCloudFallback(user.user(), size)
    },
    settings.showGravatarAvatars.letIf {
      gravatarFallback(user.realName(), size)
    }
  ).flatten()

  private fun gravatarFallback(realname: String, size: Int?): List<String> {
    return Patterns.AUTOLINK_EMAIL_ADDRESS
      .findAll(realname)
      .mapNotNull {
        it.groups[1]?.value
      }.map { email ->
        val hash = Hex.encodeHexString(DigestUtils.md5(IrcCaseMappers.unicode.toLowerCase(email)))
        if (size == null) {
          "https://www.gravatar.com/avatar/$hash?d=404"
        } else {
          "https://www.gravatar.com/avatar/$hash?d=404&s=${truncateSize(size)}"
        }
      }.toList()
  }

  private fun ircCloudFallback(ident: String, size: Int?): List<String> {
    val userId = Patterns.IRCCLOUD_IDENT.matchEntire(ident)?.groupValues?.lastOrNull()
                 ?: return emptyList()

    if (size != null) {
      return listOf("https://static.irccloud-cdn.com/avatar-redirect/w${truncateSize(size)}/$userId")
    }

    return listOf("https://static.irccloud-cdn.com/avatar-redirect/$userId")
  }

  private fun truncateSize(originalSize: Int) = if (originalSize > 72) 512 else 72
}
