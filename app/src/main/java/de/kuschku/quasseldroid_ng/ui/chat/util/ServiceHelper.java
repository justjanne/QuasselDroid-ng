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

package de.kuschku.quasseldroid_ng.ui.chat.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.quasseldroid_ng.service.QuasselService;
import de.kuschku.quasseldroid_ng.ui.settings.Settings;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.quasseldroid_ng.ui.theme.AppTheme;
import de.kuschku.quasseldroid_ng.ui.theme.ThemeUtil;

public class ServiceHelper {
    private ServiceHelper() {
    }

    /**
     * Connects to an already running QuasselService
     *
     * @param context    The context in which the service is running
     * @param connection The connection to which it should be bound
     */
    public static void connectToService(Context context, ServiceConnection connection) {
        Intent intent = new Intent(context, QuasselService.class);
        context.bindService(intent, connection, Context.BIND_IMPORTANT);
    }

    /**
     * Connects to an already running QuasselService
     *
     * @param context    The context in which the service is running
     * @param connection The connection which should be disconnected
     */
    public static void disconnect(Context context, ServiceConnection connection) {
        context.unbindService(connection);
    }

    /**
     * Starts a new QuasselService, if not yet running.
     *
     * @param context The context in which it should run
     */
    public static void startServiceIfNotRunning(Context context) {
        Intent intent = new Intent(context, QuasselService.class);
        context.startService(intent);
    }

    public static int initContext(AppContext context, Activity activity) {
        context.setSettings(new Settings(activity));
        context.bufferDisplayTypes().add(QBufferViewConfig.DisplayType.ALWAYS);
        return initTheme(context, activity);
    }

    public static int initTheme(AppContext context, Activity activity) {
        // Load Theme from Preferences
        AppTheme theme = AppTheme.themeFromString(context.settings().preferenceTheme.get());
        activity.setTheme(theme.themeId);
        context.setThemeUtil(new ThemeUtil(activity, theme));
        return theme.themeId;
    }
}
