package de.kuschku.quasseldroid.ui.coresettings.chatlist

import android.support.annotation.StringRes
import de.kuschku.libquassel.protocol.Buffer_Activity

data class MinimumActivityItem(val activity: Buffer_Activity, @StringRes val name: Int)
