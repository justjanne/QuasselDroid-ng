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

package de.kuschku.util.certificates;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CertificateDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    @NonNull
    private static final String DATABASE_NAME = "certificates";
    @NonNull
    private static final String TABLE_CERTIFICATES = "certificates";

    @NonNull
    private static final String KEY_CORE_ADDRESS = "core_address";
    @NonNull
    private static final String KEY_FINGERPRINT = "fingerprint";

    // Again we can only use String.format, as SQL doesn’t support table or column names to be bound
    // in prepared statements
    @NonNull
    private static final String STATEMENT_INSERT =
            String.format("INSERT OR IGNORE INTO %s(%s, %s) VALUES (?, ?)",
                    TABLE_CERTIFICATES, KEY_CORE_ADDRESS, KEY_FINGERPRINT);
    @NonNull
    private static final String STATEMENT_DELETE =
            String.format("DELETE FROM %s WHERE %s = ? AND %s = ?",
                    TABLE_CERTIFICATES, KEY_CORE_ADDRESS, KEY_FINGERPRINT);
    @NonNull
    private static final String STATEMENT_DELETE_ALL =
            String.format("DELETE FROM %s WHERE %s = ?",
                    TABLE_CERTIFICATES, KEY_CORE_ADDRESS);

    @NonNull
    private static final String SPECIFIER_FIND_ALL = String.format("%s = ?", KEY_CORE_ADDRESS);

    public CertificateDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        // Why do we use String.format and not prepared statements? Because we can’t bind table or
        // column names in prepared statements
        String statement = String.format("CREATE TABLE %s (%s, %s, PRIMARY KEY (%s, %s), UNIQUE(%s, %s));",
                TABLE_CERTIFICATES,
                KEY_CORE_ADDRESS, KEY_FINGERPRINT,
                KEY_CORE_ADDRESS, KEY_FINGERPRINT,
                KEY_CORE_ADDRESS, KEY_FINGERPRINT);
        db.execSQL(statement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addCertificate(String fingerprint, String coreAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(STATEMENT_INSERT);
        statement.bindString(1, coreAddress);
        statement.bindString(2, fingerprint);
        // executeInsert returns -1 if unsuccessful
        return statement.executeInsert() != -1;
    }

    public boolean removeCertificate(String fingerprint, String coreAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(STATEMENT_DELETE);
        statement.bindString(1, coreAddress);
        statement.bindString(2, fingerprint);
        // executeUpdateDelete returns amount of modified rows
        return statement.executeUpdateDelete() > 0;
    }

    public boolean removeCertificates(String coreAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(STATEMENT_DELETE_ALL);
        statement.bindString(1, coreAddress);
        // executeUpdateDelete returns amount of modified rows
        return statement.executeUpdateDelete() > 0;
    }

    public Cursor cursorFindCertificates(String coreAddress) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                // table name
                TABLE_CERTIFICATES,
                // column names
                new String[]{KEY_FINGERPRINT},
                // where clause
                SPECIFIER_FIND_ALL,
                // binds for where clause
                new String[]{coreAddress},
                null,
                null,
                null,
                null
        );
    }

    public Cursor cursorFindAllCertificates() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                // table name
                TABLE_CERTIFICATES,
                // column names
                new String[]{KEY_CORE_ADDRESS, KEY_FINGERPRINT},
                // where clause
                null,
                // binds for where clause
                new String[0],
                null,
                null,
                null,
                null
        );
    }

    @NonNull
    public List<String> findCertificates(String coreAddress) {
        Cursor cursor = cursorFindCertificates(coreAddress);
        List<String> certificates = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                certificates.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return certificates;
    }

    @NonNull
    public Map<String, Collection<String>> findAllCertificates() {
        Cursor cursor = cursorFindAllCertificates();

        Map<String, Collection<String>> certificates = new HashMap<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String coreid = cursor.getString(0);
                String fingerprint = cursor.getString(1);
                if (certificates.get(coreid) == null)
                    certificates.put(coreid, new HashSet<>());

                certificates.get(coreid).add(fingerprint);
            } while (cursor.moveToNext());
        }

        return certificates;
    }
}
