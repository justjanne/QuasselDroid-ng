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

package de.kuschku.quasseldroid.util.missingfeatures

import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.quasseldroid.R

object RequiredFeatures {
  val features = listOf(
    MissingFeature(ExtendedFeature.SynchronizedMarkerLine,
                   R.string.label_feature_synchronizedmarkerline),
    MissingFeature(ExtendedFeature.SaslAuthentication,
                   R.string.label_feature_saslauthentication),
    MissingFeature(ExtendedFeature.SaslExternal,
                   R.string.label_feature_saslexternal),
    MissingFeature(ExtendedFeature.HideInactiveNetworks,
                   R.string.label_feature_hideinactivenetworks),
    MissingFeature(ExtendedFeature.PasswordChange,
                   R.string.label_feature_passwordchange),
    MissingFeature(ExtendedFeature.CapNegotiation,
                   R.string.label_feature_capnegotiation),
    MissingFeature(ExtendedFeature.VerifyServerSSL,
                   R.string.label_feature_verifyserverssl),
    MissingFeature(ExtendedFeature.CustomRateLimits,
                   R.string.label_feature_customratelimits),
    MissingFeature(ExtendedFeature.AwayFormatTimestamp,
                   R.string.label_feature_awayformattimestamp),
    MissingFeature(ExtendedFeature.BufferActivitySync,
                   R.string.label_feature_bufferactivitysync),
    MissingFeature(ExtendedFeature.CoreSideHighlights,
                   R.string.label_feature_coresidehighlights),
    MissingFeature(ExtendedFeature.SenderPrefixes,
                   R.string.label_feature_senderprefixes),
    MissingFeature(ExtendedFeature.RemoteDisconnect,
                   R.string.label_feature_remotedisconnect),
    MissingFeature(ExtendedFeature.RichMessages,
                   R.string.label_feature_richmessages),
    MissingFeature(ExtendedFeature.BacklogFilterType,
                   R.string.label_feature_backlogfiltertype)
  )
}
