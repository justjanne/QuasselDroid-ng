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
import de.kuschku.util.annotationbind.AutoBinder;
import de.kuschku.util.ui.MenuTint;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class SlidingPanelHandler {
    // Main Sliding Layout
    final SlidingUpPanelLayout slidingLayout;
    private final Activity activity;
    private final AppContext context;
    private final AdvancedEditor editor;
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
    private ItemAdapter<IItem> previousMessages;

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

        MenuTint.colorIcons(formattingMenu.getMenu(), AutoBinder.obtainColor(R.attr.colorFill, formattingToolbar.getContext().getTheme()));

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

                case R.id.fill_clear:
                    editor.toggleBackground(-1);
                    return true;
                case R.id.fill_mircColor0:
                    editor.toggleBackground(0x0);
                    return true;
                case R.id.fill_mircColor1:
                    editor.toggleBackground(0x1);
                    return true;
                case R.id.fill_mircColor2:
                    editor.toggleBackground(0x2);
                    return true;
                case R.id.fill_mircColor3:
                    editor.toggleBackground(0x3);
                    return true;
                case R.id.fill_mircColor4:
                    editor.toggleBackground(0x4);
                    return true;
                case R.id.fill_mircColor5:
                    editor.toggleBackground(0x5);
                    return true;
                case R.id.fill_mircColor6:
                    editor.toggleBackground(0x6);
                    return true;
                case R.id.fill_mircColor7:
                    editor.toggleBackground(0x7);
                    return true;
                case R.id.fill_mircColor8:
                    editor.toggleBackground(0x8);
                    return true;
                case R.id.fill_mircColor9:
                    editor.toggleBackground(0x9);
                    return true;
                case R.id.fill_mircColorA:
                    editor.toggleBackground(0xA);
                    return true;
                case R.id.fill_mircColorB:
                    editor.toggleBackground(0xB);
                    return true;
                case R.id.fill_mircColorC:
                    editor.toggleBackground(0xC);
                    return true;
                case R.id.fill_mircColorD:
                    editor.toggleBackground(0xD);
                    return true;
                case R.id.fill_mircColorE:
                    editor.toggleBackground(0xE);
                    return true;
                case R.id.fill_mircColorF:
                    editor.toggleBackground(0xF);
                    return true;

                case R.id.paint_clear:
                    editor.toggleForeground(-1);
                    return true;
                case R.id.paint_mircColor0:
                    editor.toggleForeground(0x0);
                    return true;
                case R.id.paint_mircColor1:
                    editor.toggleForeground(0x1);
                    return true;
                case R.id.paint_mircColor2:
                    editor.toggleForeground(0x2);
                    return true;
                case R.id.paint_mircColor3:
                    editor.toggleForeground(0x3);
                    return true;
                case R.id.paint_mircColor4:
                    editor.toggleForeground(0x4);
                    return true;
                case R.id.paint_mircColor5:
                    editor.toggleForeground(0x5);
                    return true;
                case R.id.paint_mircColor6:
                    editor.toggleForeground(0x6);
                    return true;
                case R.id.paint_mircColor7:
                    editor.toggleForeground(0x7);
                    return true;
                case R.id.paint_mircColor8:
                    editor.toggleForeground(0x8);
                    return true;
                case R.id.paint_mircColor9:
                    editor.toggleForeground(0x9);
                    return true;
                case R.id.paint_mircColorA:
                    editor.toggleForeground(0xA);
                    return true;
                case R.id.paint_mircColorB:
                    editor.toggleForeground(0xB);
                    return true;
                case R.id.paint_mircColorC:
                    editor.toggleForeground(0xC);
                    return true;
                case R.id.paint_mircColorD:
                    editor.toggleForeground(0xD);
                    return true;
                case R.id.paint_mircColorE:
                    editor.toggleForeground(0xE);
                    return true;
                case R.id.paint_mircColorF:
                    editor.toggleForeground(0xF);
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

    private void bindListener() {
        slidingLayout.setAntiDragView(R.id.card_panel);
        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

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
                return true;
            }

            // Always return false to make sure we don’t lose focus
            return false;
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
            previousMessages.add(new PrimaryDrawerItem().withName(text));
        }
    }

    private void setupHistoryFakeData() {
        FastAdapter<IItem> fastAdapter = new FastAdapter<>();
        previousMessages = new ItemAdapter<>();
        previousMessages.wrap(fastAdapter);
        msgHistory.setAdapter(fastAdapter);
        msgHistory.setLayoutManager(new LinearLayoutManager(activity));
        msgHistory.setItemAnimator(new DefaultItemAnimator());
    }

    private void openHistory() {
        slidingLayoutHistory.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public boolean onBackPressed() {
        if (slidingLayoutHistory.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingLayoutHistory.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return true;
        } else {
            return false;
        }
    }

    public void onDestroy() {
        chatline.setOnKeyListener(null);
        send.setOnClickListener(null);
        slidingLayout.setPanelSlideListener(null);
        formattingMenu.setOnMenuItemClickListener(null);
    }
}
