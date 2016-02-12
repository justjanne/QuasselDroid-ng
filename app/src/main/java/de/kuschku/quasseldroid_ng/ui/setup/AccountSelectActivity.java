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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.setup.slides.AccountSelectSlide;
import de.kuschku.quasseldroid_ng.ui.setup.slides.SlideFragment;

public class AccountSelectActivity extends AppCompatActivity {
    private SlidePagerAdapter slidePagerAdapter;

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    @Bind(R.id.btn)
    FloatingActionButton btn;

    private class SlidePagerAdapter extends FragmentPagerAdapter {
        private SlideFragment item = new AccountSelectSlide();

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public SlideFragment getItem(int position) {
            return item;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);
        slidePagerAdapter = new SlidePagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(slidePagerAdapter);
        slidePagerAdapter.notifyDataSetChanged();
        updateValidity(slidePagerAdapter.item.isValid());
        btn.setImageResource(R.drawable.ic_check_dark);
        slidePagerAdapter.item.addChangeListener(this::updateValidity);
        btn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            slidePagerAdapter.item.getData(bundle);

            Intent result = new Intent();
            result.putExtra("extra", bundle);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    public void updateValidity(boolean validity) {
        btn.setVisibility(validity ? View.VISIBLE : View.GONE);
        slidePagerAdapter.notifyDataSetChanged();
    }
}
