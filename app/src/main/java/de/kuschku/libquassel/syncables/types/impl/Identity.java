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

package de.kuschku.libquassel.syncables.types.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.abstracts.AIdentity;
import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;
import de.kuschku.util.CompatibilityUtils;

public class Identity extends AIdentity<Identity> {
    private int id;
    private String name;
    private String realName;
    private List<String> nicks;
    private String awayNick;
    private boolean awayNickEnabled;
    private String awayReason;
    private boolean awayReasonEnabled;
    private boolean autoAwayEnabled;
    private int autoAwayTime;
    private String autoAwayReason;
    private boolean autoAwayReasonEnabled;
    private boolean detachAwayEnabled;
    private String detachAwayReason;
    private boolean detachAwayReasonEnabled;
    private String ident;
    private String kickReason;
    private String partReason;
    private String quitReason;
    private String sslKey;
    private String sslCert;

    public Identity(int id, String name, String realName, List<String> nicks, String awayNick, boolean awayNickEnabled, String awayReason, boolean awayReasonEnabled, boolean autoAwayEnabled, int autoAwayTime, String autoAwayReason, boolean autoAwayReasonEnabled, boolean detachAwayEnabled, String detachAwayReason, boolean detachAwayReasonEnabled, String ident, String kickReason, String partReason, String quitReason) {
        this.id = id;
        this.name = name;
        this.realName = realName;
        this.nicks = nicks;
        this.awayNick = awayNick;
        this.awayNickEnabled = awayNickEnabled;
        this.awayReason = awayReason;
        this.awayReasonEnabled = awayReasonEnabled;
        this.autoAwayEnabled = autoAwayEnabled;
        this.autoAwayTime = autoAwayTime;
        this.autoAwayReason = autoAwayReason;
        this.autoAwayReasonEnabled = autoAwayReasonEnabled;
        this.detachAwayEnabled = detachAwayEnabled;
        this.detachAwayReason = detachAwayReason;
        this.detachAwayReasonEnabled = detachAwayReasonEnabled;
        this.ident = ident;
        this.kickReason = kickReason;
        this.partReason = partReason;
        this.quitReason = quitReason;
    }

    private String defaultNick() {
        return "quassel";
    }


    private String defaultRealName() {
        return "Quassel IRC User";
    }

