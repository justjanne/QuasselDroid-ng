package de.kuschku.quasseldroid.ui.coresettings.ignoreitem

import android.support.annotation.StringRes
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager

data class StrictnessTypeItem(val value: IgnoreListManager.StrictnessType, @StringRes val name: Int)
