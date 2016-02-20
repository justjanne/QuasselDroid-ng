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
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.editor.AdvancedEditor;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.quasseldroid_ng.ui.theme.ThemeUtil;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class SlidingPanelHandler {
    private final Activity activity;
    private final AppContext context;
    private final AdvancedEditor editor;

    // Main Sliding Layout
    SlidingUpPanelLayout slidingLayout;

    // Input History
    @Bind(R.id.sliding_layout_history)
    SlidingUpPanelLayout slidingLayoutHistory;
    @Bind(R.id.msg_history)
    RecyclerView msgHistory;

    // Advanced Formatter
    @Bind(R.id.formatting_menu)
    ActionMenuView formattingMenu;
    @Bind(R.id.formatting_toolbar)
    Toolbar formattingToolbar;

    // Input Line
    @Bind(R.id.chatline)
    AppCompatEditText chatline;
    @Bind(R.id.send)
    AppCompatImageButton send;

    public SlidingPanelHandler(Activity activity, SlidingUpPanelLayout slidingLayout, AppContext context) {
        this.slidingLayout = slidingLayout;
        ButterKnife.bind(this, slidingLayout);
        this.activity = activity;
        this.context = context;
        this.editor = new AdvancedEditor(context, chatline);

        setupFormattingMenu(activity);

        setupHistoryFakeData();

        bindListener();
    }

    private void setupFormattingMenu(Activity activity) {
        activity.getMenuInflater().inflate(R.menu.formatting, formattingMenu.getMenu());
        formattingMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.format_bold:
                    editor.toggleBold();
                    return true;
                case R.id.format_italic:
                    editor.toggleItalic();
                    return true;
                case R.id.format_underline:
                    editor.toggleUnderline();
                    return true;
                case R.id.action_history:
                    openHistory();
                    return true;
                default:
                    return false;
            }
        });
    }

    public void setFormattingEnabled(boolean formattingEnabled) {
        Menu menu = formattingMenu.getMenu();
        MenuItem[] items = new MenuItem[]{
                menu.findItem(R.id.format_bold),
                menu.findItem(R.id.format_italic),
                menu.findItem(R.id.format_underline),
                menu.findItem(R.id.format_paint),
                menu.findItem(R.id.format_fill)
        };

        for (MenuItem item : items) {
            if (item != null)
                item.setEnabled(!formattingEnabled);
        }
    }

    private void setChatlineExpanded(boolean expanded) {
        assertNotNull(chatline);
        assertNotNull(chatline.getLayoutParams());

        ThemeUtil themeUtil = context.themeUtil();
        assertNotNull(themeUtil);

        int selectionStart = chatline.getSelectionStart();
        int selectionEnd = chatline.getSelectionEnd();

        if (expanded) {
            chatline.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            chatline.getLayoutParams().height = themeUtil.res.actionBarSize;
        }
        chatline.setSingleLine(!expanded);

        chatline.setSelection(selectionStart, selectionEnd);
    }

    private int combineColors(@ColorInt int colora, @ColorInt int colorb, @FloatRange(from = 0.0, to = 1.0) float offset) {
        float invOffset = 1 - offset;

        double alphaA = Math.pow(Color.alpha(colora), 2);
        double alphaB = Math.pow(Color.alpha(colorb), 2);

        double redA = Math.pow(Color.red(colora), 2);
        double redB = Math.pow(Color.red(colorb), 2);

        double greenA = Math.pow(Color.green(colora), 2);
        double greenB = Math.pow(Color.green(colorb), 2);

        double blueA = Math.pow(Color.blue(colora), 2);
        double blueB = Math.pow(Color.blue(colorb), 2);

        return Color.argb(
                (int) Math.sqrt(alphaA * invOffset + alphaB * offset),
                (int) Math.sqrt(redA * invOffset + redB * offset),
                (int) Math.sqrt(greenA * invOffset + greenB * offset),
                (int) Math.sqrt(blueA * invOffset + blueB * offset)
        );
    }

    private void bindListener() {
        slidingLayout.setAntiDragView(R.id.card_panel);
        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                /*
                slidingLayoutHistory.setBackgroundColor(combineColors(
                        context.themeUtil().res.colorBackgroundCard,
                        context.themeUtil().res.colorBackground,
                        slideOffset
                ));
                */
            }

            @Override
            public void onPanelCollapsed(View panel) {
                setChatlineExpanded(false);
            }

            @Override
            public void onPanelExpanded(View panel) {
                setChatlineExpanded(true);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
        setChatlineExpanded(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED);

        send.setOnClickListener(v -> sendInput());
        chatline.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                sendInput();
            }

            // Always return false to make sure we don’t lose focus
            return true;
        });
    }

    private void sendInput() {
        if (context.client() == null) return;

        int bufferId = context.client().backlogManager().open();

        if (bufferId >= 0) {
            Buffer buffer = context.client().bufferManager().buffer(bufferId);
            assertNotNull(buffer);

            String text = editor.toFormatString();
            context.client().sendInput(buffer.getInfo(), text);
            chatline.setText("");
            chatline.requestFocus();
        }
    }

    private void setupHistoryFakeData() {
        FastAdapter<IItem> fastAdapter = new FastAdapter<>();
        ItemAdapter<IItem> itemAdapter = new ItemAdapter<>();
        itemAdapter.wrap(fastAdapter);
        itemAdapter.add(
                new PrimaryDrawerItem().withName("Entry #1"),
                new PrimaryDrawerItem().withName("Entry #2"),
                new PrimaryDrawerItem().withName("Entry #3"),
                new PrimaryDrawerItem().withName("Entry #4"),
                new PrimaryDrawerItem().withName("Entry #5"),
                new PrimaryDrawerItem().withName("Entry #6"),
                new PrimaryDrawerItem().withName("Entry #7"),
                new PrimaryDrawerItem().withName("Entry #8"),
                new PrimaryDrawerItem().withName("Entry #9"),
                new PrimaryDrawerItem().withName("Entry #10"),
                new PrimaryDrawerItem().withName("Entry #11"),
                new PrimaryDrawerItem().withName("Entry #12"),
                new PrimaryDrawerItem().withName("Entry #13"),
                new PrimaryDrawerItem().withName("Entry #14"),
                new PrimaryDrawerItem().withName("Entry #15"),
                new PrimaryDrawerItem().withName("Entry #16")
        );
        msgHistory.setAdapter(fastAdapter);
        msgHistory.setLayoutManager(new LinearLayoutManager(activity));
        msgHistory.setItemAnimator(new DefaultItemAnimator());
    }

    private void openHistory() {
        slidingLayoutHistory.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }
}
