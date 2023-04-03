/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2023 Janne Mareike Koschinski
 * Copyright (c) 2023 The Quassel Project
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

package de.kuschku.quasseldroid.ui.info.user

import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.IrcUser

data class IrcUserMeta(
    val networkId: NetworkId,
    val nick: String,
    val knownToCore: Boolean = false,
    val user: String? = null,
    val host: String? = null,
    val account: String? = null,
    val server: String? = null,
    val realName: String? = null,
    val isAway: Boolean? = false,
    val awayMessage: String? = null,
    val ircUser: IrcUser? = null,
)
