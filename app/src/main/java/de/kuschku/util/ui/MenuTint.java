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

/*
 * Copyright (C) 2015. Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.kuschku.util.ui;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.ActionMenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import java.lang.reflect.Method;

/**
 * <p>Apply colors and/or transparency to menu icons in a {@link Menu}.</p>
 * <p>
 * <p>Example usage:</p>
 * <p>
 * <pre class="prettyprint">
 * public boolean onCreateOptionsMenu(Menu menu) {
 * ...
 * int color = getResources().getColor(R.color.your_awesome_color);
 * int alpha = 204; // 80% transparency
 * MenuTint.on(menu).setMenuItemIconColor(color).setMenuItemIconAlpha(alpha).apply(this);
 * ...
 * }
 * </pre>
 */
public class MenuTint {

    private static final String TAG = "MenuTint";

    private static Method nativeIsActionButton;
    private final Menu menu;
    private final Integer originalMenuItemIconColor;
    private final Integer menuItemIconAlpha;
    private final Integer subMenuIconColor;
    private final Integer subMenuIconAlpha;
    private final Integer overflowDrawableId;
    private final boolean reApplyOnChange;
    private final boolean forceIcons;
    private Integer menuItemIconColor;
    private ImageView overflowButton;
    private ViewGroup actionBarView;

    private MenuTint(Builder builder) {
        menu = builder.menu;
        originalMenuItemIconColor = builder.originalMenuItemIconColor;
        menuItemIconColor = builder.menuItemIconColor;
        menuItemIconAlpha = builder.menuItemIconAlpha;
        subMenuIconColor = builder.subMenuIconColor;
        subMenuIconAlpha = builder.subMenuIconAlpha;
        overflowDrawableId = builder.overflowDrawableId;
        reApplyOnChange = builder.reApplyOnChange;
        forceIcons = builder.forceIcons;
    }

