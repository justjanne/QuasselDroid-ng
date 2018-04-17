package de.kuschku.quasseldroid.ui.coresettings.highlightlist

import android.support.annotation.StringRes
import de.kuschku.libquassel.quassel.syncables.interfaces.IHighlightRuleManager

data class HighlightNickTypeItem(
  val value: IHighlightRuleManager.HighlightNickType,
  @StringRes val name: Int
)
