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

package de.kuschku.quasseldroid_ng.util.accounts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AccountManagerHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    @NonNull
    private static final String DATABASE_NAME = "accounts";
    @NonNull
    private static final String TABLE_ACCOUNTS = "accounts";

    @NonNull
    private static final String KEY_ID = "account_id";
    @NonNull
    private static final String KEY_NAME = "account_name";
    @NonNull
    private static final String KEY_HOST = "account_host";
    @NonNull
    private static final String KEY_PORT = "account_port";
    @NonNull
    private static final String KEY_USER = "account_user";
    @NonNull
    private static final String KEY_PASS = "account_pass";

    // Again we can only use String.format, as SQL doesn’t support table or column names to be bound
    // in prepared statements
    @NonNull
    private static final String STATEMENT_INSERT =
            String.format("INSERT OR IGNORE INTO %s(%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
                    TABLE_ACCOUNTS, KEY_ID, KEY_NAME, KEY_HOST, KEY_PORT, KEY_USER, KEY_PASS);
    @NonNull
    private static final String STATEMENT_DELETE =
            String.format("DELETE FROM %s WHERE %s = ?",
                    TABLE_ACCOUNTS, KEY_ID);

    @NonNull
    private static final String SPECIFIER_FIND =
            String.format("%s = ?", KEY_ID);

    public AccountManagerHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Why do we use String.format and not prepared statements? Because we can’t bind table or
        // column names in prepared statements
        String statement = String.format("CREATE TABLE %s (%s, %s, %s, %s, %s, %s, PRIMARY KEY (%s), UNIQUE(%s));",
                TABLE_ACCOUNTS,
                KEY_ID, KEY_NAME, KEY_HOST, KEY_PORT, KEY_USER, KEY_PASS,
                KEY_ID,
                KEY_ID);
        db.execSQL(statement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(STATEMENT_INSERT);
        statement.bindString(1, account.id.toString());
        statement.bindString(2, account.name);
        statement.bindString(3, account.host);
        statement.bindLong(4, account.port);
        statement.bindString(5, account.user);
        statement.bindString(6, account.pass);
        // executeInsert returns -1 if unsuccessful
        return statement.executeInsert() != -1;
    }

    public boolean updateAccount(Account account) {
        removeAccount(account.id);
        return addAccount(account);
    }

    public boolean removeAccount(UUID id) {
        return removeAccount(id.toString());
    }

    public boolean removeAccount(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(STATEMENT_DELETE);
        statement.bindString(1, id);
        // executeUpdateDelete returns amount of modified rows
        return statement.executeUpdateDelete() > 0;
    }

    private Cursor cursorFindAllAccounts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                // table name
                TABLE_ACCOUNTS,
                // column names
                new String[]{KEY_ID, KEY_NAME, KEY_HOST, KEY_PORT, KEY_USER, KEY_PASS},
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Cursor cursorFindAccount(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                // table name
                TABLE_ACCOUNTS,
                // column names
                new String[]{KEY_ID, KEY_NAME, KEY_HOST, KEY_PORT, KEY_USER, KEY_PASS},
                // where clause
                SPECIFIER_FIND,
                // binds for where clause
                new String[]{id},
                null,
                null,
                null,
                null
        );
    }

    @NonNull
    public Set<Account> findAllAccounts() {
        Cursor cursor = cursorFindAllAccounts();

        Set<Account> accounts = new HashSet<>(cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String host = cursor.getString(2);
                int port = (int) cursor.getLong(3);
                String user = cursor.getString(4);
                String pass = cursor.getString(5);
                try {
                    accounts.add(new Account(UUID.fromString(id), name, host, port, user, pass));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("AccountManager", "Removing account because invalid", e);
                    removeAccount(id);
                }
            } while (cursor.moveToNext());
        }

        return accounts;
    }

    public Account account(String query_id) {
        Cursor cursor = cursorFindAccount(query_id);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String host = cursor.getString(2);
                int port = (int) cursor.getLong(3);
                String user = cursor.getString(4);
                String pass = cursor.getString(5);
                try {
                    return new Account(UUID.fromString(id), name, host, port, user, pass);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("AccountManager", "Removing account because invalid", e);
                    removeAccount(id);
                }
            } while (cursor.moveToNext());
        }

        return null;
    }
}