    @Override
    public void setToDefaults() {
        setIdentityName("");
        setRealName(defaultRealName());
        setNicks(Collections.singletonList(defaultNick()));
        setAwayNick("");
        setAwayNickEnabled(false);
        setAwayReason("Gone fishing.");
        setAwayReasonEnabled(true);
        setAutoAwayEnabled(false);
        setAutoAwayTime(10);
        setAutoAwayReason("Not here. No, really. not here!");
        setAutoAwayReasonEnabled(false);
        setDetachAwayEnabled(false);
        setDetachAwayReason("All Quassel clients vanished from the face of the earth...");
        setDetachAwayReasonEnabled(false);
        setIdent("quassel");
        setKickReason("Kindergarten is elsewhere!");
        setPartReason("http://quassel-irc.org - Chat comfortably. Anywhere.");
        setQuitReason("http://quassel-irc.org - Chat comfortably. Anywhere.");
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String identityName() {
        return name;
    }

    @Override
    public String realName() {
        return realName;
    }

    @Override
    public List<String> nicks() {
        return nicks;
    }

    @Override
    public String awayNick() {
        return awayNick;
    }

    @Override
    public boolean awayNickEnabled() {
        return awayNickEnabled;
    }

    @Override
    public String awayReason() {
        return awayReason;
    }

    @Override
    public boolean awayReasonEnabled() {
        return awayReasonEnabled;
    }

    @Override
    public boolean autoAwayEnabled() {
        return autoAwayEnabled;
    }

    @Override
    public int autoAwayTime() {
        return autoAwayTime;
    }

    @Override
    public String autoAwayReason() {
        return autoAwayReason;
    }

    @Override
    public boolean autoAwayReasonEnabled() {
        return autoAwayReasonEnabled;
    }

    @Override
    public boolean detachAwayEnabled() {
        return detachAwayEnabled;
    }

    @Override
    public String detachAwayReason() {
        return detachAwayReason;
    }

    @Override
    public boolean detachAwayReasonEnabled() {
        return detachAwayReasonEnabled;
    }

    @Override
    public String ident() {
        return ident;
    }

    @Override
    public String kickReason() {
        return kickReason;
    }

    @Override
    public String partReason() {
        return partReason;
    }

    @Override
    public String quitReason() {
        return quitReason;
    }

    @Override
    public void _setId(int id) {
        this.id = id;
        _update();
    }

    @Override
    public void _setIdentityName(String name) {
        this.name = name;
        _update();
    }

    @Override
    public void _setRealName(String realName) {
        this.realName = realName;
        _update();
    }

    @Override
    public void _setNicks(List<String> nicks) {
        this.nicks = nicks;
        _update();
    }

    @Override
    public void _setAwayNick(String awayNick) {
        this.awayNick = awayNick;
        _update();
    }

    @Override
    public void _setAwayNickEnabled(boolean enabled) {
        this.awayNickEnabled = enabled;
        _update();
    }

    @Override
    public void _setAwayReason(String awayReason) {
        this.awayReason = awayReason;
        _update();
    }

    @Override
    public void _setAwayReasonEnabled(boolean enabled) {
        this.awayReasonEnabled = enabled;
        _update();
    }

    @Override
    public void _setAutoAwayEnabled(boolean enabled) {
        this.autoAwayEnabled = enabled;
        _update();
    }

    @Override
    public void _setAutoAwayTime(int time) {
        this.autoAwayTime = time;
        _update();
    }

    @Override
    public void _setAutoAwayReason(String reason) {
        this.autoAwayReason = reason;
        _update();
    }

    @Override
    public void _setAutoAwayReasonEnabled(boolean enabled) {
        this.autoAwayReasonEnabled = enabled;
        _update();
    }

    @Override
    public void _setDetachAwayEnabled(boolean enabled) {
        this.detachAwayEnabled = enabled;
        _update();
    }

    @Override
    public void _setDetachAwayReason(String reason) {
        this.detachAwayReason = reason;
        _update();
    }

    @Override
    public void _setDetachAwayReasonEnabled(boolean enabled) {
        this.detachAwayReasonEnabled = enabled;
        _update();
    }

    @Override
    public void _setIdent(String ident) {
        this.ident = ident;
        _update();
    }

    @Override
    public void _setKickReason(String reason) {
        this.kickReason = reason;
        _update();
    }

    @Override
    public void _setPartReason(String reason) {
        this.partReason = reason;
        _update();
    }

    @Override
    public void _setQuitReason(String reason) {
        this.quitReason = reason;
        _update();
    }

    @Override
    public void _copyFrom(@NonNull QIdentity other) {
        this.id = other.id();
        this.name = other.identityName();
        this.realName = other.realName();
        this.nicks = other.nicks();
        this.awayNick = other.awayNick();
        this.awayNickEnabled = other.awayNickEnabled();
        this.awayReason = other.awayReason();
        this.awayReasonEnabled = other.awayReasonEnabled();
        this.autoAwayEnabled = other.autoAwayEnabled();
        this.autoAwayTime = other.autoAwayTime();
        this.autoAwayReason = other.autoAwayReason();
        this.autoAwayReasonEnabled = other.autoAwayReasonEnabled();
        this.detachAwayEnabled = other.detachAwayEnabled();
        this.detachAwayReason = other.detachAwayReason();
        this.detachAwayReasonEnabled = other.detachAwayReasonEnabled();
        this.ident = other.ident();
        this.kickReason = other.kickReason();
        this.partReason = other.partReason();
        this.quitReason = other.quitReason();
        this.sslKey = other.sslKey();
        this.sslCert = other.sslCert();
        _update();
    }

    @Override
    public String sslKey() {
        return sslKey;
    }

    @Nullable
    @Override
    public String sslKeyPem() {
        return null;
    }

    @Override
    public String sslCert() {
        return sslCert;
    }

    @Nullable
    @Override
    public String sslCertPem() {
        return null;
    }

    @Override
    public void _setSslKey(String encoded) {
        this.sslKey = encoded;
        _update();
    }

    @Override
    public void _setSslCert(String encoded) {
        this.sslCert = encoded;
        _update();
    }

    @Override
    public void _update(Map<String, QVariant> from) {
    }

    @Override
    public void _update(Identity from) {
    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        super.init(objectName, provider, client);
        client.identityManager().createIdentity(this);
    }
}
