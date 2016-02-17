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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
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
    private static final String STATEMENT_UPDATE_HIDDEN =
            String.format("UPDATE %s SET %s=? WHERE %s=? AND %s=?",
                    TABLE_ACCOUNTS, KEY_HIDDEN, KEY_CORE, KEY_BUFFER);

    @NonNull
    private static final String STATEMENT_UPDATE_MARKERLINE =
            String.format("UPDATE %s SET %s=? WHERE %s=? AND %s=?",
                    TABLE_ACCOUNTS, KEY_MARKERLINE, KEY_CORE, KEY_BUFFER);
    @NonNull
    private static final String STATEMENT_DELETE_BUFFER =
            String.format("DELETE FROM %s WHERE %s = ? AND %s = ?",
                    TABLE_ACCOUNTS, KEY_CORE, KEY_BUFFER);

    @NonNull
    private static final String STATEMENT_DELETE_CORE =
            String.format("DELETE FROM %s WHERE %s = ?",
                    TABLE_ACCOUNTS, KEY_CORE);

    @NonNull
    private static final String SPECIFIER_FIND =
            String.format("%s = ? ", KEY_CORE);

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

    public boolean storeMarkerline(String coreid, int bufferid, int messageid) {
        ensureExisting(coreid, bufferid);

        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(STATEMENT_UPDATE_MARKERLINE);
        statement.bindLong(1, messageid);
        statement.bindString(2, coreid);
        statement.bindLong(3, bufferid);
        return statement.executeUpdateDelete() > 0;
    }

    public boolean storeHiddenData(String coreid, int bufferid, int hiddendata) {
        ensureExisting(coreid, bufferid);

        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(STATEMENT_UPDATE_HIDDEN);
        statement.bindLong(1, hiddendata);
        statement.bindString(2, coreid);
        statement.bindLong(3, bufferid);
        return statement.executeUpdateDelete() > 0;
    }

    private boolean ensureExisting(String coreid, int bufferid) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(STATEMENT_INSERT);
        statement.bindString(1, coreid);
        statement.bindLong(2, bufferid);
        statement.bindLong(3, 0);
        statement.bindLong(4, -1);
        // executeInsert returns -1 if unsuccessful
        return statement.executeInsert() != -1;
    }

    private Cursor cursorFindData(String coreid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                // table name
                TABLE_ACCOUNTS,
                // column names
                new String[]{KEY_CORE, KEY_BUFFER, KEY_HIDDEN, KEY_MARKERLINE},
                // where clause
                SPECIFIER_FIND,
                // binds for where clause
                new String[]{coreid},
                null,
                null,
                null,
                null
        );
    }

    public int markerLine(String coreid, int bufferid) {
        Cursor cursor = cursorFindData(coreid);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(1) == bufferid) {
                    return cursor.getInt(3);
                }
            } while (cursor.moveToNext());
        }
        return -1;
    }

    public int hiddenData(String coreid, int bufferid) {
        Cursor cursor = cursorFindData(coreid);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(1) == bufferid) {
                    return cursor.getInt(2);
                }
            } while (cursor.moveToNext());
        }
        return 0;
    }

    public boolean deleteCore(String coreid) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(STATEMENT_DELETE_CORE);
        statement.bindString(1, coreid);
        // executeUpdateDelete returns amount of modified rows
        return statement.executeUpdateDelete() > 0;
    }

    public boolean deleteBuffer(String coreid, int bufferid) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(STATEMENT_DELETE_BUFFER);
        statement.bindString(1, coreid);
        statement.bindLong(2, bufferid);
        // executeUpdateDelete returns amount of modified rows
        return statement.executeUpdateDelete() > 0;
    }
}
