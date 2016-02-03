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

package de.kuschku.quasseldroid_ng.ui.theme;

import android.support.annotation.NonNull;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.QClient;
import de.kuschku.quasseldroid_ng.ui.chat.WrappedSettings;

public class AppContext {
    private ThemeUtil themeUtil;
    private WrappedSettings settings;
    private QClient client;
    private BusProvider provider;

    public ThemeUtil themeUtil() {
        return themeUtil;
    }

    public void setThemeUtil(ThemeUtil themeUtil) {
        this.themeUtil = themeUtil;
    }

    @NonNull
    public AppContext withThemeUtil(ThemeUtil themeUtil) {
        setThemeUtil(themeUtil);
        return this;
    }

    public WrappedSettings settings() {
        return settings;
    }

    public void setSettings(WrappedSettings settings) {
        this.settings = settings;
    }

    @NonNull
    public AppContext withSettings(WrappedSettings settings) {
        setSettings(settings);
        return this;
    }

    public QClient client() {
        return client;
    }

    public void setClient(QClient client) {
        this.client = client;
    }

    @NonNull
    public AppContext withClient(QClient client) {
        setClient(client);
        return this;
    }

    public BusProvider provider() {
        return provider;
    }

    public void setProvider(BusProvider provider) {
        this.provider = provider;
    }

    @NonNull
    public AppContext withProvider(BusProvider provider) {
        setProvider(provider);
        return this;
    }
}
