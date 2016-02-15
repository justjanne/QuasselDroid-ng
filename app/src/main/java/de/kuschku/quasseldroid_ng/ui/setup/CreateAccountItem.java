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

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.materialdrawer.model.AbstractDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import de.kuschku.quasseldroid_ng.R;

public class CreateAccountItem extends AbstractDrawerItem<CreateAccountItem, CreateAccountItem.ViewHolder> {
    protected Pair<Integer, ColorStateList> colorStateList;

    @Override
    public int getType() {
        return R.id.item_create_account;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.widget_core_account_add;
    }

    @Override
    public void bindView(ViewHolder viewHolder) {
        //set the identifier from the drawerItem here. It can be used to run tests
        viewHolder.itemView.setId(hashCode());

        //set the item selected if it is
        viewHolder.itemView.setSelected(isSelected());

        //for android API 17 --> Padding not applied via xml
        DrawerUIUtils.setDrawerVerticalPadding(viewHolder.view);

        //call the onPostBindView method to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, viewHolder.itemView);
    }

    @Override
    public ViewHolderFactory getFactory() {
        return new ItemFactory();
    }

    /**
     * helper method to decide for the correct color
     *
     * @param ctx
     * @return
     */
    protected int getColor(Context ctx) {
        int color;
        if (this.isEnabled()) {
            color = com.mikepenz.materialdrawer.R.color.material_drawer_primary_text;
        } else {
            color = com.mikepenz.materialdrawer.R.color.material_drawer_hint_text;
        }
        return color;
    }

    /**
     * helper method to decide for the correct color
     *
     * @param ctx
     * @return
     */
    protected int getSelectedTextColor(Context ctx) {
        return com.mikepenz.materialdrawer.R.color.material_drawer_selected_text;
    }

    /**
     * helper to get the ColorStateList for the text and remembering it so we do not have to recreate it all the time
     *
     * @param color
     * @param selectedTextColor
     * @return
     */
    protected ColorStateList getTextColorStateList(@ColorInt int color, @ColorInt int selectedTextColor) {
        if (colorStateList == null || color + selectedTextColor != colorStateList.first) {
            colorStateList = new Pair<>(color + selectedTextColor, DrawerUIUtils.getTextColorStateList(color, selectedTextColor));
        }

        return colorStateList.second;
    }

    public static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;

        private ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}