    /**
     * Check if an item is showing (not in the overflow menu).
     *
     * @param item the MenuItem.
     * @return {@code true} if the MenuItem is visible on the ActionBar.
     */
    public static boolean isActionButton(MenuItem item) {
        if (item instanceof MenuItemImpl) {
            return ((MenuItemImpl) item).isActionButton();
        }
        if (nativeIsActionButton == null) {
            try {
                Class<?> MenuItemImpl = Class.forName("com.android.internal.view.menu.MenuItemImpl");
                nativeIsActionButton = MenuItemImpl.getDeclaredMethod("isActionButton");
                if (!nativeIsActionButton.isAccessible()) {
                    nativeIsActionButton.setAccessible(true);
                }
            } catch (Exception ignored) {
            }
        }
        try {
            return (boolean) nativeIsActionButton.invoke(item, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Check if an item is in the overflow menu.
     *
     * @param item the MenuItem
     * @return {@code true} if the MenuItem is in the overflow menu.
     * @see #isActionButton(MenuItem)
     */
    public static boolean isInOverflow(MenuItem item) {
        return !isActionButton(item);
    }

    /**
     * Sets the color filter and/or the alpha transparency on a {@link MenuItem}'s icon.
     *
     * @param menuItem The {@link MenuItem} to theme.
     * @param color    The color to set for the color filter or {@code null} for no changes.
     * @param alpha    The alpha value (0...255) to set on the icon or {@code null} for no changes.
     */
    public static void colorMenuItem(MenuItem menuItem, Integer color, Integer alpha) {
        if (color == null && alpha == null) {
            return; // nothing to do.
        }
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            // If we don't mutate the drawable, then all drawables with this id will have the ColorFilter
            drawable.mutate();
            if (color != null) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
            if (alpha != null) {
                drawable.setAlpha(alpha);
            }
        }
    }

    /**
     * Set the menu to show MenuItem icons in the overflow window.
     *
     * @param menu the menu to force icons to show
     */
    public static void forceMenuIcons(Menu menu) {
        try {
            Class<?> MenuBuilder = menu.getClass();
            Method setOptionalIconsVisible =
                    MenuBuilder.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            if (!setOptionalIconsVisible.isAccessible()) {
                setOptionalIconsVisible.setAccessible(true);
            }
            setOptionalIconsVisible.invoke(menu, true);
        } catch (Exception ignored) {
        }
    }

    public static Builder on(Menu menu) {
        return new Builder(menu);
    }

    /**
     * Apply a ColorFilter with the specified color to all icons in the menu.
     *
     * @param menu  the menu after items have been added.
     * @param color the color for the ColorFilter.
     */
    public static void colorIcons(Menu menu, int color) {
        MenuTint.on(menu).setMenuItemIconColor(color).apply();
    }

    public static void colorIcons(Activity activity, Menu menu, int color) {
        MenuTint.on(menu).setMenuItemIconColor(color).apply(activity);
    }

    /**
     * @param activity the Activity
     * @return the OverflowMenuButton or {@code null} if it doesn't exist.
     */
    public static ImageView getOverflowMenuButton(Activity activity) {
        return findOverflowMenuButton(activity, findActionBar(activity));
    }

    private static ImageView findOverflowMenuButton(Activity activity, ViewGroup viewGroup) {
        if (viewGroup == null) {
            return null;
        }
        ImageView overflow = null;
        for (int i = 0, count = viewGroup.getChildCount(); i < count; i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ImageView
                    && (v.getClass().getSimpleName().equals("OverflowMenuButton")
                    || v instanceof ActionMenuView.ActionMenuChildView)) {
                overflow = (ImageView) v;
            } else if (v instanceof ViewGroup) {
                overflow = findOverflowMenuButton(activity, (ViewGroup) v);
            }
            if (overflow != null) {
                break;
            }
        }
        return overflow;
    }

    private static ViewGroup findActionBar(Activity activity) {
        int id = activity.getResources().getIdentifier("action_bar", "id", "android");
        ViewGroup actionBar = null;
        if (id != 0) {
            actionBar = (ViewGroup) activity.findViewById(id);
        }
        if (actionBar == null) {
            actionBar = findToolbar((ViewGroup)
                    activity.findViewById(android.R.id.content).getRootView());
        }
        return actionBar;
    }

    private static ViewGroup findToolbar(ViewGroup viewGroup) {
        ViewGroup toolbar = null;
        for (int i = 0, len = viewGroup.getChildCount(); i < len; i++) {
            View view = viewGroup.getChildAt(i);
            if (view.getClass() == android.support.v7.widget.Toolbar.class
                    || view.getClass().getName().equals("android.widget.Toolbar")) {
                toolbar = (ViewGroup) view;
            } else if (view instanceof ViewGroup) {
                toolbar = findToolbar((ViewGroup) view);
            }
            if (toolbar != null) {
                break;
            }
        }
        return toolbar;
    }

    public void apply() {
        if (forceIcons) {
            forceMenuIcons(menu);
        }

        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            colorMenuItem(item, menuItemIconColor, menuItemIconAlpha);
            if (reApplyOnChange) {
                View view = item.getActionView();
                if (view != null) {
                    if (item instanceof MenuItemImpl) {
                        ((MenuItemImpl) item).setSupportOnActionExpandListener(
                                new SupportActionExpandListener(this));
                    } else {
                        item.setOnActionExpandListener(new NativeActionExpandListener(this));
                    }
                }
            }
        }
    }

    /**
     * <p>Sets a ColorFilter and/or alpha on all the {@link MenuItem}s in the menu, including the
     * OverflowMenuButton.</p>
     * <p>
     * <p>Call this method after inflating/creating your menu in
     * {@link Activity#onCreateOptionsMenu(Menu)}.</p>
     * <p>
     * <p>Note: This is targeted for the native ActionBar/Toolbar, not AppCompat.</p>
     */
    public void apply(Activity activity) {
        apply();
        actionBarView = findActionBar(activity);
        if (actionBarView == null) {
            Log.w(TAG, "Could not find the ActionBar");
            return;
        }

        // We must wait for the view to be created to set a color filter on the drawables.
        actionBarView.post(new Runnable() {

            @Override
            public void run() {
                for (int i = 0, size = menu.size(); i < size; i++) {
                    MenuItem menuItem = menu.getItem(i);
                    if (isInOverflow(menuItem)) {
                        colorMenuItem(menuItem, subMenuIconColor, subMenuIconAlpha);
                    }
                    if (menuItem.hasSubMenu()) {
                        SubMenu subMenu = menuItem.getSubMenu();
                        for (int j = 0; j < subMenu.size(); j++) {
                            colorMenuItem(subMenu.getItem(j), subMenuIconColor, subMenuIconAlpha);
                        }
                    }
                }
                if (menuItemIconColor != null || menuItemIconAlpha != null) {
                    overflowButton = findOverflowMenuButton(activity, actionBarView);
                    colorOverflowMenuItem(overflowButton);
                }
            }
        });
    }

