package de.kuschku.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.Space;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.LinearLayout;

import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.ui.MaterialActionBarDrawerToggle;

public class DrawerUtils {
    @UiThread
    public static void initDrawer(Activity actvity, DrawerLayout layout, Toolbar toolbar, @StringRes int open_res, @StringRes int close_res) {
        ActionBarDrawerToggle actionBarDrawerToggle = new MaterialActionBarDrawerToggle(actvity, layout, toolbar, open_res, close_res);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actvity.getWindow().setStatusBarColor(actvity.getResources().getColor(android.R.color.transparent));
            layout.setStatusBarBackground(R.color.colorPrimaryDark);
        }
        layout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
}
