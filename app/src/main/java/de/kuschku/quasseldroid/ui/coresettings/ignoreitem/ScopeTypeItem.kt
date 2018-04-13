package de.kuschku.quasseldroid.ui.coresettings.ignoreitem

import android.support.annotation.StringRes
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager

data class ScopeTypeItem(val value: IgnoreListManager.ScopeType, @StringRes val name: Int)