    /**
     * <p>Sets a ColorFilter and/or alpha on all the {@link MenuItem}s in the menu, including the
     * OverflowMenuButton.</p>
     * <p>
     * <p>This should only be called after calling {@link #apply(Activity)}. It is useful for when
     * {@link MenuItem}s might be re-arranged due to an action view being collapsed or expanded.</p>
     */
    public void reapply() {

        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            if (isActionButton(item)) {
                colorMenuItem(menu.getItem(i), menuItemIconColor, menuItemIconAlpha);
            }
        }

        if (actionBarView == null) {
            return;
        }

        actionBarView.post(new Runnable() {

            @Override
            public void run() {
                for (int i = 0, size = menu.size(); i < size; i++) {
                    MenuItem menuItem = menu.getItem(i);
                    if (isInOverflow(menuItem)) {
                        colorMenuItem(menuItem, subMenuIconColor, subMenuIconAlpha);
                    } else {
                        colorMenuItem(menu.getItem(i), menuItemIconColor, menuItemIconAlpha);
                    }
                    if (menuItem.hasSubMenu()) {
                        SubMenu subMenu = menuItem.getSubMenu();
                        for (int j = 0; j < subMenu.size(); j++) {
                            colorMenuItem(subMenu.getItem(j), subMenuIconColor, subMenuIconAlpha);
                        }
                    }
                }
                if (menuItemIconColor != null || menuItemIconAlpha != null) {
                    colorOverflowMenuItem(overflowButton);
                }
            }

        });
    }

    private void colorOverflowMenuItem(ImageView overflow) {
        if (overflow != null) {
            if (overflowDrawableId != null) {
                overflow.setImageResource(overflowDrawableId);
            }
            if (menuItemIconColor != null) {
                overflow.setColorFilter(menuItemIconColor);
            }
            if (menuItemIconAlpha != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    overflow.setImageAlpha(menuItemIconAlpha);
                } else {
                    overflow.setAlpha(menuItemIconAlpha);
                }
            }
        }
    }

    public Menu getMenu() {
        return menu;
    }

    public ImageView getOverflowMenuButton() {
        return overflowButton;
    }

    public void setMenuItemIconColor(Integer color) {
        menuItemIconColor = color;
    }

    public static class NativeActionExpandListener implements OnActionExpandListener {

        private final MenuTint menuTint;

        public NativeActionExpandListener(MenuTint menuTint) {
            this.menuTint = menuTint;
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            int color = menuTint.originalMenuItemIconColor == null ? menuTint.menuItemIconColor :
                    menuTint.originalMenuItemIconColor;
            menuTint.setMenuItemIconColor(color);
            menuTint.reapply();
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            int color = menuTint.originalMenuItemIconColor == null ? menuTint.menuItemIconColor :
                    menuTint.originalMenuItemIconColor;
            menuTint.setMenuItemIconColor(color);
            menuTint.reapply();
            return true;
        }

    }

    public static class SupportActionExpandListener implements
            MenuItemCompat.OnActionExpandListener {

        private final MenuTint menuTint;

        public SupportActionExpandListener(MenuTint menuTint) {
            this.menuTint = menuTint;
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            int color = menuTint.originalMenuItemIconColor == null ? menuTint.menuItemIconColor :
                    menuTint.originalMenuItemIconColor;
            menuTint.setMenuItemIconColor(color);
            menuTint.reapply();
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            int color = menuTint.originalMenuItemIconColor == null ? menuTint.menuItemIconColor :
                    menuTint.originalMenuItemIconColor;
            menuTint.setMenuItemIconColor(color);
            menuTint.reapply();
            return true;
        }

    }

    // --------------------------------------------------------------------------------------------

    public static final class Builder {

        private final Menu menu;
        private Integer menuItemIconColor;
        private Integer menuItemIconAlpha;
        private Integer subMenuIconColor;
        private Integer subMenuIconAlpha;
        private Integer overflowDrawableId;
        private Integer originalMenuItemIconColor;
        private boolean reApplyOnChange;
        private boolean forceIcons;

        private Builder(Menu menu) {
            this.menu = menu;
        }

        /**
         * <p>Sets an {@link OnActionExpandListener} on all {@link MenuItem}s with views, so when the
         * menu is updated, the colors will be also.</p>
         * <p>
         * <p>This is useful when the overflow menu is showing icons and {@link MenuItem}s might be
         * pushed to the overflow menu when a action view is expanded e.g. android.widget.SearchView.
         * </p>
         *
         * @param reapply {@code true} to set the listeners on all {@link MenuItem}s with action views.
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder reapplyOnChange(boolean reapply) {
            reApplyOnChange = reapply;
            return this;
        }

        /**
         * Specify an alpha value for visible MenuItem icons, including the OverflowMenuButton.
         *
         * @param alpha the alpha value for the drawable. 0 means fully transparent, and 255 means fully
         *              opaque.
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder setMenuItemIconAlpha(int alpha) {
            menuItemIconAlpha = alpha;
            return this;
        }

        /**
         * Specify a color for visible MenuItem icons, including the OverflowMenuButton.
         *
         * @param color the color to apply on visible MenuItem icons, including the OverflowMenuButton.
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder setMenuItemIconColor(int color) {
            menuItemIconColor = color;
            return this;
        }

        /**
         * Specify a color that is applied when an action view is expanded or collapsed.
         *
         * @param color the color to apply on MenuItems when an action-view is expanded or collapsed.
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder setOriginalMenuItemIconColor(int color) {
            originalMenuItemIconColor = color;
            return this;
        }

        /**
         * Set the drawable id to set on the OverflowMenuButton.
         *
         * @param drawableId the resource identifier of the drawable
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder setOverflowDrawableId(int drawableId) {
            overflowDrawableId = drawableId;
            return this;
        }

        /**
         * Specify an alpha value for MenuItems that are in a SubMenu or in the Overflow menu.
         *
         * @param alpha the alpha value for the drawable. 0 means fully transparent, and 255 means fully
         *              opaque.
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder setSubMenuIconAlpha(int alpha) {
            subMenuIconAlpha = alpha;
            return this;
        }

        /**
         * Specify a color for MenuItems that are in a SubMenu or in the Overflow menu.
         *
         * @param color the color to apply on visible MenuItem icons, including the OverflowMenuButton.
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder setSubMenuIconColor(int color) {
            subMenuIconColor = color;
            return this;
        }

        /**
         * Set the menu to show MenuItem icons in the overflow window.
         *
         * @return this Builder object to allow for chaining of calls to set methods
         */
        public Builder forceIcons() {
            forceIcons = true;
            return this;
        }

        /**
         * <p>Sets a ColorFilter and/or alpha on all the MenuItems in the menu, including the
         * OverflowMenuButton.</p>
         * <p>
         * <p>Call this method after inflating/creating your menu in</p>
         * {@link Activity#onCreateOptionsMenu(Menu)}.</p>
         * <p>
         * <p>Note: This is targeted for the native ActionBar/Toolbar, not AppCompat.</p>
         */
        public MenuTint apply() {
            MenuTint theme = new MenuTint(this);
            theme.apply();
            return theme;
        }

        public MenuTint apply(Activity activity) {
            MenuTint theme = new MenuTint(this);
            theme.apply(activity);
            return theme;
        }

        /**
         * <p>Creates a {@link MenuTint} with the arguments supplied to this builder.</p>
         * <p>
         * <p>It does not apply the theme. Call {@link MenuTint#apply(Activity)} to do so.</p>
         *
         * @see #apply(Activity)
         */
        public MenuTint create() {
            return new MenuTint(this);
        }

    }

    // --------------------------------------------------------------------------------------------

    /**
     * Auto collapses the SearchView when the soft keyboard is dismissed.
     */
    public static class SearchViewFocusListener implements View.OnFocusChangeListener {

        private final MenuItem item;

        public SearchViewFocusListener(MenuItem item) {
            this.item = item;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus && item != null) {
                item.collapseActionView();
                if (v instanceof SearchView) {
                    ((SearchView) v).setQuery("", false);
                }
            }
        }

    }

}
