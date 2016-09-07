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

package de.kuschku.libquassel.syncables.types.abstracts;

import java.util.List;

import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.IdentitySerializer;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.impl.Identity;
import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;

public abstract class AIdentity extends SyncableObject<QIdentity> implements QIdentity {
    @Override
    public void setId(int id) {
        _setId(id);
        syncVar("setId", id);
    }

    @Override
    public void setIdentityName(String name) {
        _setIdentityName(name);
        syncVar("setIdentityName", name);
    }

    @Override
    public void setRealName(String realName) {
        _setRealName(realName);
        syncVar("setRealName", realName);
    }

    @Override
    public void setNicks(List<String> nicks) {
        _setNicks(nicks);
        syncVar("setNicks", nicks);
    }

    @Override
    public void setAwayNick(String awayNick) {
        _setAwayNick(awayNick);
        syncVar("setAwayNick", awayNick);
    }

    @Override
    public void setAwayNickEnabled(boolean enabled) {
        _setAwayNickEnabled(enabled);
        syncVar("setAwayNickEnabled", enabled);
    }

    @Override
    public void setAwayReason(String awayReason) {
        _setAwayReason(awayReason);
        syncVar("setAwayReason", awayReason);

    }

    @Override
    public void setAwayReasonEnabled(boolean enabled) {
        _setAwayReasonEnabled(enabled);
        syncVar("setAwayReasonEnabled", enabled);
    }

    @Override
    public void setAutoAwayEnabled(boolean enabled) {
        _setAutoAwayEnabled(enabled);
        syncVar("setAutoAwayEnabled", enabled);
    }

    @Override
    public void setAutoAwayTime(int time) {
        _setAutoAwayTime(time);
        syncVar("setAutoAwayTime", time);
    }

    @Override
    public void setAutoAwayReason(String reason) {
        _setAutoAwayReason(reason);
        syncVar("setAutoAwayReason", reason);
    }

    @Override
    public void setAutoAwayReasonEnabled(boolean enabled) {
        _setAutoAwayReasonEnabled(enabled);
        syncVar("setAutoAwayReasonEnabled", enabled);
    }

    @Override
    public void setDetachAwayEnabled(boolean enabled) {
        _setDetachAwayEnabled(enabled);
        syncVar("setDetachAwayEnabled", enabled);
    }

    @Override
    public void setDetachAwayReason(String reason) {
        _setDetachAwayReason(reason);
        syncVar("setDetachAwayReason", reason);
    }

    @Override
    public void setDetachAwayReasonEnabled(boolean enabled) {
        _setDetachAwayReasonEnabled(enabled);
        syncVar("setDetachAwayReasonEnabled", enabled);
    }

    @Override
    public void setIdent(String ident) {
        _setIdent(ident);
        syncVar("setIdent", ident);
    }

    @Override
    public void setKickReason(String reason) {
        _setKickReason(reason);
        syncVar("setKickReason", reason);
    }

    @Override
    public void setPartReason(String reason) {
        _setPartReason(reason);
        syncVar("setPartReason", reason);
    }

    @Override
    public void setQuitReason(String reason) {
        _setQuitReason(reason);
        syncVar("setQuitReason", reason);

    }

    @Override
    public void copyFrom(QIdentity other) {
        _copyFrom(other);
        syncVar("copyFrom", other);
    }

    @Override
    public void setSslKey(String encoded) {
        _setSslKey(encoded);
        syncVar("setSslKey", encoded);
    }

    @Override
    public void setSslCert(String encoded) {
        _setSslCert(encoded);
        syncVar("setSslCert", encoded);
    }

    @Override
    public void update(Identity identity) {
        _copyFrom(identity);
        syncVar("update", new QVariant<>("Identity", IdentitySerializer.get().toVariantMap(identity)));
    }
}
