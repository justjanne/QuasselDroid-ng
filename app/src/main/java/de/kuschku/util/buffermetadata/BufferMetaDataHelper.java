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

package de.kuschku.util.buffermetadata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class BufferMetaDataHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    @NonNull
    private static final String DATABASE_NAME = "bufferdata";
    @NonNull
    private static final String TABLE_ACCOUNTS = "bufferdata";

    @NonNull
    private static final String KEY_CORE = "core";
    @NonNull
    private static final String KEY_BUFFER = "buffer";
    @NonNull
    private static final String KEY_HIDDEN = "hiddenevents";
    @NonNull
    private static final String KEY_MARKERLINE = "markerline";

    // Again we can only use String.format, as SQL doesn’t support table or column names to be bound
    // in prepared statements
    @NonNull
    private static final String STATEMENT_INSERT =
            String.format("INSERT OR IGNORE INTO %s(%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                    TABLE_ACCOUNTS, KEY_CORE, KEY_BUFFER, KEY_HIDDEN, KEY_MARKERLINE);
    @NonNull
    private static final String STATEMENT_DELETE =
            String.format("DELETE FROM %s WHERE %s = ? AND %s = ?",
                    TABLE_ACCOUNTS, KEY_CORE, KEY_BUFFER);

    @NonNull
    private static final String SPECIFIER_FIND =
            String.format("%s = ? AND %s = ?", KEY_CORE, KEY_BUFFER);

    public BufferMetaDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Why do we use String.format and not prepared statements? Because we can’t bind table or
        // column names in prepared statements
        String statement = String.format("CREATE TABLE %s (%s, %s, %s, %s, PRIMARY KEY (%s, %s), UNIQUE(%s));",
                TABLE_ACCOUNTS,
                KEY_CORE, KEY_BUFFER, KEY_HIDDEN, KEY_MARKERLINE,
                KEY_CORE, KEY_BUFFER,
                KEY_CORE);
        db.execSQL(statement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storeMarkerline(String coreid, int bufferid, int messageid) {

    }

    public void storeHiddenData(String coreid, int bufferid, int hiddendata) {

    }

    public int markerLine(String coreid, int bufferid) {
        return -1;
    }

    public int hiddenData(String coreid, int bufferid) {
        return -1;
    }

    public void deleteCore(String coreid) {

    }

    public void deleteBuffer(String coreid, int bufferid) {

    }
}
