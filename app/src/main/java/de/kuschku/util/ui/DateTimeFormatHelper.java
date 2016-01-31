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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.util.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;

public class DateTimeFormatHelper {
    @NonNull
    private final Context context;

    public DateTimeFormatHelper(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    public static DateTimeFormatter getTimeFormatter(Context ctx) {
        return DateTimeFormat.forPattern(((SimpleDateFormat) android.text.format.DateFormat.getTimeFormat(ctx)).toLocalizedPattern());
    }

    @NonNull
    public static DateTimeFormatter getDateFormatter(Context ctx) {
        return DateTimeFormat.forPattern(((SimpleDateFormat) android.text.format.DateFormat.getDateFormat(ctx)).toLocalizedPattern());
    }

    @NonNull
    public static DateTimeFormatter getLongDateFormatter(Context ctx) {
        return DateTimeFormat.forPattern(((SimpleDateFormat) android.text.format.DateFormat.getLongDateFormat(ctx)).toLocalizedPattern());
    }

    @NonNull
    public static DateTimeFormatter getMediumDateFormatter(Context ctx) {
        return DateTimeFormat.forPattern(((SimpleDateFormat) android.text.format.DateFormat.getMediumDateFormat(ctx)).toLocalizedPattern());
    }

    @NonNull
    public DateTimeFormatter getTimeFormatter() {
        return getTimeFormatter(context);
    }

    @NonNull
    public DateTimeFormatter getDateFormatter() {
        return getDateFormatter(context);
    }

    @NonNull
    public DateTimeFormatter getLongDateFormatter() {
        return getLongDateFormatter(context);
    }

    @NonNull
    public DateTimeFormatter getMediumDateFormatter() {
        return getMediumDateFormatter(context);
    }
}
