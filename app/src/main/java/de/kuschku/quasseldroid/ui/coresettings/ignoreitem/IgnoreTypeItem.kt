package de.kuschku.quasseldroid.ui.coresettings.ignoreitem

import android.support.annotation.StringRes
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager

data class IgnoreTypeItem(val value: IgnoreListManager.IgnoreType, @StringRes val name: Int)
