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

package de.kuschku.util.ui.preferences;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.reflect.Field;

import de.kuschku.util.annotationbind.AutoBinder;

/**
 * @author Marc Holder Kluver (marchold), Aidan Follestad (afollestad)
 */
public class MaterialListPreference extends ListPreference {

    private Context context;
    private MaterialDialog mDialog;

    public MaterialListPreference(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        PrefUtil.setLayoutResource(context, this, attrs);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
            setWidgetLayoutResource(0);
    }

    @Override
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        if (mDialog != null)
            mDialog.setItems(entries);
    }

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    public ListView getListView() {
        if (getDialog() == null) return null;
        return ((MaterialDialog) getDialog()).getListView();
    }

    @Override
    protected void showDialog(Bundle state) {
        if (getEntries() == null || getEntryValues() == null) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array.");
        }

        int preselect = findIndexOfValue(getValue());
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(getDialogTitle())
                .icon(getDialogIcon())
                .dismissListener(this)
                .onAny((dialog, which) -> {
                    switch (which) {
                        default:
                            MaterialListPreference.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                            break;
                        case NEUTRAL:
                            MaterialListPreference.this.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
                            break;
                        case NEGATIVE:
                            MaterialListPreference.this.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                            break;
                    }
                })
                .backgroundColor(AutoBinder.obtainColor(de.kuschku.quasseldroid_ng.R.attr.colorBackgroundDialog, context.getTheme()))
                .negativeText(getNegativeButtonText())
                .items(getEntries())
                .autoDismiss(true) // immediately close the dialog after selection
                .itemsCallbackSingleChoice(preselect, (dialog, itemView, which, text) -> {
                    onClick(null, DialogInterface.BUTTON_POSITIVE);
                    if (which >= 0 && getEntryValues() != null) {
                        try {
                            Field clickedIndex = ListPreference.class.getDeclaredField("mClickedDialogEntryIndex");
                            clickedIndex.setAccessible(true);
                            clickedIndex.set(MaterialListPreference.this, which);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                });

        final View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.customView(contentView, false);
        } else {
            builder.content(getDialogMessage());
        }

        PrefUtil.registerOnActivityDestroyListener(this, this);

        mDialog = builder.build();
        if (state != null)
            mDialog.onRestoreInstanceState(state);
        onClick(mDialog, DialogInterface.BUTTON_NEGATIVE);
        mDialog.show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        PrefUtil.unregisterOnActivityDestroyListener(this, this);
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        Dialog dialog = getDialog();
        if (dialog == null || !dialog.isShowing()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = dialog.onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    // From DialogPreference
    private static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        boolean isDialogShowing;
        Bundle dialogBundle;

        public SavedState(Parcel source) {
            super(source);
            isDialogShowing = source.readInt() == 1;
            dialogBundle = source.readBundle(getClass().getClassLoader());
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isDialogShowing ? 1 : 0);
            dest.writeBundle(dialogBundle);
        }
    }
}
