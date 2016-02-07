/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.syncables.types.interfaces;

import android.support.annotation.Nullable;

import java.util.List;

import de.kuschku.libquassel.syncables.Synced;

public interface QIdentity extends QObservable {
    void setToDefaults();

    boolean isValid();

    int id();

    String identityName();

    String realName();

    List<String> nicks();

    String awayNick();

    boolean awayNickEnabled();

    String awayReason();

    boolean awayReasonEnabled();

    boolean autoAwayEnabled();

    int autoAwayTime();

    String autoAwayReason();

    boolean autoAwayReasonEnabled();

    boolean detachAwayEnabled();

    String detachAwayReason();

    boolean detachAwayReasonEnabled();

    String ident();

    String kickReason();

    String partReason();

    String quitReason();

    @Synced
    void setId(int id);

    void _setId(int id);

    @Synced
    void setIdentityName(final String name);

    void _setIdentityName(final String name);

    @Synced
    void setRealName(final String realName);

    void _setRealName(final String realName);

    @Synced
    void setNicks(final List<String> nicks);

    void _setNicks(final List<String> nicks);

    @Synced
    void setAwayNick(final String awayNick);

    void _setAwayNick(final String awayNick);

    @Synced
    void setAwayNickEnabled(boolean enabled);

    void _setAwayNickEnabled(boolean enabled);

    @Synced
    void setAwayReason(final String awayReason);

    void _setAwayReason(final String awayReason);

    @Synced
    void setAwayReasonEnabled(boolean enabled);

    void _setAwayReasonEnabled(boolean enabled);

    @Synced
    void setAutoAwayEnabled(boolean enabled);

    void _setAutoAwayEnabled(boolean enabled);

    @Synced
    void setAutoAwayTime(int time);

    void _setAutoAwayTime(int time);

    @Synced
    void setAutoAwayReason(final String reason);

    void _setAutoAwayReason(final String reason);

    @Synced
    void setAutoAwayReasonEnabled(boolean enabled);

    void _setAutoAwayReasonEnabled(boolean enabled);

    @Synced
    void setDetachAwayEnabled(boolean enabled);

    void _setDetachAwayEnabled(boolean enabled);

    @Synced
    void setDetachAwayReason(final String reason);

    void _setDetachAwayReason(final String reason);

    @Synced
    void setDetachAwayReasonEnabled(boolean enabled);

    void _setDetachAwayReasonEnabled(boolean enabled);

    @Synced
    void setIdent(final String ident);

    void _setIdent(final String ident);

    @Synced
    void setKickReason(final String reason);

    void _setKickReason(final String reason);

    @Synced
    void setPartReason(final String reason);

    void _setPartReason(final String reason);

    @Synced
    void setQuitReason(final String reason);

    void _setQuitReason(final String reason);

    @Synced
    void copyFrom(final QIdentity other);

    void _copyFrom(final QIdentity other);

    String sslKey();

    @Nullable
    String sslKeyPem();

    String sslCert();

    @Nullable
    String sslCertPem();

    @Synced
    void setSslKey(final String encoded);

    void _setSslKey(final String encoded);

    @Synced
    void setSslCert(final String encoded);

    void _setSslCert(final String encoded);
}
