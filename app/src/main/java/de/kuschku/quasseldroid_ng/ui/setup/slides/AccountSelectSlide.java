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

package de.kuschku.quasseldroid_ng.ui.setup.slides;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.Iterator;
import java.util.Set;

import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.setup.AccountItem;
import de.kuschku.quasseldroid_ng.ui.setup.AccountSetupActivity;
import de.kuschku.quasseldroid_ng.ui.setup.CreateAccountItem;
import de.kuschku.util.accounts.Account;
import de.kuschku.util.accounts.AccountManager;
import de.kuschku.util.backports.Optional;
import de.kuschku.util.backports.Optionals;

public class AccountSelectSlide extends SlideFragment implements AccountItem.OnDeleteListener {
    private ItemAdapter<IItem> itemAdapter;
    private FastAdapter<IItem> fastAdapter;
    private AccountManager manager;

    @Override
    public Bundle getData(Bundle in) {
        AccountItem item = (AccountItem) findAny(fastAdapter.getSelectedItems()).get();
        in.putString("account", item.account.id.toString());
        return in;
    }

    private <T> Optional<T> findAny(Set<T> selections) {
        Iterator<T> iterator = selections.iterator();
        if (iterator.hasNext()) {
            return Optionals.of(iterator.next());
        } else {
            return Optionals.absent();
        }
    }

    @Override
    public boolean isValid() {
        return fastAdapter != null && fastAdapter.getSelections().size() == 1 && findAny(fastAdapter.getSelectedItems()).get() instanceof AccountItem;
    }

    @Override
    protected View onCreateContent(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.slide_select, container, false);

        manager = new AccountManager(getContext());

        fastAdapter = new FastAdapter<>();
        itemAdapter = new ItemAdapter<>();
        itemAdapter.wrap(fastAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        updateContent();
        recyclerView.setAdapter(fastAdapter);

        fastAdapter.withSelectWithItemUpdate(true);
        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            if (item instanceof CreateAccountItem) {
                createNew();
                return true;
            } else {
                fastAdapter.deselect();
                fastAdapter.select(position);
                updateValidity();
                return false;
            }
        });


        return recyclerView;
    }

    private void createNew() {
        Intent intent = new Intent(getContext(), AccountSetupActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (findAny(fastAdapter.getSelectedItems()).get() instanceof CreateAccountItem)
            fastAdapter.deselect();

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            updateContent();
        } else if (fastAdapter.getItemCount() == 0) {
            getActivity().finish();
        }
    }

    private void updateContent() {
        itemAdapter.clear();
        Set<Account> accounts = manager.accounts();
        for (Account account : accounts) {
            AccountItem accountItem = new AccountItem(account);
            accountItem.setOnDeleteListener(this);
            itemAdapter.add(accountItem);
        }
        itemAdapter.add(new CreateAccountItem());

        if (accounts.size() == 0) {
            createNew();
        }
    }

    @Override
    protected int getTitle() {
        return R.string.slideAccountselectTitle;
    }

    @Override
    protected int getDescription() {
        return R.string.slideAccountselectDescription;
    }

    @Override
    public void onDelete(AccountItem item) {
        manager.remove(item.account);
        updateContent();
    }
}
