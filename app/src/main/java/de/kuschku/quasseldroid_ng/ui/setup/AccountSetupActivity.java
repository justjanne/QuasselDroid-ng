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

package de.kuschku.quasseldroid_ng.ui.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.setup.slides.AccountSetupCoreSlide;
import de.kuschku.quasseldroid_ng.ui.setup.slides.AccountSetupNameSlide;
import de.kuschku.quasseldroid_ng.ui.setup.slides.AccountSetupUserSlide;
import de.kuschku.quasseldroid_ng.ui.setup.slides.SlideFragment;
import de.kuschku.quasseldroid_ng.util.accounts.Account;
import de.kuschku.quasseldroid_ng.util.accounts.AccountManager;

public class AccountSetupActivity extends AppCompatActivity implements ValidUpdateCallback {

    private SlidePagerAdapter slidePagerAdapter;

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    @Bind(R.id.btn)
    FloatingActionButton btn;

    private int lastValidPage = -1;

    private class SlidePagerAdapter extends FragmentPagerAdapter {
        List<SlideFragment> list = new ArrayList<>();

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public SlideFragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return Math.min(lastValidPage + 2, list.size());
        }

        public void addFragment(SlideFragment fragment) {
            list.add(fragment);
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);

        slidePagerAdapter = new SlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(slidePagerAdapter);

        slidePagerAdapter.addFragment(new AccountSetupCoreSlide());
        slidePagerAdapter.addFragment(new AccountSetupUserSlide());
        slidePagerAdapter.addFragment(new AccountSetupNameSlide());

        SlideFragment item = slidePagerAdapter.getItem(viewPager.getCurrentItem());

        updateValidity(item.isValid());

        btn.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == slidePagerAdapter.getCount() - 1) {
                onDone();
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });

        slidePagerAdapter.getItem(viewPager.getCurrentItem()).addChangeListener(AccountSetupActivity.this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SlideFragment slideFragment = slidePagerAdapter.getItem(viewPager.getCurrentItem());
                for (int i = 0; i < slidePagerAdapter.getCount(); i++) {
                    slidePagerAdapter.getItem(i).removeChangeListener(AccountSetupActivity.this);
                }
                slideFragment.addChangeListener(AccountSetupActivity.this);
                updateValidity(slideFragment.isValid());
                if (viewPager.getCurrentItem() == slidePagerAdapter.list.size() - 1) {
                    btn.setImageResource(R.drawable.ic_check_dark);
                } else {
                    btn.setImageResource(R.drawable.ic_arrow_right_dark);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void onDone() {
        Bundle bundle = new Bundle();
        for (SlideFragment fragment : slidePagerAdapter.list) {
            fragment.getData(bundle);
        }

        setupAccount(bundle);

        Intent result = new Intent();
        result.putExtra("extra", bundle);
        setResult(RESULT_OK, result);
        finish();
    }

    private void setupAccount(Bundle bundle) {
        AccountManager manager = new AccountManager(this);
        manager.add(new Account(
                UUID.randomUUID(),
                bundle.getString("name"),
                bundle.getString("host"),
                bundle.getInt("port"),
                bundle.getString("user"),
                bundle.getString("pass")
        ));
    }

    public void updateValidity(boolean validity) {
        btn.setVisibility(validity ? View.VISIBLE : View.GONE);
        if (validity) {
            lastValidPage = viewPager.getCurrentItem();
        } else {
            lastValidPage = viewPager.getCurrentItem() - 1;
        }
        slidePagerAdapter.notifyDataSetChanged();
    }
}
