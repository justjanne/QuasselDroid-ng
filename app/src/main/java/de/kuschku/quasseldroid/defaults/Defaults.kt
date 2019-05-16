/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.quasseldroid.defaults

import android.content.Context
import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.quasseldroid.R
import java.util.*

object Defaults {
  fun bufferViewConfigInitial(context: Context, proxy: SignalProxy = SignalProxy.NULL) =
    bufferViewConfig(context, proxy).apply {
      setBufferViewName(context.getString(R.string.default_bufferviewconfig_name))
    }

  fun bufferViewConfig(context: Context, proxy: SignalProxy = SignalProxy.NULL) =
    BufferViewConfig(-1, proxy).apply {
      setAllowedBufferTypes(Buffer_Type.of(
        Buffer_Type.StatusBuffer, Buffer_Type.ChannelBuffer, Buffer_Type.QueryBuffer
      ))
      setHideInactiveBuffers(false)
      setHideInactiveNetworks(false)
      setAddNewBuffersAutomatically(true)
      setSortAlphabetically(true)
      setShowSearch(true)
      setDisableDecoration(false)
      setMinimumActivity(Buffer_Activity.NoActivity.toInt())
    }

  fun identity(context: Context, proxy: SignalProxy = SignalProxy.NULL) =
    Identity(proxy).apply {
      setIdentityName("")
      setRealName(context.getString(R.string.default_identity_realname))
      setNicks(listOf(context.getString(R.string.default_identity_nick, Random().nextInt(16))))
      setAwayNick("")
      setAwayNickEnabled(false)
      setAwayReason(context.getString(R.string.default_identity_awayreason))
      setAwayReasonEnabled(true)
      setAutoAwayEnabled(false)
      setAutoAwayTime(10)
      setAutoAwayReason(context.getString(R.string.default_identity_autoawayreason))
      setAutoAwayReasonEnabled(false)
      setDetachAwayEnabled(false)
      setDetachAwayReason(context.getString(R.string.default_identity_detachawayreason))
      setDetachAwayReasonEnabled(false)
      setIdent(context.getString(R.string.default_identity_ident))
      setKickReason(context.getString(R.string.default_identity_kickreason))
      setPartReason(context.getString(R.string.default_identity_partreason))
      setQuitReason(context.getString(R.string.default_identity_quitreason))
    }

  fun network(context: Context, proxy: SignalProxy = SignalProxy.NULL) =
    Network(NetworkId(-1), proxy).apply {
      setNetworkName("")
    }
}
