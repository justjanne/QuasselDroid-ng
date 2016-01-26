package de.kuschku.quasseldroid_ng.ui;

import android.support.annotation.StyleRes;

import de.kuschku.quasseldroid_ng.R;

public enum Themes {
    QUASSEL(R.style.Quassel),
    MATERIAL_DARK(R.style.Material_Dark),
    MATERIAL_LIGHT(R.style.Material_Light);

    public final int themeId;

    Themes(@StyleRes int themeId) {
        this.themeId = themeId;
    }
}
