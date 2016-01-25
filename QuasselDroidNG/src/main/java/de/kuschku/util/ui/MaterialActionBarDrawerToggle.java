package de.kuschku.util.ui;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

@UiThread
public class MaterialActionBarDrawerToggle extends ActionBarDrawerToggle {
    public MaterialActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout,
                                         Toolbar toolbar, @StringRes int openDrawerContentDescRes,
                                         @StringRes int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, 0);
    }
}
