package de.kuschku.quasseldroid.ui.coresettings.networkserver

import android.support.annotation.StringRes
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork

data class ProxyTypeItem(val value: INetwork.ProxyType, @StringRes val name: Int)
