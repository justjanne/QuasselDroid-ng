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

import java.util.Set;
import java.util.UUID;

public class AccountManager {
    AccountManagerHelper helper;

    public AccountManager(Context context) {
        helper = new AccountManagerHelper(context);
    }

    public Set<Account> accounts() {
        return helper.findAllAccounts();
    }

    public void add(Account account) {
        helper.addAccount(account);
    }

    public void update(Account account) {
        helper.updateAccount(account);
    }

    public void remove(String id) {
        remove(UUID.fromString(id));
    }

    public void remove(UUID id) {
        helper.removeAccount(id);
    }

    public void remove(Account account) {
        remove(account.id);
    }

    public Account account(String id) {
        return helper.account(id);
    }
}
