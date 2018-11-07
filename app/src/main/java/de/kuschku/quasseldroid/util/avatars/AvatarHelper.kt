/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
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

package de.kuschku.quasseldroid.util.avatars

import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.info.user.IrcUserInfo
import de.kuschku.quasseldroid.util.Patterns
import de.kuschku.quasseldroid.util.backport.codec.Hex
import de.kuschku.quasseldroid.util.helper.letIf
import de.kuschku.quasseldroid.util.helper.notBlank
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
import de.kuschku.quasseldroid.viewmodel.data.Avatar
import de.kuschku.quasseldroid.viewmodel.data.IrcUserItem
import org.apache.commons.codec.digest.DigestUtils

object AvatarHelper {
  fun avatar(settings: MessageSettings, ident: String, realName: String,
             avatarUrl: String?, size: Int?) =
    listOfNotNull(
      avatarUrl.notBlank()?.let { listOf(Avatar.NativeAvatar(it)) },
      (settings.showAvatars && settings.showIRCCloudAvatars).letIf {
        ircCloudFallback(ident, size)
      },
      (settings.showAvatars && settings.showGravatarAvatars).letIf {
        gravatarFallback(realName, size)
      },
      (settings.showAvatars && settings.showMatrixAvatars).letIf {
        matrixFallback(realName, size)
      }
    ).flatten()

  fun avatar(settings: MessageSettings, message: QuasselDatabase.NotificationData,
             size: Int? = null) =
    avatar(settings, HostmaskHelper.user(message.sender), message.realName, message.avatarUrl, size)

  fun avatar(settings: MessageSettings, message: QuasselDatabase.MessageData, size: Int? = null) =
    avatar(settings, HostmaskHelper.user(message.sender), message.realName, message.avatarUrl, size)

  fun avatar(settings: MessageSettings, user: IrcUserItem, size: Int? = null) =
    avatar(settings, HostmaskHelper.user(user.hostmask), user.realname.toString(), null, size)

  fun avatar(settings: MessageSettings, user: IrcUserInfo, size: Int? = null) =
    avatar(settings, user.user ?: "", user.realName ?: "", null, size)

  fun avatar(settings: MessageSettings, user: IrcUser, size: Int? = null) =
    avatar(settings, user.user(), user.realName(), null, size)

  fun avatar(settings: MessageSettings, user: AutoCompleteItem.UserItem, size: Int? = null) =
    avatar(settings, HostmaskHelper.user(user.hostMask), user.realname.toString(), null, size)

  private fun ircCloudFallback(ident: String, size: Int?): List<Avatar> {
    val userId = Patterns.IRCCLOUD_IDENT.matchEntire(ident)?.groupValues?.lastOrNull()
                 ?: return emptyList()

    if (size != null) {
      return listOf(
        Avatar.IRCCloudAvatar(
          "https://static.irccloud-cdn.com/avatar-redirect/s${truncateSize(size)}/$userId"
        )
      )
    }

    return listOf(
      Avatar.IRCCloudAvatar(
        "https://static.irccloud-cdn.com/avatar-redirect/$userId"
      )
    )
  }

  private fun gravatarFallback(realname: String, size: Int?): List<Avatar> {
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
      }.map { Avatar.GravatarAvatar(it) }.toList()
  }

  private fun matrixFallback(realname: String, size: Int?): List<Avatar> {
    return if (Patterns.MATRIX_REALNAME.matches(realname)) {
      listOf(
        Avatar.MatrixAvatar(realname, size?.let(this::truncateSize))
      )
    } else {
      emptyList()
    }
  }

  private fun truncateSize(originalSize: Int) = if (originalSize > 72) 512 else 72
}
