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

import org.joda.time.DateTime;

import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.abstracts.ACoreInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QCoreInfo;

public class CoreInfo extends ACoreInfo {
    private Map<String, QVariant> coreData;

    private int sessionConnectedClients;

    private String quasselVersion;

    private String quasselBuildDate;

    private DateTime startTime;

    public CoreInfo(Map<String, QVariant> coreData) {
        _setCoreData(coreData);
    }

    @Override
    public Map<String, QVariant> coreData() {
        return coreData;
    }

    @Override
    public void _setCoreData(Map<String, QVariant> coreData) {
        this.coreData = coreData;

        QVariant sessionConnectedClients1 = coreData.remove("sessionConnectedClients");
        this.sessionConnectedClients = sessionConnectedClients1 != null ? (int) sessionConnectedClients1.data : -1;

        QVariant quasselVersion1 = coreData.remove("quasselVersion");
        this.quasselVersion = quasselVersion1 != null ? (String) quasselVersion1.data : null;

        QVariant quasselBuildDate1 = coreData.remove("quasselBuildDate");
        this.quasselBuildDate = quasselBuildDate1 != null ? (String) quasselBuildDate1.data : null;

        QVariant startTime1 = coreData.remove("startTime");
        this.startTime = startTime1 != null ? (DateTime) startTime1.data : null;

        _update();
    }

    @Override
    public void _update(Map<String, QVariant> from) {

    }

    @Override
    public void _update(QCoreInfo from) {

    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        super.init(objectName, provider, client);
        client.setCoreInfo(this);
    }

    public int sessionConnectedClients() {
        return sessionConnectedClients;
    }

    public String quasselVersion() {
        return quasselVersion;
    }

    public String quasselBuildDate() {
        return quasselBuildDate;
    }

    public DateTime startTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "CoreInfo{" +
                "sessionConnectedClients=" + sessionConnectedClients +
                ", quasselVersion='" + quasselVersion + '\'' +
                ", quasselBuildDate='" + quasselBuildDate + '\'' +
                ", startTime=" + startTime +
                ", coreData=" + coreData +
                '}';
    }
}
